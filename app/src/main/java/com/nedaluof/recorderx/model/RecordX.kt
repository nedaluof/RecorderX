package com.nedaluof.recorderx.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Created by nedaluof on 10/28/2020.
 */
@Entity
@Parcelize
data class RecordX(
  var recordName: String = "", // file name
  var filePath: String = "", // file path
  var recordLength: Long = 0, // in seconds
  var createdAt: String = LocalDateTime.now().toString(), // date time of the record creation
) : Parcelable {
  @IgnoredOnParcel //  id not used to build the object from this class
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0
}
