package me.cniekirk.flex.ui.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.table.TableEntry
import io.noties.markwon.recycler.table.TableEntryPlugin
import kotlinx.coroutines.launch
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.ShareAsImageDialogBinding
import me.cniekirk.flex.util.*
import org.commonmark.ext.gfm.tables.TableBlock

@AndroidEntryPoint
class SharePostAsImageDialog : BottomSheetDialogFragment() {

    private val markwon by lazy(LazyThreadSafetyMode.NONE) {
        Markwon
            .builder(requireContext())
            .usePlugin(StrikethroughPlugin())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(TableEntryPlugin.create(requireContext()))
            .build()
    }
    private val markwonAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MarkwonAdapter.builder(R.layout.adapter_default_entry, R.id.text_default)
            .include(TableBlock::class.java, TableEntry.create {
                it.tableLayout(R.layout.adapter_table_block, R.id.table_layout)
                    .textLayoutIsRoot(R.layout.view_table_entry_cell)
            }).build()
    }
    private var binding: ShareAsImageDialogBinding? = null
    private val args by navArgs<SharePostAsImageDialogArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShareAsImageDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            textSubmissionTitle.text = args.post.title
            textSubredditName.text = args.post.subreddit
            textUpvoteRatio.text = getString(
                R.string.percentage_format,
                (args.post.upvoteRatio.times(100).toInt().toString())
            )
            textUpvoteCount.text = args.post.ups?.condense()
            textTimeSincePost.text = args.post.created?.toLong()?.getElapsedTime()
            textSubmissionAuthor.text = getString(R.string.author_format, args.post.author)
            args.post.linkFlairText?.let {
                textSubmissionFlair.visibility = View.VISIBLE
                textSubmissionFlair.text = it
                if (args.post.linkFlairBackgroundColor.isNullOrEmpty()) {
                    textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    textSubmissionFlair.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor(args.post.linkFlairBackgroundColor))
                }
                if (args.post.linkFlairTextColor.equals("dark", true)) {
                    textSubmissionFlair.setTextColor(root.context.getColor(R.color.black))
                } else {
                    textSubmissionFlair.setTextColor(root.context.getColor(R.color.white))
                }
            } ?: run {
                textSubmissionFlair.visibility = View.GONE
            }
            if (args.post.isSelf!!) {
                selftextPreview.selfTextMarkdown.visibility = View.VISIBLE
                imagePreview.submissionImage.visibility = View.GONE
                videoPreview.videoPlayer.visibility = View.GONE
                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                selftextPreview.selfTextMarkdown.adapter = markwonAdapter
                markwonAdapter.setMarkdown(markwon, args.post.selftext ?: "")
                markwonAdapter.notifyDataSetChanged()
            } else {
                selftextPreview.selfTextMarkdown.visibility = View.GONE
                args.post.urlOverriddenByDest?.let { url ->
                    url.processLink {
                        when (it) {
                            Link.ExternalLink -> {
                                videoPreview.videoPlayer.visibility = View.GONE
                                imagePreview.submissionImage.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.VISIBLE
                                externalLinkPreview.linkContent.text = args.post.url
                                Glide.with(externalLinkPreview.linkImage)
                                    .load(args.post.preview?.images?.lastOrNull()?.resolutions?.lastOrNull()?.url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(
                                        CenterCrop(), GranularRoundedCorners(
                                            root.resources.getDimension(R.dimen.spacing_m),
                                            root.resources.getDimension(R.dimen.spacing_m),
                                            0F,
                                            0F
                                        )
                                    )
                                    .into(externalLinkPreview.linkImage)
                            }
                            is Link.ImageLink -> {
                                videoPreview.videoPlayer.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                imagePreview.submissionImage.visibility = View.VISIBLE
                                Glide.with(imagePreview.submissionImage)
                                    .load(it.url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imagePreview.submissionImage)
                            }
                            is Link.VideoLink -> {
                                imagePreview.submissionImage.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                videoPreview.videoPlayer.visibility = View.VISIBLE
                                //videoPreview.videoPlayer.initialise(simpleExoPlayer, args.post.urlOverriddenByDest ?: "")
                            }
                            Link.RedditVideo -> {
                                imagePreview.submissionImage.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                videoPreview.videoPlayer.visibility = View.VISIBLE
//                                player = videoPreview.videoPlayer.initialise(simpleExoPlayer,
//                                    args.post.media?.redditVideo?.dashUrl ?:
//                                    args.post.media?.redditVideo?.fallbackUrl!!)
                            }
                            is Link.TwitterLink -> {
                                // TODO: Get Twitter API access
                            }
                        }
                    }
                }
            }
            if (args.post.stickied!!) {
                textSubmissionAuthor.setTextColor(root.context.getColor(R.color.green))
                textSubmissionAuthor.setTypeface(textSubmissionAuthor.typeface, Typeface.BOLD)
                submissionPin.visibility = View.VISIBLE
                val cs = ConstraintSet()
                cs.clone(previewImageContainer)
                cs.connect(
                    textSubmissionAuthor.id,
                    ConstraintSet.START,
                    submissionPin.id,
                    ConstraintSet.END,
                    root.context.resources.getDimension(R.dimen.spacing_s).toInt()
                )
                cs.applyTo(previewImageContainer)
            } else {
                textSubmissionAuthor.setTextColor(root.context.resolveColorAttr(android.R.attr.textColorPrimary))
                textSubmissionAuthor.setTypeface(textSubmissionAuthor.typeface, Typeface.NORMAL)
                submissionPin.visibility = View.GONE
                val cs = ConstraintSet()
                cs.clone(previewImageContainer)
                cs.connect(
                    textSubmissionAuthor.id,
                    ConstraintSet.START,
                    textSubredditName.id,
                    ConstraintSet.START
                )
                cs.applyTo(previewImageContainer)
            }
            if (args.post.authorFlairText.isNullOrBlank()) {
                textAuthorFlair.visibility = View.GONE
            } else {
                textAuthorFlair.visibility = View.VISIBLE
                if (args.post.author.equals("tommyinnit", true)) {
                    textAuthorFlair.text = args.post.authorFlairText + " 5'10\""
                } else {
                    textAuthorFlair.text = args.post.authorFlairText
                }
                if (args.post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (args.post.authorFlairBackgroundColor.equals("transparent", true)) {
                    textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                } else {
                    textAuthorFlair.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor(args.post.authorFlairBackgroundColor))
                }
            }

            hideSubreddit.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    textSubredditName.foreground = ColorDrawable(requireContext().getColorFromAttr(R.attr.colorSecondary))
                } else {
                    textSubredditName.foreground = null
                }
            }

            hideUsername.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    textSubmissionAuthor.foreground = ColorDrawable(requireContext().getColorFromAttr(R.attr.colorSecondary))
                } else {
                    textSubmissionAuthor.foreground = null
                }
            }

            shareButton.setOnClickListener {
                previewImageContainer.bitmap { bmp ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        val uri = getUriFromBitmap(bmp)
                        shareMedia(uri)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}