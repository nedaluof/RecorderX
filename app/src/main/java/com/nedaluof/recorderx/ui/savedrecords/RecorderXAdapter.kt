package com.nedaluof.recorderx.ui.savedrecords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nedaluof.recorderx.databinding.ItemRecordBinding
import com.nedaluof.recorderx.model.RecordX
import com.nedaluof.recorderx.util.formatDate
import com.nedaluof.recorderx.util.formatLength

/**
 * Created by nedaluof on 11/14/2020.
 */
class RecorderXAdapter : RecyclerView.Adapter<RecorderXAdapter.RecordXVH>() {

		private val recordersList = ArrayList<RecordX>()


		var clickListener: ClickListener? = null

		fun addData(data: ArrayList<RecordX>) {
				recordersList.clear()
				recordersList.addAll(data)
				notifyDataSetChanged()
		}

		override fun onBindViewHolder(holder: RecordXVH, position: Int) {
				val recordX = recordersList[position]
				holder.binding.run {
						recordName.text = recordX.recordName
						createdAt.text = recordX.createdAt.formatDate()
						length.text = recordX.recordLength.formatLength()
						root.setOnClickListener { clickListener?.onClick(recordX) }
				}
		}

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordXVH =
				RecordXVH(
						ItemRecordBinding.inflate(LayoutInflater.from(parent.context))
				)

		override fun getItemCount(): Int = recordersList.size

		class RecordXVH(val binding: ItemRecordBinding) : RecyclerView.ViewHolder(binding.root)



		fun interface ClickListener {
				fun onClick(recordX: RecordX)
		}
}
