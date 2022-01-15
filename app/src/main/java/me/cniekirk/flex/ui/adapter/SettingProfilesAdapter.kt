package me.cniekirk.flex.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.databinding.SettingProfileListItemBinding

class SettingProfilesAdapter : ListAdapter<FlexSettings.Profile, SettingProfilesAdapter.ProfileViewHolder>(ProfileDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(
            SettingProfileListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ProfileViewHolder(private val binding: SettingProfileListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: FlexSettings.Profile) {
            binding.profileIcon.isVisible = profile.selected
            binding.profileName.text = profile.name
        }
    }

    object ProfileDiffer : DiffUtil.ItemCallback<FlexSettings.Profile>() {
        override fun areItemsTheSame(oldItem: FlexSettings.Profile, newItem: FlexSettings.Profile) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: FlexSettings.Profile, newItem: FlexSettings.Profile) =
            oldItem == newItem
    }
}