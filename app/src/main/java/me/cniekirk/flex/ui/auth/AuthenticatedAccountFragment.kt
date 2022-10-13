package me.cniekirk.flex.ui.auth
//
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.fragment.app.viewModels
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import dagger.hilt.android.AndroidEntryPoint
//import im.ene.toro.exoplayer.ExoCreator
//import me.cniekirk.flex.R
//import me.cniekirk.flex.databinding.AuthenticatedAccountFragmentBinding
//import me.cniekirk.flex.ui.BaseFragment
//import me.cniekirk.flex.ui.adapter.SubmissionListAdapter
//import me.cniekirk.flex.ui.auth.state.AccountViewSideEffect
//import me.cniekirk.flex.ui.auth.state.AccountViewState
//import me.cniekirk.flex.ui.viewmodel.AccountViewModel
//import me.cniekirk.flex.util.viewBinding
//import org.orbitmvi.orbit.viewmodel.observe
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class AuthenticatedAccountFragment: BaseFragment(R.layout.authenticated_account_fragment) {
//
//    private val binding by viewBinding(AuthenticatedAccountFragmentBinding::bind)
//    private val viewModel by viewModels<AccountViewModel>()
//    private var adapter: SubmissionListAdapter? = null
//
//    @Inject
//    lateinit var exoCreator: ExoCreator
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
//        if (!actionButton.isOrWillBeHidden) {
//            actionButton.visibility = View.GONE
//        }
//
//        viewModel.observe(viewLifecycleOwner, ::render, ::handleSideEffect)
//    }
//
//    private fun render(accountViewState: AccountViewState) {
//        binding.apply {
//            textAccountUsername.text = accountViewState.username
//            commentKarmaStatItem.setValue(accountViewState.commentKarma)
//            postKarmaStatItem.setValue(accountViewState.postKarma)
//            accountAgeStatItem.setValue(accountViewState.accountAge)
//        }
//    }
//
//    private fun handleSideEffect(accountViewSideEffect: AccountViewSideEffect) {
//        when (accountViewSideEffect) {
//            is AccountViewSideEffect.Toast -> {
//                Toast.makeText(requireContext(), accountViewSideEffect.message, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}