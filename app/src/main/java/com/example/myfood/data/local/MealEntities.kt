package com.example.myfood.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val thumbnail: String?,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "meal_details")
data class MealDetailEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val thumbnail: String?,
    val instructions: String?,
    val ingredientsJson: String, // JSON sérialisé de la liste d'ingrédients
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val thumbnail: String?,
    val cachedAt: Long = System.currentTimeMillis()
)

