package me.cniekirk.flex.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.flair.UserFlairItem
import me.cniekirk.flex.databinding.SubredditFlairListItemBinding

class SubredditUserFlairAdapter(
    private val flairClickListener: FlairClickListener
) : ListAdapter<UserFlairItem, SubredditUserFlairAdapter.UserFlairViewHolder>(FlairDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFlairViewHolder {
        return UserFlairViewHolder(
            SubredditFlairListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: UserFlairViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class UserFlairViewHolder(private val binding: SubredditFlairListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserFlairItem) {
            binding.apply {
                textSubmissionFlair.text = item.text
                if (item.textColor.equals("dark", true)) {
                    textSubmissionFlair.setTextColor(binding.root.context.getColor(R.color.black))
                } else {
                    textSubmissionFlair.setTextColor(binding.root.context.getColor(R.color.white))
                }
                if (item.backgroundColor.isNullOrEmpty()) {
                    textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item.backgroundColor))
                }
                root.setOnClickListener { flairClickListener.onFlairClicked(item) }
            }
        }
    }

    object FlairDiffer : DiffUtil.ItemCallback<UserFlairItem>() {
        override fun areItemsTheSame(oldItem: UserFlairItem, newItem: UserFlairItem) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: UserFlairItem, newItem: UserFlairItem) =
            oldItem == newItem
    }

    interface FlairClickListener {
        fun onFlairClicked(flair: UserFlairItem)
    }
}