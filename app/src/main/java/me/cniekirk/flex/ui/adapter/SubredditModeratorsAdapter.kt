package me.cniekirk.flex.ui.adapter

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.subreddit.ModUser
import me.cniekirk.flex.databinding.SubredditModeratorListItemBinding

class SubredditModeratorsAdapter : ListAdapter<ModUser, SubredditModeratorsAdapter.ModeratorViewHolder>(ModeratorDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModeratorViewHolder {
        return ModeratorViewHolder(
            SubredditModeratorListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ModeratorViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ModeratorViewHolder(private val binding: SubredditModeratorListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(modUser: ModUser) {
            binding.apply {
                modName.text = modUser.name
                textModPerms.text =
                    binding.root.context.getString(
                        R.string.moderator_permissions_format,
                        modUser.modPermissions?.joinToString())
            }
        }
    }

    object ModeratorDiffer : DiffUtil.ItemCallback<ModUser>() {
        override fun areItemsTheSame(oldItem: ModUser, newItem: ModUser) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ModUser, newItem: ModUser) =
            oldItem == newItem
    }
}