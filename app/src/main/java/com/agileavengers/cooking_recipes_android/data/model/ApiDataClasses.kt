package com.agileavengers.cooking_recipes_android.data.model

import java.io.Serializable

data class Type(
    val name: String,
    val imageURL: String
): Serializable {}

data class Image(
    val id: String,
    val name: String,
    val url: String
)
data class User(
    val username: String,
    val email: String,
    val password: String,
): Serializable{}

data class LoginRequest(
    val username: String,
    val password: String
)

data class TokenResponse(
    val token: String
)

