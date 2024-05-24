package com.agileavengers.cooking_recipes_android.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String,
    val displayMail: String
)