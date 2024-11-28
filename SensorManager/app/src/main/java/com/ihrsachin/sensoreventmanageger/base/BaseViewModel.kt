package com.ihrsachin.sensoreventmanageger.base

import androidx.lifecycle.ViewModel
import com.ihrsachin.sensoreventmanageger.network.UserApi

abstract class BaseViewModel(
    private val  repository: BaseRepository
): ViewModel() {

        suspend fun logout(api: UserApi) = repository.logout(api)
}