package me.cniekirk.flex.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.core.view.isVisible
//import androidx.paging.LoadState
//import androidx.paging.LoadStateAdapter
//import androidx.recyclerview.widget.RecyclerView
//import me.cniekirk.flex.databinding.LoadingFooterBinding
//
//class SubmissionListLoadingStateAdapter
//    : LoadStateAdapter<SubmissionListLoadingStateAdapter.LoadingStateViewHolder>() {
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        loadState: LoadState
//    ): LoadingStateViewHolder = LoadingStateViewHolder(
//            LoadingFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//
//    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
//        holder.binding.loadingIndicator.isVisible = loadState is LoadState.Loading
//    }
//
//    inner class LoadingStateViewHolder(val binding: LoadingFooterBinding)
//        : RecyclerView.ViewHolder(binding.root)
//}