package com.nedaluof.recorderx.ui.savedrecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nedaluof.recorderx.R
import com.nedaluof.recorderx.databinding.FragmentSavedRecordsBinding
import com.nedaluof.recorderx.model.RecordX
import com.nedaluof.recorderx.ui.savedrecords.RecorderXAdapter.ClickListener
import com.nedaluof.recorderx.ui.savedrecords.optiondialog.OptionDialog
import com.nedaluof.recorderx.util.initBottomSheetBehavior
import com.nedaluof.recorderx.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedRecordsBottomSheet : BottomSheetDialogFragment() {

		//view binding
		private var _binding: FragmentSavedRecordsBinding? = null
		private val binding: FragmentSavedRecordsBinding
				get() = _binding!!

		//saved records view model
		private val viewModel: SavedRecordsViewModel by viewModels()

		//saved records adapter
		private var recordsAdapter = RecorderXAdapter()

		override fun onCreateView(
				inflater: LayoutInflater,
				container: ViewGroup?,
				savedInstanceState: Bundle?,
		): View {
				_binding = FragmentSavedRecordsBinding.inflate(inflater, container, false)
				initBottomSheetBehavior { sheetState ->
						when (sheetState) {
								BottomSheetBehavior.STATE_HIDDEN -> dismiss()
								BottomSheetBehavior.STATE_COLLAPSED -> dismiss()
						}
				}
				return binding.root
		}

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
				super.onViewCreated(view, savedInstanceState)
				binding.recordsRecyclerView.apply {
						itemAnimator = DefaultItemAnimator()
						adapter = recordsAdapter
				}
				onDataChange()
				with(viewModel) {
						getRecords()

						records.observe(viewLifecycleOwner) {
								recordsAdapter.addData(it as ArrayList<RecordX>)
								onDataChange()
						}

						loading.observe(viewLifecycleOwner) {
								//unused now
						}

						error.observe(viewLifecycleOwner) { error ->
								activity?.toast("Error: $error")
						}
				}

				recordsAdapter.clickListener = ClickListener {
						val dialog = OptionDialog.newInstance(it)
						dialog.show(activity?.supportFragmentManager!!, OptionDialog.TAG)
				}
		}


		private fun onDataChange() {
				if (recordsAdapter.itemCount > 0) {
						binding.recordsRecyclerView.visibility = View.VISIBLE
						binding.noRecordsTxt.visibility = View.GONE
				} else {
						binding.recordsRecyclerView.visibility = View.GONE
						binding.noRecordsTxt.visibility = View.VISIBLE
				}
		}


		override fun onDestroyView() {
				super.onDestroyView()
				_binding = null
		}

		override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

		companion object {
				const val TAG = "SAVED_RECORDS"
		}
}
