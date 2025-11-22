package com.learning.workout__android.viewModel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learning.workout__android.data.AppDatabase
import com.learning.workout__android.data.models.RecordModel
import com.learning.workout__android.data.repositories.RecordsRepository
import com.learning.workout__android.utils.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecordsViewModel(
    private val recordsRepository: RecordsRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    val records = recordsRepository.getAllRecords().distinctUntilChanged()

    private var _recordToEdit = MutableStateFlow<RecordModel?>(null)
    private var _sortState = MutableStateFlow(loadSortState())

    fun setRecordToEdit(record: RecordModel?) {
        _recordToEdit.value = record
    }

    fun deleteRecord(record: RecordModel) {
        viewModelScope.launch {
            recordsRepository.deleteRecord(record)
        }
    }

    fun updateRecord(record: RecordModel) {
        viewModelScope.launch {
            recordsRepository.updateRecord(record)
        }
    }

    fun createRecord(record: RecordModel) {
        viewModelScope.launch {
            recordsRepository.createRecord(record)
        }
    }

    private fun loadSortState(): SortState {
        val savedField =
            sharedPreferences.getString(SharedPrefsKeys.SORT_FIELD.name, SortField.DATE.name)
        val savedOrder =
            sharedPreferences.getString(SharedPrefsKeys.SORT_ORDER.name, SortOrder.DESC.name)

        val field = runCatching {
            SortField.valueOf(savedField ?: SortField.DATE.name)
        }.getOrDefault(SortField.DATE)

        val order = runCatching {
            SortOrder.valueOf(savedOrder ?: SortOrder.DESC.name)
        }.getOrDefault(SortOrder.DESC)

        return SortState(field = field, order = order)
    }

    fun onSortChange(field: SortField) {
        val currentState = _sortState.value

        val newSortState = when {
            // Different field clicked: sort by new field in ASC order
            currentState.field != field -> SortState(field = field, order = SortOrder.ASC)

            // Same field clicked: toggle through ASC -> DESC -> ASC
            currentState.order == SortOrder.ASC -> SortState(field = field, order = SortOrder.DESC)
            else -> SortState(field = field, order = SortOrder.ASC)
        }

        _sortState.value = newSortState
        saveSortState(newSortState)
    }

    private fun saveSortState(sortState: SortState) {
        sharedPreferences.edit().apply {
            putString(SharedPrefsKeys.SORT_FIELD.name, sortState.field.name)
            putString(SharedPrefsKeys.SORT_ORDER.name, sortState.order.name)
            apply()
        }
    }

    private fun getSortedRecords(
        records: List<RecordModel>,
        sortState: SortState
    ): List<RecordModel> {
        return when (sortState.order) {
            SortOrder.ASC -> records.sortedWith(compareBy {
                getComparableValue(
                    it,
                    sortState.field
                )
            })

            SortOrder.DESC -> records.sortedWith(compareByDescending {
                getComparableValue(
                    it,
                    sortState.field
                )
            })
        }
    }

    private fun getComparableValue(record: RecordModel, field: SortField): Comparable<*> {
        return when (field) {
            SortField.EXERCISE -> record.name.lowercase()
            SortField.RESULT -> record.count
            SortField.DATE -> record.createdAt.toString()
        }
    }

    val uiState = combine(
        records,
        _recordToEdit,
        _sortState
    ) { records, recordToEdit, sortState ->
        val sortedRecords = getSortedRecords(records, sortState)
        RecordsUiState(
            records = LoadState.Success(sortedRecords),
            recordToEdit = recordToEdit,
            sortState = sortState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        RecordsUiState()
    )

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getDatabase(context)
                val recordsRepository = RecordsRepository(recordDao = db.recordDao())
                val sharedPreferences =
                    context.getSharedPreferences(SharedPrefName, Context.MODE_PRIVATE)

                RecordsViewModel(recordsRepository, sharedPreferences)
            }
        }
    }
}

data class RecordsUiState(
    val records: LoadState<List<RecordModel>> = LoadState.Loading,
    val recordToEdit: RecordModel? = null,
    val sortState: SortState = SortState()
)

enum class SortField {
    EXERCISE, RESULT, DATE
}

enum class SortOrder {
    ASC, DESC
}

data class SortState(
    val field: SortField = SortField.DATE,
    val order: SortOrder = SortOrder.DESC
)

const val SharedPrefName = "records_prefs"

enum class SharedPrefsKeys {
    SORT_FIELD,
    SORT_ORDER
}