package com.example.myfood.data.repository

import com.example.myfood.data.local.AppDatabase
import com.example.myfood.data.local.CategoryEntity
import com.example.myfood.data.local.MealDetailEntity
import com.example.myfood.data.local.MealEntity
import com.example.myfood.data.remote.MealApi
import com.example.myfood.data.remote.MealDetailDto

// Durée du cache : 10 minutes
private const val CACHE_TTL_MS = 10 * 60 * 1000L

class MealRepository(private val db: AppDatabase) {

    private val dao = db.mealDao()

    // -------------------------------------------------------
    // Catégories
    // -------------------------------------------------------

    suspend fun getCategories(): Result<List<CategoryEntity>> {
        return try {
            // Vérifier la fraîcheur du cache
            val oldest = dao.getOldestCategoryTimestamp()
            val isCacheValid = oldest != null && (System.currentTimeMillis() - oldest) < CACHE_TTL_MS

            if (!isCacheValid) {
                val remote = MealApi.getCategories().categories ?: emptyList()
                val entities = remote.map { dto ->
                    CategoryEntity(id = dto.id, name = dto.name, thumbnail = dto.thumbnail)
                }
                dao.clearCategories()
                dao.insertCategories(entities)
            }

            Result.success(dao.getAllCategories())
        } catch (e: Exception) {
            // En cas d'erreur réseau → utiliser le cache local
            val cached = dao.getAllCategories()
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    // -------------------------------------------------------
    // Recherche de recettes
    // -------------------------------------------------------

    suspend fun searchMeals(query: String): Result<List<MealEntity>> {
        return try {
            val remote = MealApi.searchMeals(query).meals ?: emptyList()
            val entities = remote.map { dto ->
                MealEntity(
                    id = dto.id,
                    name = dto.name,
                    category = dto.category,
                    area = dto.area,
                    thumbnail = dto.thumbnail
                )
            }
            // Mettre à jour le cache pour ces résultats
            dao.insertMeals(entities)
            Result.success(entities)
        } catch (e: Exception) {
            // Fallback : chercher dans le cache local
            val cached = dao.searchMeals(query)
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    // -------------------------------------------------------
    // Filtrer par catégorie
    // -------------------------------------------------------

    suspend fun getMealsByCategory(category: String): Result<List<MealEntity>> {
        return try {
            val remote = MealApi.filterByCategory(category).meals ?: emptyList()
            val entities = remote.map { dto ->
                MealEntity(
                    id = dto.id,
                    name = dto.name,
                    category = category,
                    area = dto.area,
                    thumbnail = dto.thumbnail
                )
            }
            dao.insertMeals(entities)
            Result.success(entities)
        } catch (e: Exception) {
            val cached = dao.getMealsByCategory(category)
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    // -------------------------------------------------------
    // Toutes les recettes (recherche vide = "a")
    // -------------------------------------------------------

    suspend fun getAllMeals(): Result<List<MealEntity>> {
        return try {
            val oldest = dao.getOldestMealTimestamp()
            val isCacheValid = oldest != null && (System.currentTimeMillis() - oldest) < CACHE_TTL_MS

            if (!isCacheValid) {
                // L'API ne supporte pas la liste complète, on charge avec "a" comme default
                val remote = MealApi.searchMeals("").meals ?: emptyList()
                val entities = remote.map { dto ->
                    MealEntity(
                        id = dto.id,
                        name = dto.name,
                        category = dto.category,
                        area = dto.area,
                        thumbnail = dto.thumbnail
                    )
                }
                dao.clearMeals()
                dao.insertMeals(entities)
            }

            Result.success(dao.getAllMeals())
        } catch (e: Exception) {
            val cached = dao.getAllMeals()
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    // -------------------------------------------------------
    // Détail d'une recette
    // -------------------------------------------------------

    suspend fun getMealDetail(id: String): Result<MealDetailEntity> {
        return try {
            // Vérifier le cache
            val cached = dao.getMealDetail(id)
            val isCacheValid = cached != null &&
                (System.currentTimeMillis() - cached.cachedAt) < CACHE_TTL_MS

            if (isCacheValid && cached != null) {
                return Result.success(cached)
            }

            val remote = MealApi.getMealById(id).meals?.firstOrNull()
                ?: return Result.failure(Exception("Recette introuvable"))

            val entity = remote.toEntity()
            dao.insertMealDetail(entity)
            Result.success(entity)
        } catch (e: Exception) {
            val cached = dao.getMealDetail(id)
            if (cached != null) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }
}

// -------------------------------------------------------
// Extension : convertir MealDetailDto → MealDetailEntity
// -------------------------------------------------------

private fun MealDetailDto.toEntity(): MealDetailEntity {
    val ingredients = getIngredients()
    val ingredientsJson = ingredients.joinToString(";") { (ing, mes) -> "$ing|$mes" }
    return MealDetailEntity(
        id = id,
        name = name,
        category = category,
        area = area,
        thumbnail = thumbnail,
        instructions = instructions,
        ingredientsJson = ingredientsJson
    )
}

fun MealDetailEntity.parseIngredients(): List<Pair<String, String>> {
    if (ingredientsJson.isBlank()) return emptyList()
    return ingredientsJson.split(";").mapNotNull { entry ->
        val parts = entry.split("|")
        if (parts.size == 2) parts[0] to parts[1] else null
    }
}

