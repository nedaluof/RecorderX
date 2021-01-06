package com.nedaluof.recorderx.ui.savedrecords.playrecord

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nedaluof.recorderx.databinding.FragmentPlayerBinding
import com.nedaluof.recorderx.model.RecordX
import com.nedaluof.recorderx.util.initBottomSheetBehavior

/**
 * Created by nedaluof on 12/5/2020.
 */
class RecorderXPlayerBottomSheet : BottomSheetDialogFragment() {

		//view binding
		private var _binding: FragmentPlayerBinding? = null
		private val binding: FragmentPlayerBinding
				get() = _binding!!

		private var sheetBehavior: BottomSheetBehavior<*>? = null

		//EXO player
		private var player: SimpleExoPlayer? = null
		private lateinit var mediaSource: MediaSource
		private var playWhenReady = true
		private var currentWindow = 0
		private var playbackPosition: Long = 0

		override fun onCreateView(
				inflater: LayoutInflater,
				container: ViewGroup?,
				savedInstanceState: Bundle?,
		): View {
				_binding = FragmentPlayerBinding.inflate(inflater, container, false)
				initBottomSheetBehavior { sheetState ->
						if (sheetState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
				}
				return binding.root
		}

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
				super.onViewCreated(view, savedInstanceState)
				val comingRecord = arguments?.getParcelable<RecordX>(KEY)
				initializePlayer(comingRecord!!)
				binding.imgBtnClose.setOnClickListener {
						releasePlayer()
						dismiss()
				}
		}

		private fun initializePlayer(comingRecordX: RecordX) {
				binding.recordName.text = comingRecordX.recordName
				val recordPath = comingRecordX.filePath
				mediaSource = buildMediaSource(Uri.parse(recordPath))
				player = SimpleExoPlayer.Builder(requireActivity()).build()
				binding.playerController.player = player
				player?.playWhenReady = playWhenReady
				player?.seekTo(currentWindow, playbackPosition)
				player?.prepare(mediaSource, false, false)
				player?.addListener(object : Player.EventListener {
						override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
								if (playbackState == Player.STATE_ENDED) {
										sheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
										Handler(Looper.getMainLooper()).postDelayed({
												player?.release()
												dismiss()
										}, 1000)
								}
						}
				})
		}

		private fun buildMediaSource(uri: Uri): MediaSource {
				val dataSourceFactory: DataSource.Factory =
						DefaultDataSourceFactory(requireActivity(), "RecorderX Player")
				return ProgressiveMediaSource.Factory(dataSourceFactory)
						.createMediaSource(uri)
		}

		private fun releasePlayer() {
				if (player != null) {
						playWhenReady = player?.playWhenReady!!
						playbackPosition = player?.currentPosition!!
						currentWindow = player?.currentWindowIndex!!
						player?.release()
						player = null
				}
		}


		override fun onPause() {
				super.onPause()
				if (Util.SDK_INT < 24) {
						releasePlayer()
				}
		}

		override fun onStop() {
				super.onStop()
				if (Util.SDK_INT >= 24) {
						releasePlayer()
				}
		}

		override fun onDestroyView() {
				super.onDestroyView()
				_binding = null
		}

		companion object {
				fun newInstance(recordX: RecordX) =
						RecorderXPlayerBottomSheet().apply {
								arguments = Bundle().apply {
										putParcelable(KEY, recordX)
								}
						}

				private const val KEY = "RECORD_KEY"
				const val TAG = "RecorderXPlayerBS"
		}
}