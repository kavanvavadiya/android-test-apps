package com.ihrsachin.sensoreventmanageger

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences (
    context: Context
) {

    private  val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences> = applicationContext.createDataStore(
        name = "my_data_store"
    )

    //Flow is a type to represent asynchronous stream of data
    val authLoginToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_LOGIN]
        }

    suspend fun saveLoginAuthToken(authToken: String){
        //edit function is a suspending function so need to be called
        // inside a suspending function
        dataStore.edit {  preferences ->
            preferences[KEY_LOGIN] = authToken
        }
    }

    val authRegisterToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_REGISTER]
        }

    suspend fun saveRegisterAuthToken(authToken: String){
        //edit function is a suspending function so need to be called
        // inside a suspending function
        dataStore.edit {  preferences ->
            preferences[KEY_REGISTER] = authToken
        }
    }

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    companion object{
        private val KEY_LOGIN = preferencesKey<String>("key_login")
        private val KEY_REGISTER = preferencesKey<String>("key_register")
    }
}