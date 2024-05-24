package com.agileavengers.cooking_recipes_android.data

import com.agileavengers.cooking_recipes_android.data.model.LoggedInUser

// Class that requests authentication and user information from the remote data source and maintains an in-memory cache of login status and user credentials information.
class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    suspend fun register(email: String, username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.register(email, username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }
}