package me.cniekirk.flex.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.cniekirk.flex.domain.usecase.AddTrackerUseCase
import me.cniekirk.flex.ui.settings.state.CreateSubredditTrackerEffect
import me.cniekirk.flex.ui.settings.state.CreateSubredditTrackerState
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class CreateSubredditTrackerViewModel @Inject constructor(
    private val addTrackerUseCase: AddTrackerUseCase
) : ViewModel(), ContainerHost<CreateSubredditTrackerState, CreateSubredditTrackerEffect> {

    override val container = container<CreateSubredditTrackerState, CreateSubredditTrackerEffect>(
        CreateSubredditTrackerState()
    ) {

    }

    private fun init() = intent {

    }
}