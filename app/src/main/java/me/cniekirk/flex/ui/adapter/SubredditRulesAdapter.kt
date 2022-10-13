package me.cniekirk.flex.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import io.noties.markwon.Markwon
//import me.cniekirk.flex.data.remote.model.reddit.rules.Rule
//import me.cniekirk.flex.databinding.SubredditRuleListItemBinding
//
//class SubredditRulesAdapter(
//    private val markwon: Markwon
//) : ListAdapter<Rule, SubredditRulesAdapter.RuleViewHolder>(RulesDiffer) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
//        return RuleViewHolder(
//            SubredditRuleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        )
//    }
//
//    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
//        holder.bind(currentList[position])
//    }
//
//    inner class RuleViewHolder(private val binding: SubredditRuleListItemBinding) :
//            RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(rule: Rule) {
//            binding.apply {
//                ruleName.text = rule.shortName
//                ruleDescription.text = if (rule.description.isNullOrEmpty()) {
//                    "No description"
//                } else {
//                     markwon.toMarkdown(rule.description)
//                }
//            }
//        }
//    }
//
//    object RulesDiffer : DiffUtil.ItemCallback<Rule>() {
//        override fun areItemsTheSame(oldItem: Rule, newItem: Rule) =
//            oldItem.shortName == newItem.shortName
//        override fun areContentsTheSame(oldItem: Rule, newItem: Rule) =
//            oldItem == newItem
//    }
//}