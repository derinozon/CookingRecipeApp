package com.agileavengers.cooking_recipes_android.data.model

data class LoggedInUser(
    val id: String,
    val username: String,
    val email: String,
    val roles: List<String>
)
data class UserRegisterData(
    val username: String,
	val email: String,
    val password: String
)

data class UserLoginData(
    val username: String,
    val password: String
)