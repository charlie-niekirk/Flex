package me.cniekirk.flex.ui.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.AuthedSubmission
import me.cniekirk.flex.databinding.SubmissionCopyDialogBinding
import me.cniekirk.flex.util.setCurrentScreen

@AndroidEntryPoint
class SubmissionCopyDialogFragment(
    private val post: AuthedSubmission,
    private val mediaUrl: String
) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(post: AuthedSubmission, mediaUrl: String): SubmissionCopyDialogFragment {
            return SubmissionCopyDialogFragment(post, mediaUrl)
        }
    }

    private val clipboard by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    private var binding: SubmissionCopyDialogBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SubmissionCopyDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            actionCopyPostTitle.setOnClickListener {
                setClipAndDismiss(getString(R.string.post_title_clip_key), post.title)
            }
            actionCopyMediaLink.setOnClickListener {
                setClipAndDismiss(getString(R.string.media_link_clip_key), mediaUrl)
            }
            actionCopyRedditLink.setOnClickListener {
                setClipAndDismiss(getString(R.string.post_link_clip_key),
                    getString(R.string.reddit_permalink_format, post.permalink))
            }
        }
    }

    private fun setClipAndDismiss(key: String, value: String) {
        val clipData = ClipData.newPlainText(key, value)
        clipboard.setPrimaryClip(clipData)
        dismiss()
    }

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }
}