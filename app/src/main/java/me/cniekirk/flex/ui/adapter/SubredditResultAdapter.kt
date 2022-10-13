package me.cniekirk.flex.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import me.cniekirk.flex.R
//import me.cniekirk.flex.data.remote.model.reddit.subreddit.Subreddit
//import me.cniekirk.flex.databinding.SubredditResultListItemBinding
//import me.cniekirk.flex.util.condense
//
//class SubredditResultAdapter(
//    private val subredditResultListener: SubredditResultListener
//) : ListAdapter<Subreddit, SubredditResultAdapter.SubredditResultViewHolder>(SubredditDiffer) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditResultViewHolder {
//        return SubredditResultViewHolder(
//            SubredditResultListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        )
//    }
//
//    override fun onBindViewHolder(holder: SubredditResultViewHolder, position: Int) =
//        holder.bind(currentList[position])
//
//    inner class SubredditResultViewHolder(private val binding: SubredditResultListItemBinding)
//        : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: Subreddit) {
//            binding.apply {
//                root.setOnClickListener { subredditResultListener.onSubredditSelected(item.displayName ?: "") }
//                subredditName.text = item.displayName
//                subredditDescription.text = item.publicDescription
//                subredditSubscribers.text =
//                    binding.root.context.getString(R.string.subreddit_subscribers_format, item.subscribers?.condense())
//            }
//        }
//
//    }
//
//    object SubredditDiffer : DiffUtil.ItemCallback<Subreddit>() {
//        override fun areItemsTheSame(oldItem: Subreddit, newItem: Subreddit) =
//            oldItem.id == newItem.id
//        override fun areContentsTheSame(oldItem: Subreddit, newItem: Subreddit) =
//            oldItem == newItem
//    }
//
//    interface SubredditResultListener {
//        fun onSubredditSelected(subreddit: String)
//    }
//}