package com.agileavengers.cooking_recipes_android.data

import android.util.Log
import com.agileavengers.cooking_recipes_android.data.model.LoggedInUser
import com.agileavengers.cooking_recipes_android.data.model.UserLoginData
import com.agileavengers.cooking_recipes_android.data.model.UserRegisterData
import com.agileavengers.cooking_recipes_android.networking.RetrofitProvider
import com.agileavengers.cooking_recipes_android.networking.UserApi
import java.io.IOException

// Class that handles authentication w/ login credentials and retrieves user information.
class LoginDataSource {

	private val userApi = RetrofitProvider.retrofit.create(UserApi::class.java)

	// Logs in the user with given credentials
	suspend fun login(username: String, password: String): Result<LoggedInUser> {
		val data = UserLoginData(username, password);
		val apiResult = userApi.login(data).body();

		if (apiResult != null) {
			return Result.Success(apiResult)
		}
		else {
			return Result.Error(IOException("Error logging in"))
		}
	}

	// Registers the user with given credentials
	suspend fun register(email: String, username: String, password: String): Result<LoggedInUser> {
		val data = UserRegisterData(username, email, password);
		val apiResult = userApi.register(data);
		if (apiResult.string() == "{\"message\":\"User registered successfully!\"}") {
			return login(username, password)
		}
		else {
			return Result.Error(IOException("Error registering user"))
		}
	}

	// Generates a fake user for testing purposes
	fun fake_user (username: String): Result<LoggedInUser> {
		try {
			val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username, "email", listOf("ROLE_USER"))
			return Result.Success(fakeUser)
		} catch (e: Throwable) {
			return Result.Error(IOException("Error logging in", e))
		}
	}
}