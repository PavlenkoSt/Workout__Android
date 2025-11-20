package com.learning.workout__android.viewModel

import android.content.Context
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
    private val recordsRepository: RecordsRepository
) : ViewModel() {
    val records = recordsRepository.getAllRecords().distinctUntilChanged()

    private var _recordToEdit = MutableStateFlow<RecordModel?>(null)

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

    val uiState = combine(
        records,
        _recordToEdit
    ) { records, recordToEdit ->
        RecordsUiState(
            records = LoadState.Success(records),
            recordToEdit = recordToEdit
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

                RecordsViewModel(recordsRepository)
            }
        }
    }
}

data class RecordsUiState(
    val records: LoadState<List<RecordModel>> = LoadState.Loading,
    val recordToEdit: RecordModel? = null
)