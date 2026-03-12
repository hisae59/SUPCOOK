package com.example.myfood.data.local

import androidx.room.*

@Dao
interface MealDao {

    // --- Meals (résultats de recherche / filtre) ---

    @Query("SELECT * FROM meals WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchMeals(query: String): List<MealEntity>

    @Query("SELECT * FROM meals WHERE category = :category ORDER BY name ASC")
    suspend fun getMealsByCategory(category: String): List<MealEntity>

    @Query("SELECT * FROM meals ORDER BY name ASC")
    suspend fun getAllMeals(): List<MealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealEntity>)

    @Query("DELETE FROM meals")
    suspend fun clearMeals()

    // --- Meal Details ---

    @Query("SELECT * FROM meal_details WHERE id = :id")
    suspend fun getMealDetail(id: String): MealDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealDetail(detail: MealDetailEntity)

    // --- Categories ---

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    // --- Cache validity ---

    @Query("SELECT MIN(cachedAt) FROM meals")
    suspend fun getOldestMealTimestamp(): Long?

    @Query("SELECT MIN(cachedAt) FROM categories")
    suspend fun getOldestCategoryTimestamp(): Long?
}

