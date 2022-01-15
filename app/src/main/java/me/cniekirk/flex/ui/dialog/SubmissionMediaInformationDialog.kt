package me.cniekirk.flex.ui.dialog

import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.cniekirk.flex.databinding.MediaMetadataDialogBinding
import me.cniekirk.flex.util.getUriFromBitmap
import me.cniekirk.flex.util.loadImage


@AndroidEntryPoint
class SubmissionMediaInformationDialog(private val mediaUrl: String) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(mediaUrl: String): SubmissionMediaInformationDialog {
            return SubmissionMediaInformationDialog(mediaUrl)
        }
    }

    private var binding: MediaMetadataDialogBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MediaMetadataDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            metadataSourceValue.text = mediaUrl
            lifecycleScope.launch(Dispatchers.IO) {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                loadImage(mediaUrl) {
                    val uri = getUriFromBitmap(it)
                    BitmapFactory.decodeStream(
                        requireContext().contentResolver.openInputStream(uri),
                        null,
                        options
                    )
                    requireContext().contentResolver.query(uri,
                        null, null, null, null)?.use { cursor ->
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        cursor.moveToFirst()
                        withContext(Dispatchers.Main) {
                            metadataDimensionsValue.text = "${options.outWidth} X ${options.outHeight}"
                            metadataSizeValue.text = Formatter.formatFileSize(requireContext(), cursor.getLong(sizeIndex))
                        }
                    }
                }
            }

        }
    }

}