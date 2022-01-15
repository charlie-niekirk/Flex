package me.cniekirk.flex.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.cniekirk.flex.data.remote.model.reddit.subreddit.PollOption
import me.cniekirk.flex.databinding.PollOptionItemBinding

class PollOptionsAdapter : ListAdapter<PollOption, PollOptionsAdapter.PollOptionViewHolder>(OptionDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollOptionViewHolder {
        return PollOptionViewHolder(
            PollOptionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PollOptionViewHolder, position: Int) {
        holder.bind(currentList[position], position)
    }

    inner class PollOptionViewHolder(private val binding: PollOptionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pollOption: PollOption, position: Int) {
            binding.apply {
                optionName.text = "${position + 1}. ${pollOption.text}"
            }
        }
    }

    object OptionDiffer : DiffUtil.ItemCallback<PollOption>() {
        override fun areItemsTheSame(oldItem: PollOption, newItem: PollOption) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PollOption, newItem: PollOption) =
            oldItem == newItem
    }
}