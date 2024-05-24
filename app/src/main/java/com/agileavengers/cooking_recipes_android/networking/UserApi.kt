package com.agileavengers.cooking_recipes_android.networking
import com.agileavengers.cooking_recipes_android.data.model.LoggedInUser
import com.agileavengers.cooking_recipes_android.data.model.UserLoginData
import com.agileavengers.cooking_recipes_android.data.model.UserRegisterData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("/api/auth/register")
    suspend fun register(
        @Body user: UserRegisterData
    ): ResponseBody

    @POST("/api/auth/login")
    suspend fun login(
        @Body user: UserLoginData
    ): Response<LoggedInUser>
}
