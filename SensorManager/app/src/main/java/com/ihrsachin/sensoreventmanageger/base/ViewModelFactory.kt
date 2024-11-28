package com.ihrsachin.sensoreventmanageger.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ihrsachin.sensoreventmanageger.AuthViewModel
import com.ihrsachin.sensoreventmanageger.repository.AuthRepository


// creating a ViewModelFactory class to instantiate a ViewModel with non-default constructors

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T
            else -> throw java.lang.IllegalArgumentException("ViewModelClass Not found")
        }
    }
}