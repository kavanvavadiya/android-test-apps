//============================================================================
// Name        : ThreadPool.h
// Author      : Auro
// Version     :
// Copyright   :
// Description : Thread Pool library
//============================================================================

#ifndef THREAD_POOL_H
#define THREAD_POOL_H

#include<thread>
#include<future>
#include<vector>
#include<functional>
#include<queue>
#include<mutex>
#include"bad_thread.h"

using namespace std;

/**
 * Used to create a pool of persistent threads that last until the ThreadPool variable goes out of scope or
 * shutdown() or join() is called.
 */
class ThreadPool{
private:

	//No of threads under pool
	unsigned short workers;

	//vector to hold all the threads under pool
	vector<thread> worker_threads;

	//terminate_pool signals the threads to break out of InfiniteLoopFunction().
	bool terminate_pool;

	//isJoinable evaluates to false if join or shutdown() was already called on the Thread Pool.
	//No mutex required. Only accessed via calling thread.
	bool isJoinable;

	//Condition variable to notify the threads about pending work
	condition_variable condition;

	//queue that hold the functions/Function Objects/ lambdas to be executed.
	queue<function<void()>>  tasks;

	//Mutex that protects access to terminate_pool, condition and tasks.
	mutex threadPoolMutex;

	/**
	 * Keeps the threads active until terminated.
	 */
	void InfiniteLoopFunction(){
		function<void()> func;
		while(true){
			{
				unique_lock<mutex> lock(threadPoolMutex);

				//This condition is checked on the first iteration of the InfiniteLoopFunction
				//and whenever condition variable is notified. Condition variable is notified as
				//soon as a function is pushed to tasks. The predicate guarantees that lost wakeup
				//or spurious wakeup does not cause any problems.
				//In the wildest of scenarios, if a double lost wakeup takes place, then
				//there would always be a task in the function that was not executed.(I can
				//safely neglect this possibility as it would probably never happen).
				condition.wait(lock,[this](){
					return (!tasks.empty() || terminate_pool);
				});

				if(terminate_pool){
					//At this point the tasks queue is empty and we need to break out of this loop.
					break;
				}

				func = tasks.front();
				tasks.pop();
			}
			func();
		}
	}

public:
	/**
	 * Default parameterized constructor.
	 */
	explicit ThreadPool(unsigned short n) : workers{n},terminate_pool{false},isJoinable{true}{
		for(int i=0; i<workers;i++){
			worker_threads.emplace_back(thread(&ThreadPool::InfiniteLoopFunction,this));
		}
		//Log here
	}

	//Thread Pools should not be copy constructible or copy assignable.
	ThreadPool(const ThreadPool&) = delete;
	ThreadPool& operator=(const ThreadPool&) = delete;

	//I see no use cases where move semantics would be  required.
	ThreadPool(ThreadPool&&) = delete;
	ThreadPool& operator=(ThreadPool&&) = delete;

	/**
	 * Destructor automatically checks if the ThreadPool was joinable. If it was then
	 * shutdown() is automatically called.
	 */
	~ThreadPool(){
		if(isJoinable){
			shutdown();
		}
		//Log here
	}

	/**
	 * Can post lambdas, Function Objects as well as functions. The syntax is exactly the same
	 * as if passing to std::thread. There is one exception to this rule.
	 * The callable is internally bound to a  function<void()> so the type cannot be moveable.
	 * Thus you cannot pass parameters using move semantics (rvalue references).
	 * Throws bad_thread exception if shutdown or join was already called.
	 */
	template<typename Function, typename ...Args>
	void post(Function&& f, Args&&... args){
		if(isJoinable){
			function<void()> func = bind(forward<Function>(f),forward<Args>(args)...);
			{
				unique_lock<mutex> lock(threadPoolMutex);
				tasks.push(func);
			}
			condition.notify_one();
		}else{
			bad_thread e("Cannot call post() on a thread that is no longer joinable.");
			throw e;
		}
	}

	/**
	 * Works exactly the same as post(). But with callables that return a value. The callable is
	 * internally bound to a std::packaged_task and a future is returned. User can access the return
	 * value of the callable once by calling the get() method of std:::future.
	 * The calling thread will not block unless get() is called on the future returned by the submit().
	 */
	template<typename Function, typename ...Args>
	auto submit(Function&& f, Args&&... args) -> future<decltype(f(args...))> {

		if(!isJoinable){
			bad_thread e("Cannot call submit() on a thread that is no longer joinable");
			throw e;
		}

		// Create a function with bounded parameters ready to execute
		function<decltype(f(args...))()> func = bind(forward<Function>(f), forward<Args>(args)...);
		// Encapsulate it into a shared ptr in order to be able to copy construct / assign
		auto task_ptr = make_shared<packaged_task<decltype(f(args...))()>>(func);

		// Wrap packaged task into void function
		function<void()> wrapper_func = [task_ptr]() {
		  (*task_ptr)();
		};

		{
			unique_lock<mutex> lock(threadPoolMutex);
			tasks.push(wrapper_func);
		}

		condition.notify_one();

		// Return future from promise
		return task_ptr->get_future();
	 }

	/**
	 * Completes the current jobs under execution and terminates the threads in Pool.
	 * Whenever we call shutdown there are two possibilities:
	 * 1. thread(s) in pool is currently performing a job and the threadPoolMutex is unlocked.
	 * 2. There is no task under execution and the thread(s) is waiting for notification
	 * 	  of the condition variable. The threadPoolMutex is unlocked but the thread(s) is blocked(can't join it).
	 *
	 * In case 1, we simply  acquire the threadPoolMutex, pop all the tasks and join the thread(s).
	 *
	 * In case 2, also acquire the threadPoolMutex and pop all the tasks.
	 * Then we notify the condition variable by terminate_thread
	 *
	 * Calls bad_thread exception if the Thread Pool was no longer joinable.
	 */
	void shutdown(){
		if(!isJoinable){	//if join or shutdown was already called on this thread
			bad_thread e("Cannot call shutdown() on a Thread Pool that is no longer joinable.");
			throw e;
		}
		isJoinable = false;

		{
			unique_lock<mutex> lock(threadPoolMutex);

			while(!tasks.empty()){
				//Access gained when tasks is not empty.
				//A particular task would be under execution by the thread as of this moment.
				//We empty  out the rest of the tasks meaning on freeing of this mutex,
				//there would be no tasks to execute.
				tasks.pop();
			}

			terminate_pool = true;

			condition.notify_all();
		}

		//Ensure completion of current task(s) under execution
		//Thread(s) must be joined otherwise they would be terminated.
		for(auto it = begin(worker_threads); it != end(worker_threads); it++){
			if(it->joinable()){
				it->join();
			}
		}
	}

	/**
	 * Executes all the pending tasks in queue. The calling thread is blocked until all
	 * tasks in queue are executed.
	 * Throws bad_thread exception if shutdown() or join() was already called.
	 */
	void join(){
		if(!isJoinable){	//if join or shutdown was already called on this thread
			bad_thread e("Cannot call join() on a Thread Pool that is no longer joinable.");
			throw e;
		}
		isJoinable = false;

		while(true){	//blocks the caller thread until tasks is empty.
			{
				unique_lock<mutex> lock(threadPoolMutex);
				if(tasks.empty()){
					terminate_pool = true;
					break;
				}
			}	//free the lock to avoid deadlock
		}

		condition.notify_all();

		//Ensure completion of current task under execution
		//Thread must be joined otherwise it would be terminated.
		for(auto it = begin(worker_threads); it != end(worker_threads); it++){
			if(it->joinable()){
				it->join();
			}
		}
	}
};

#endif
