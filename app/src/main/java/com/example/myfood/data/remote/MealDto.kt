package com.example.myfood.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealSummaryDto(
    @SerialName("idMeal") val id: String,
    @SerialName("strMeal") val name: String,
    @SerialName("strCategory") val category: String? = null,
    @SerialName("strArea") val area: String? = null,
    @SerialName("strMealThumb") val thumbnail: String? = null
)

@Serializable
data class MealDetailDto(
    @SerialName("idMeal") val id: String,
    @SerialName("strMeal") val name: String,
    @SerialName("strCategory") val category: String? = null,
    @SerialName("strArea") val area: String? = null,
    @SerialName("strInstructions") val instructions: String? = null,
    @SerialName("strMealThumb") val thumbnail: String? = null,
    @SerialName("strIngredient1") val ingredient1: String? = null,
    @SerialName("strIngredient2") val ingredient2: String? = null,
    @SerialName("strIngredient3") val ingredient3: String? = null,
    @SerialName("strIngredient4") val ingredient4: String? = null,
    @SerialName("strIngredient5") val ingredient5: String? = null,
    @SerialName("strIngredient6") val ingredient6: String? = null,
    @SerialName("strIngredient7") val ingredient7: String? = null,
    @SerialName("strIngredient8") val ingredient8: String? = null,
    @SerialName("strIngredient9") val ingredient9: String? = null,
    @SerialName("strIngredient10") val ingredient10: String? = null,
    @SerialName("strIngredient11") val ingredient11: String? = null,
    @SerialName("strIngredient12") val ingredient12: String? = null,
    @SerialName("strIngredient13") val ingredient13: String? = null,
    @SerialName("strIngredient14") val ingredient14: String? = null,
    @SerialName("strIngredient15") val ingredient15: String? = null,
    @SerialName("strMeasure1") val measure1: String? = null,
    @SerialName("strMeasure2") val measure2: String? = null,
    @SerialName("strMeasure3") val measure3: String? = null,
    @SerialName("strMeasure4") val measure4: String? = null,
    @SerialName("strMeasure5") val measure5: String? = null,
    @SerialName("strMeasure6") val measure6: String? = null,
    @SerialName("strMeasure7") val measure7: String? = null,
    @SerialName("strMeasure8") val measure8: String? = null,
    @SerialName("strMeasure9") val measure9: String? = null,
    @SerialName("strMeasure10") val measure10: String? = null,
    @SerialName("strMeasure11") val measure11: String? = null,
    @SerialName("strMeasure12") val measure12: String? = null,
    @SerialName("strMeasure13") val measure13: String? = null,
    @SerialName("strMeasure14") val measure14: String? = null,
    @SerialName("strMeasure15") val measure15: String? = null
) {
    fun getIngredients(): List<Pair<String, String>> {
        val ingredients = listOf(
            ingredient1 to measure1, ingredient2 to measure2, ingredient3 to measure3,
            ingredient4 to measure4, ingredient5 to measure5, ingredient6 to measure6,
            ingredient7 to measure7, ingredient8 to measure8, ingredient9 to measure9,
            ingredient10 to measure10, ingredient11 to measure11, ingredient12 to measure12,
            ingredient13 to measure13, ingredient14 to measure14, ingredient15 to measure15
        )
        return ingredients
            .filter { (ing, _) -> !ing.isNullOrBlank() }
            .map { (ing, mes) -> ing!! to (mes ?: "") }
    }
}

@Serializable
data class CategoryDto(
    @SerialName("idCategory") val id: String,
    @SerialName("strCategory") val name: String,
    @SerialName("strCategoryThumb") val thumbnail: String? = null
)

@Serializable
data class SearchResponse(
    @SerialName("meals") val meals: List<MealSummaryDto>? = null
)

@Serializable
data class LookupResponse(
    @SerialName("meals") val meals: List<MealDetailDto>? = null
)

@Serializable
data class CategoriesResponse(
    @SerialName("categories") val categories: List<CategoryDto>? = null
)

@Serializable
data class FilterResponse(
    @SerialName("meals") val meals: List<MealSummaryDto>? = null
)

