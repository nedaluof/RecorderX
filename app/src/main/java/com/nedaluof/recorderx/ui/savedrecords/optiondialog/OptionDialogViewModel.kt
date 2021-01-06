package com.nedaluof.recorderx.ui.savedrecords.optiondialog

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedaluof.recorderx.model.RecordX
import com.nedaluof.recorderx.model.Status
import com.nedaluof.recorderx.repository.RecorderXRepository
import kotlinx.coroutines.launch

/**
 * Created by nedaluof on 11/17/2020.
 */
class OptionDialogViewModel @ViewModelInject constructor(
		private val repository: RecorderXRepository
) : ViewModel() {

		private val _deleted = MutableLiveData<Boolean>()
		val deleted: LiveData<Boolean>
				get() = _deleted

		private val _errorDelete = MutableLiveData<String>()
		val errorInDelete: LiveData<String>
				get() = _errorDelete

		fun deleteRecordX(recordx: RecordX) {
				viewModelScope.launch {
						val result = repository.deleteRecordX(recordx)
						when (result.status) {
								Status.SUCCESS -> _deleted.postValue(result.data)
								Status.ERROR -> {
										_deleted.postValue(false)
										_errorDelete.postValue(result.message)
								}
						}
				}
		}

		private val _renamed = MutableLiveData<Boolean>()
		val renamed: LiveData<Boolean>
				get() = _renamed


		private val _errorUpdate = MutableLiveData<String>()
		val errorInRename: LiveData<String>
				get() = _errorUpdate

		fun renameRecord(recordx: RecordX) {
				viewModelScope.launch {
						val result = repository.updateRecordX(recordx)
						when (result.status) {
								Status.SUCCESS -> _renamed.postValue(result.data)
								Status.ERROR -> {
										_renamed.postValue(false)
										_errorUpdate.postValue(result.message)
								}
						}
				}
		}
}
