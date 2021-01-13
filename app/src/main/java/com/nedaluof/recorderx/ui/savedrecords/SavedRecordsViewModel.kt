package com.nedaluof.recorderx.ui.savedrecords

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedaluof.recorderx.model.RecordX
import com.nedaluof.recorderx.repository.RecorderXRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SavedRecordsViewModel @ViewModelInject constructor(
		private val repository: RecorderXRepository,
) : ViewModel() {

		private val _records = MutableLiveData<List<RecordX>>()
		val records: LiveData<List<RecordX>>
				get() = _records

		private val _loading = MutableLiveData<Boolean>()
		val loading: LiveData<Boolean>
				get() = _loading

		private val _error = MutableLiveData<String>()
		val error: LiveData<String>
				get() = _error


		fun getRecords() {
				_loading.value = true
				viewModelScope.launch {
						repository.getAllRecordX()
								.catch { _error.postValue(it.message!!) }
								.collect {
										_loading.postValue(false)
										_records.postValue(it)
								}
				}
		}
}
