package com.ihrsachin.sensoreventmanageger.utility

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ihrsachin.sensoreventmanageger.LoginFragment
import com.ihrsachin.sensoreventmanageger.base.BaseFragment
import com.ihrsachin.sensoreventmanageger.network.Resource

fun<A : Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this, activity).also {
        // Following flags ensure that the activity you're starting becomes the root activity of a new task
        // And discarding any old tasks that may have been present

        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible : Boolean){
    visibility = if(isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean){
    isEnabled = enabled
    alpha = if(enabled) 1f else 0.5f
}

fun View.snackbar(message: String, action: (() -> Unit)? = null){
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let{
        snackbar.setAction(("Retry")){
            it()
        }
    }
    snackbar.show()
}
fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
){
    when{
        failure.isNetworkError -> requireView().snackbar("Please check your internet connection", retry)
        failure.errorCode == 401 -> {
            if(this is LoginFragment){
                requireView().snackbar("You've entered incorrect email or password")
            }else{
                //logout operation
                (this as BaseFragment<*, *, *>).logout()
            }
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbar(error)
        }
    }
}