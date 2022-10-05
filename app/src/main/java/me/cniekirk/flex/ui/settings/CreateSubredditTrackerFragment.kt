package me.cniekirk.flex.ui.settings

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.CreateSubredditTrackerFragmentBinding
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.util.WavyUnderlineSpan
import me.cniekirk.flex.util.resolveColorAttr
import me.cniekirk.flex.util.viewBinding

/**
 *
 */
@AndroidEntryPoint
class CreateSubredditTrackerFragment : BaseFragment(R.layout.create_subreddit_tracker_fragment) {

    private val binding by viewBinding(CreateSubredditTrackerFragmentBinding::bind)
//    private val viewModel by viewModels<CreateSubredditTrackerViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stringToShow = "Track posts in r/worldnews that contains \"Ukraine\" in the title and has more than 200 upvotes"
        val color = requireContext().resolveColorAttr(R.attr.colorTertiary)

        val spannable = SpannableString(stringToShow)

        spannable.setSpan(WavyUnderlineSpan(color), stringToShow.indexOf("r/worldnews"), stringToShow.indexOf("r/worldnews") + "r/worldnews".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(WavyUnderlineSpan(color), stringToShow.indexOf("\"Ukraine\""), stringToShow.indexOf("\"Ukraine\"") + "\"Ukraine\"".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(WavyUnderlineSpan(color), stringToShow.indexOf("200"), stringToShow.indexOf("200") + "200".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.trackerBuilderText.setText(spannable, TextView.BufferType.SPANNABLE)
    }
}