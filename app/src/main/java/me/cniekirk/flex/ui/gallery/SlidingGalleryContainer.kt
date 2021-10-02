package me.cniekirk.flex.ui.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.GallerySliderHostFragmentBinding
import me.cniekirk.flex.ui.adapter.MediaGalleryPagerAdapter
import me.cniekirk.flex.ui.dialog.SubmissionCopyDialogFragment
import me.cniekirk.flex.ui.dialog.SubmissionMediaInformationDialog
import me.cniekirk.flex.ui.dialog.SubmissionShareDialogFragment
import me.cniekirk.flex.ui.viewmodel.GalleryViewModel
import me.cniekirk.flex.util.observe
import timber.log.Timber

@AndroidEntryPoint
class SlidingGalleryContainer : AppCompatActivity() {

    private val args by navArgs<SlidingGalleryContainerArgs>()
    private val viewModel by viewModels<GalleryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = GallerySliderHostFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observe(viewModel.downloadState) {
            when (it) {
                DownloadState.Idle -> {}
                DownloadState.NoDefinedLocation -> {
                    // Start permissions/file picker flow
                    viewModel.resetState()
                    chooseDirectory()
                }
                DownloadState.Success -> {
                    // Display toast message
                    Toast.makeText(this, R.string.successful_download, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.apply {
            args.post.mediaMetadata?.values?.toList()?.let {
                pager.adapter = MediaGalleryPagerAdapter(this@SlidingGalleryContainer, it)
                pageNumber.text = getString(R.string.gallery_page_number, pager.currentItem + 1, it.size)
                pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        pageNumber.text = getString(R.string.gallery_page_number, position + 1, it.size)
                    }
                })
            }
            backButton.setOnClickListener { finish() }

            val allUrls = args.post.mediaMetadata?.values?.toList()?.map { item ->
                getString(
                    R.string.reddit_image_url,
                    item.id,
                    item.m.substring(item.m.indexOf('/') + 1))
            }
            allUrls?.let {
                downloadButton.setOnClickListener {
                    viewModel.download(allUrls[pager.currentItem])
                }
                shareButton.setOnClickListener {
                    val dialog = SubmissionShareDialogFragment.newInstance(args.post, allUrls, pager.currentItem)
                    dialog.show(supportFragmentManager, "Share")
                }
                copyUrlButton.setOnClickListener {
                    val dialog = SubmissionCopyDialogFragment.newInstance(args.post, allUrls[pager.currentItem])
                    dialog.show(supportFragmentManager, "Copy")
                }
                infoButton.setOnClickListener {
                    val dialog = SubmissionMediaInformationDialog.newInstance(allUrls[pager.currentItem])
                    dialog.show(supportFragmentManager, "Info")
                }
            }
        }
    }

    private fun chooseDirectory() : Boolean
    {
        directoryPickerResultLauncher.launch(null)
        return true
    }

    //Result launcher for adding a photo from the user's gallery
    private val directoryPickerResultLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.registerDownloadLocation(it)
        }
    }

}