package me.cniekirk.flex.ui.dialog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.AuthedSubmission
import me.cniekirk.flex.databinding.MediaShareDialogBinding
import me.cniekirk.flex.util.*

@AndroidEntryPoint
class SubmissionShareDialogFragment(
    private val post: AuthedSubmission,
    private val mediaUrls: List<String>,
    private val currentIndex: Int
) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(post: AuthedSubmission, mediaUrl: List<String>, currentIndex: Int): SubmissionShareDialogFragment {
            return SubmissionShareDialogFragment(post, mediaUrl, currentIndex)
        }
    }

    private var binding: MediaShareDialogBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MediaShareDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            actionSharePostLink.setOnClickListener {
                shareText(getString(R.string.reddit_permalink_format, post.permalink))
            }
            actionShareImage.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    loadImage(mediaUrls[currentIndex]) {
                        shareMedia(getUriFromBitmap(it))
                    }
                }
            }

            actionShareAll.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val uris = arrayListOf<Uri>()
                    mediaUrls.forEach {
                        loadImage(it) { bmp -> uris.add(getUriFromBitmap(bmp)) }
                    }
                    shareMedia(uris)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }

}