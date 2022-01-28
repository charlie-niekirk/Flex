package me.cniekirk.flex.ui.submission

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.SlideDistanceProvider
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.ComposeCommentFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.viewmodel.ComposeCommentViewModel
import me.cniekirk.flex.util.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@AndroidEntryPoint
class ComposeCommentFragment : BaseFragment(R.layout.compose_comment_fragment) {

    private val binding by viewBinding(ComposeCommentFragmentBinding::bind)
    private val args by navArgs<ComposeCommentFragmentArgs>()
    private val viewModel by viewModels<ComposeCommentViewModel>()
    private val markwon by lazy(LazyThreadSafetyMode.NONE) {
        Markwon.builder(requireContext())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(StrikethroughPlugin())
            .usePlugin(SoftBreakAddsNewLinePlugin())
            .build()
    }
    private val onClick = object : LinkSpanHandler.OnClick {
        override fun onClick(view: View, link: String) {
            markwon.configuration().linkResolver().resolve(view, link)
        }
    }
    private val markwonEditor by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().createMarkdownEditor(markwon, onClick)
    }

    private var uploadingSnackbar: Snackbar? = null
    private var cameraUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            (primaryAnimatorProvider as SlideDistanceProvider).slideDistance =
                requireContext().resources.getDimension(R.dimen.slide_distance).toInt()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            (primaryAnimatorProvider as SlideDistanceProvider).slideDistance =
                requireContext().resources.getDimension(R.dimen.slide_distance).toInt()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.hide()
        }

        binding.apply {
            markwon.setMarkdown(commentContent, args.comment.body ?: "")
            commentAuthorUsername.text = args.comment.author
            if (args.comment.distinguishedRaw.equals("moderator", true)) {
                commentAuthorUsername.setTextColor(root.context.getColor(R.color.green))
            } else if (args.comment.distinguishedRaw.equals("admin", true)) {
                commentAuthorUsername.setTextColor(root.context.getColor(R.color.red))
            } else {
                commentAuthorUsername.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
            }
            commentLocked.isGone = !args.comment.isLocked!!
            commentPinned.isGone = !args.comment.isStickied!!
            commentUpvoteNumber.text = args.comment.score.toString()
            val topAwards = args.comment.allAwarding?.sortedByDescending { it.coinPrice }?.take(3)
            if (!topAwards.isNullOrEmpty()) {
                binding.awards.textTotalAwardCount.visibility = View.VISIBLE
                binding.awards.textTotalAwardCount.text = topAwards.sumOf { it.count ?: 0 }.toString()
                when (topAwards.size) {
                    1 -> {
                        binding.awards.imageSecondAward.visibility = View.GONE
                        binding.awards.imageThirdAward.visibility = View.GONE
                        Glide.with(binding.root).load(topAwards[0].iconUrl).into(binding.awards.imageFirstAward)
                    }
                    2 -> {
                        binding.awards.imageSecondAward.visibility = View.VISIBLE
                        binding.awards.imageThirdAward.visibility = View.GONE
                        Glide.with(binding.root).load(topAwards[0].iconUrl).into(binding.awards.imageFirstAward)
                        Glide.with(binding.root).load(topAwards[1].iconUrl).into(binding.awards.imageSecondAward)
                    }
                    else -> {
                        binding.awards.imageSecondAward.visibility = View.VISIBLE
                        binding.awards.imageThirdAward.visibility = View.VISIBLE
                        Glide.with(binding.root).load(topAwards[0].iconUrl).into(binding.awards.imageFirstAward)
                        Glide.with(binding.root).load(topAwards[1].iconUrl).into(binding.awards.imageSecondAward)
                        Glide.with(binding.root).load(topAwards[2].iconUrl).into(binding.awards.imageThirdAward)
                    }
                }
            } else {
                binding.awards.imageFirstAward.visibility = View.GONE
                binding.awards.imageSecondAward.visibility = View.GONE
                binding.awards.imageThirdAward.visibility = View.GONE
                binding.awards.textTotalAwardCount.visibility = View.GONE
            }

            buttonBold.setOnClickListener {
                composeCommentField.applyMarkdown("**")
            }

            buttonItalics.setOnClickListener {
                composeCommentField.applyMarkdown("_")
            }

            buttonStrikethrough.setOnClickListener {
                composeCommentField.applyMarkdown("~~")
            }

            buttonCodeBlock.setOnClickListener {
                composeCommentField.applyMarkdown("```", true)
            }

            buttonHeading.setOnClickListener {
                composeCommentField.applyMarkdown("#", shouldWrap = false)
            }

            buttonImage.setOnClickListener {
                val items = arrayOf("Camera", "Gallery")
                MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                    .setItems(items) { dialog, which ->
                        if (items[which].equals("Gallery", true)) {
                            chooseFromPhotos()
                        } else {
                            takePhoto()
                        }
                        dialog.dismiss()
                    }
                    .setTitle(R.string.pick_image_title)
                    .setIcon(R.drawable.ic_image)
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }

            composeCommentField.addTextChangedListener(
                MarkwonEditorTextWatcher.withPreRender(
                    markwonEditor,
                    Executors.newCachedThreadPool(),
                    composeCommentField
                )
            )

            backButton.setOnClickListener {
                findNavController().navigateUp()
            }

            submitButton.setOnClickListener {
                if (!composeCommentField.text?.trim().isNullOrEmpty()) {
                    viewModel.submitComment(composeCommentField.text.toString(), args.comment.fullname ?: "")
                } else {
                    // Display error toast
                    Toast.makeText(requireContext(), R.string.submit_comment_no_content_error, Toast.LENGTH_SHORT).show()
                }
            }

            composeCommentField.requestFocus()

            observe(viewModel.imageResponse) {
                when (it) {
                    is RedditResult.Error -> {
                        uploadingSnackbar?.dismiss()
                        Timber.e(it.errorMessage)
                        Snackbar.make(binding.root, R.string.unknown_error, Snackbar.LENGTH_SHORT).show()
                    }
                    RedditResult.Loading -> {
                       Toast.makeText(requireContext(), R.string.uploading_image, Toast.LENGTH_SHORT).show()
                    }
                    is RedditResult.Success -> {
                        //Toast.makeText(requireContext(), R.string.upload_image_success, Toast.LENGTH_SHORT).show()
                        uploadingSnackbar?.dismiss()

                        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.image_link_name_dialog, null)
                        val link = dialogView.findViewById<EditText>(R.id.link_input_edit_text).apply {
                            text.insert(0, it.data.data?.link)
                        }
                        val name = dialogView.findViewById<EditText>(R.id.name_input_edit_text)
                        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setTitle(R.string.set_image_name_title)
                            .setIcon(R.drawable.ic_image)
                            .setView(dialogView)
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setPositiveButton(R.string.ok) { dialog, _ ->
                                dialog.dismiss()
                                if (name.text.isNullOrEmpty()) {
                                    composeCommentField.text?.insert(composeCommentField.selectionEnd, link.text)
                                } else {
                                    composeCommentField.text?.insert(composeCommentField.selectionEnd, "[${name.text}](${link.text})")
                                }
                            }.show()
                    }
                    else -> {
                        uploadingSnackbar?.dismiss()
                        Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            observe(viewModel.submitCommentResponse) {
                when (it) {
                    RedditResult.Loading -> {
                        binding.submitButton.isVisible = false
                        binding.submitLoadingIndicator.isVisible = true
                    }
                    is RedditResult.Success -> {
                        Toast.makeText(requireContext(), R.string.submit_comment_success, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    is RedditResult.Error -> {
                        Timber.e(it.errorMessage)
                        binding.submitLoadingIndicator.isVisible = false
                        binding.submitButton.isVisible = true
                        Toast.makeText(requireContext(), R.string.submit_comment_error, Toast.LENGTH_SHORT).show()
                    }
                    RedditResult.UnAuthenticated -> {
                        // Should never happen as you shouldn't be able to get to this screen
                        Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Checks the user's permissions for uploading a photo from gallery, before allowing them to do so.
     *
     * @return true
     */
    private fun chooseFromPhotos(): Boolean
    {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            photoFromGalleryResultLauncher.launch(arrayOf("image/*"))
        }
        else
        {
            photoFilePermissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return true
    }

    // Result launcher for getting permissions to add a photo from the user's gallery
    private val photoFilePermissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if(it == true)
        {
            chooseFromPhotos()
        }
    }

    // Result launcher for adding a photo from the user's gallery
    private val photoFromGalleryResultLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        if(it != null)
        {
            val requestBody = InputStreamRequestBody(requireContext().contentResolver, it)
            viewModel.uploadImage(requestBody)
        }
    }

    //Result launcher for getting permissions to take a photo
    private val photoPermissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if(it == true)
        {
            takePhoto()
        }
    }

    //Result launcher for taking a photo
    private val takePhotoResultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { created ->
        if (created)
        {
            // Upload the image
            cameraUri?.let {
                val reqBody = InputStreamRequestBody(requireContext().contentResolver, it)
                viewModel.uploadImage(reqBody)
            }
        }
    }

    /**
     * Checks the user's permissions for taking a photo, before allowing them to take a photo to upload into a comment
     *
     * @return true
     */
    private fun takePhoto() : Boolean
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            photoPermissionResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        else
        {
            val currentDate = Calendar.getInstance().time
            val timeFormatted = SimpleDateFormat("yyyyMMddHHmmssSSSZ", Locale.getDefault()).format(currentDate)
            val uriStr = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            else
            {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "camera_$timeFormatted")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/flexApp/")
                }
            }
            val uri = context?.contentResolver?.insert(uriStr, contentValues)
            uri?.let {
                cameraUri = it
                takePhotoResultLauncher.launch(cameraUri)
            }
        }
        return true
    }

    private fun EditText.applyMarkdown(punctuation: String,
                                       requiresLineBreak: Boolean = false,
                                       shouldWrap: Boolean = true) {
        val start = selectionStart
        val end = selectionEnd

        if (shouldWrap) {
            if (start == end) {
                // Cursor position
                val wrapped = if (requiresLineBreak) {
                    "${punctuation}\nEdit me\n${punctuation}"
                } else {
                    "${punctuation}Edit me${punctuation}"
                }
                text?.insert(start, wrapped)
                if (requiresLineBreak) {
                    setSelection(start + punctuation.length + 1, start + 7 + punctuation.length + 1)
                } else {
                    setSelection(start + punctuation.length, start + 7 + punctuation.length)
                }
            } else {
                val selectionSize = end - start
                // Apply span to selected text
                text?.insert(start, punctuation)
                text?.insert(end + punctuation.length, punctuation)
                setSelection(start + punctuation.length, start + punctuation.length + selectionSize)
            }
        } else {
            if (start == end) {
                val newText = "$punctuation Edit me"
                text?.insert(start, newText)
                setSelection(start + punctuation.length + 1, start + 7 + punctuation.length + 1)
            } else {
                text?.insert(start, "$punctuation ")
            }
        }
    }
}