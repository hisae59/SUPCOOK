package com.example.myfood.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object MealApi {

    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1"

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.NONE
        }
    }

    suspend fun searchMeals(query: String): SearchResponse {
        return client.get("$BASE_URL/search.php") {
            parameter("s", query)
        }.body()
    }

    suspend fun getMealById(id: String): LookupResponse {
        return client.get("$BASE_URL/lookup.php") {
            parameter("i", id)
        }.body()
    }

    suspend fun getCategories(): CategoriesResponse {
        return client.get("$BASE_URL/categories.php").body()
    }

    suspend fun filterByCategory(category: String): FilterResponse {
        return client.get("$BASE_URL/filter.php") {
            parameter("c", category)
        }.body()
    }
}

