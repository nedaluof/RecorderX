package com.nedaluof.recorderx.ui.savedrecords.optiondialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.nedaluof.recorderx.R
import com.nedaluof.recorderx.databinding.DialogRecordOptionBinding
import com.nedaluof.recorderx.model.RecordX
import com.nedaluof.recorderx.ui.savedrecords.playrecord.RecorderXPlayerBottomSheet
import com.nedaluof.recorderx.util.getAppPath
import com.nedaluof.recorderx.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import timber.log.Timber

/**
 * Created by nedaluof on 11/17/2020.
 */

@AndroidEntryPoint
class OptionDialog : DialogFragment() {

		//view binding
		private var _binding: DialogRecordOptionBinding? = null
		private val binding: DialogRecordOptionBinding
				get() = _binding!!

		//option dialog view model
		private val viewModel: OptionDialogViewModel by viewModels()
		override fun onCreateView(
				inflater: LayoutInflater,
				container: ViewGroup?,
				savedInstanceState: Bundle?
		): View {
				_binding = DialogRecordOptionBinding.inflate(inflater, container, false)
				return binding.root
		}

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
				super.onViewCreated(view, savedInstanceState)
				val comingRecord = arguments?.getParcelable<RecordX>(RECORDX_KEY)!!

				binding.deleteBtn.setOnClickListener {
						//Todo: move to mainRepository
						val filePath = File(comingRecord.filePath)
						val result = filePath.delete()
						if (result) {
								deleteRecord(comingRecord)
						} else {
								activity?.toast(R.string.alert_delete_fail)
								Timber.d("Delete File Fail")
								dismiss()
						}
				}
				binding.renameBtn.setOnClickListener { renameDialog(comingRecord) }

				binding.playRecordBtn.setOnClickListener {
						val playFragment = RecorderXPlayerBottomSheet.newInstance(comingRecord)
						playFragment.show(requireActivity().supportFragmentManager, RecorderXPlayerBottomSheet.TAG)
						dismiss()
				}
		}

		private fun deleteRecord(comingRecordX: RecordX) {
				with(viewModel) {
						deleteRecordX(comingRecordX)
						deleted.observe(viewLifecycleOwner) { success ->
								if (success) {
										activity?.toast(R.string.alert_delete_success)
								} else {
										activity?.toast(R.string.alert_delete_fail)
								}
								dismiss()
						}

						errorInDelete.observe(viewLifecycleOwner) { error ->
								activity?.toast("Error: $error")
						}
				}
		}

		@SuppressLint("InflateParams")
		private fun renameDialog(comingRecordX: RecordX) {
				val view = LayoutInflater.from(context).inflate(R.layout.rename_layout, null)
				val newNameText = view.findViewById(R.id.new_record_name) as EditText
				val oldName = comingRecordX.recordName.substringBefore(".mp4")
				newNameText.setText(oldName)
				AlertDialog.Builder(context).apply {
						setTitle(R.string.alert_dialog_rename_title)
						setCancelable(true)
						setView(view)
						setPositiveButton(R.string.alert_dialog_rename_btn_ok) { dialog, _ ->
								val newName = newNameText.text.toString().trim() + ".mp4"
								reNamePath(comingRecordX, newName)
								dialog.cancel()
						}
						setNegativeButton(R.string.alert_dialog_rename_btn_cancel) { dialog, _ -> dialog.cancel() }
				}.create().show()
		}

		//Todo move to main repo
		private fun reNamePath(recordX: RecordX, newRecordName: String) {
				//rename a file
				var newFilePath = activity?.getAppPath()!!
				newFilePath += "/$newRecordName"
				val newFileDir = File(newFilePath)
				if (newFileDir.exists() && !newFileDir.isDirectory) {
						//file name is not unique, cannot rename file.
						activity?.toast(String.format(getString(R.string.toast_file_exists), newRecordName))
				} else {
						//file name is unique, rename file
						val oldFilePath = File(recordX.filePath)
						val result = oldFilePath.renameTo(newFileDir)
						if (result) {
								recordX.filePath = newFilePath
								recordX.recordName = newRecordName
								reName(recordX)
						} else {
								activity?.toast(R.string.alert_rename_fail)
								Timber.d("rename: result: $result")
						}
				}
		}

		private fun reName(recordX: RecordX) {
				with(viewModel) {
						renameRecord(recordX)

						renamed.observe(viewLifecycleOwner) { success ->
								if (success) {
										activity?.toast(R.string.alert_rename_success)
								} else {
										activity?.toast(R.string.alert_rename_fail)
								}
								dismiss()
						}

						errorInRename.observe(viewLifecycleOwner) { error ->
								activity?.toast("Error: $error")
								dismiss()
						}
				}
		}

		override fun onDestroyView() {
				super.onDestroyView()
				_binding = null
		}

		companion object {
				fun newInstance(recordX: RecordX) =
						OptionDialog().apply {
								arguments = Bundle().apply {
										putParcelable(RECORDX_KEY, recordX)
								}
						}

				const val RECORDX_KEY = "RECORDX_KEY"
				const val TAG = "OptionDialog"
		}
}
