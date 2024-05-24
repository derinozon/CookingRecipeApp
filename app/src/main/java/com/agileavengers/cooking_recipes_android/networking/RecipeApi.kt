package com.agileavengers.cooking_recipes_android.networking
import com.agileavengers.cooking_recipes_android.data.model.Category
import com.agileavengers.cooking_recipes_android.data.model.Recipe
import com.agileavengers.cooking_recipes_android.data.model.Type

import okhttp3.ResponseBody
import retrofit2.http.*

interface RecipeApi {
    @GET("/api/recipes")
    suspend fun getRecipes(
        @Query("types") types: List<String>?,
        @Query("categories") categories: List<String>?,
        @Query("preparationTime") preparationTime: Int?
    ): List<Recipe>

    @GET("/api/recipes/search")
    suspend fun searchRecipes(
        @Query("searchPhrase") searchPhrase: String
    ): List<Recipe>

    @GET("/api/recipes/{id}")
    suspend fun getRecipe(
        @Path("id") id: String
    ): Recipe

    @GET("/api/recipes/{id}/pdf")
    suspend fun getRecipePdf(
        @Path("id") id: String
    ): ResponseBody

    @POST("/api/recipes")
    suspend fun createRecipe(
        @Body recipe: Recipe
    ): Void

    @PUT("/api/recipes/{id}")
    suspend fun updateRecipe(
        @Path("id") id: String,
        @Body recipe: Recipe
    ): Void

    @DELETE("/api/recipes/{id}")
    suspend fun deleteRecipe(
        @Path("id") id: String
    ): Void

    @GET("/api/categories")
    suspend fun getCategories(): List<Category>?

    @GET("/api/categories/{name}")
    suspend fun getCategoryByName(@Path("name") name: String): Category

}
