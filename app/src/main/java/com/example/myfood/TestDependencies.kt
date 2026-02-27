package com.example.myfood

// Test pour vérifier que les dépendances Ktor et Room sont correctement installées

// Imports Ktor
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// Imports Room
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

/**
 * Classe de test pour vérifier que Ktor fonctionne
 */
class KtorClientTest {
    fun createHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
}

/**
 * Entité Room de test
 */
@Entity(tableName = "test_table")
data class TestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)

/**
 * Database Room de test
 */
@Database(entities = [TestEntity::class], version = 1)
abstract class TestDatabase : RoomDatabase()

