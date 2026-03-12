package com.example.myfood.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfood.data.local.AppDatabase
import com.example.myfood.data.local.CategoryEntity
import com.example.myfood.data.local.MealEntity
import com.example.myfood.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Taille de page simulée
private const val PAGE_SIZE = 30

data class HomeUiState(
    val isLoading: Boolean = false,
    val meals: List<MealEntity> = emptyList(),
    val displayedMeals: List<MealEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val error: String? = null,
    val currentPage: Int = 1,
    val canLoadMore: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MealRepository(AppDatabase.getInstance(application))

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadMeals()
    }

    // -------------------------------------------------------
    // Chargement initial
    // -------------------------------------------------------

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().onSuccess { cats ->
                _uiState.value = _uiState.value.copy(categories = cats)
            }
        }
    }

    fun loadMeals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val query = _uiState.value.searchQuery
            val category = _uiState.value.selectedCategory

            val result = when {
                category != null -> repository.getMealsByCategory(category)
                query.isNotBlank() -> repository.searchMeals(query)
                else -> repository.getAllMeals()
            }

            result.onSuccess { meals ->
                val page = 1
                val displayed = meals.take(page * PAGE_SIZE)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    meals = meals,
                    displayedMeals = displayed,
                    currentPage = page,
                    canLoadMore = meals.size > displayed.size,
                    error = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Une erreur est survenue"
                )
            }
        }
    }

    // -------------------------------------------------------
    // Pagination simulée
    // -------------------------------------------------------

    fun loadNextPage() {
        val state = _uiState.value
        if (!state.canLoadMore || state.isLoading) return

        val nextPage = state.currentPage + 1
        val displayed = state.meals.take(nextPage * PAGE_SIZE)

        _uiState.value = state.copy(
            displayedMeals = displayed,
            currentPage = nextPage,
            canLoadMore = state.meals.size > displayed.size
        )
    }

    // -------------------------------------------------------
    // Recherche
    // -------------------------------------------------------

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            selectedCategory = null,
            currentPage = 1
        )
        loadMeals()
    }

    // -------------------------------------------------------
    // Filtre par catégorie
    // -------------------------------------------------------

    fun onCategorySelected(category: String?) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = if (_uiState.value.selectedCategory == category) null else category,
            searchQuery = "",
            currentPage = 1
        )
        loadMeals()
    }
}

