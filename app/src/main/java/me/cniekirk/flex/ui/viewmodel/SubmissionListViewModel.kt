package me.cniekirk.flex.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.R
import me.cniekirk.flex.data.local.db.dao.PreLoginUserDao
import me.cniekirk.flex.data.local.db.dao.UserDao
import me.cniekirk.flex.data.remote.*
import me.cniekirk.flex.data.remote.model.reddit.subreddit.Subreddit
import me.cniekirk.flex.data.remote.pagination.SubredditSubmissionsPagingSource
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.WorkerRepository
import me.cniekirk.flex.domain.model.SubredditSearchRequest
import me.cniekirk.flex.domain.usecase.GetSubredditInfoUseCase
import me.cniekirk.flex.domain.usecase.SearchSubredditsUseCase
import me.cniekirk.flex.ui.submission.SubmissionListEvent
import me.cniekirk.flex.ui.submission.model.toUiSubmission
import me.cniekirk.flex.ui.submission.state.SubmissionListSideEffect
import me.cniekirk.flex.ui.submission.state.SubmissionListState
import me.cniekirk.flex.worker.ScheduledNotificationWorker
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltViewModel
class SubmissionListViewModel @Inject constructor(
    @Named("preLoginApi") private val redditApi: RedditApi,
    @Named("loginApi") private val authRedditApi: RedditApi,
    private val streamableApi: StreamableApi,
    private val gfycatApi: GfycatApi,
    private val imgurApi: ImgurApi,
    private val redGifsApi: RedGifsApi,
    private val twitterApi: TwitterApi,
    private val preLoginUserDao: PreLoginUserDao,
    private val flexSettings: DataStore<FlexSettings>,
    private val userDao: UserDao,
    private val searchSubredditsUseCase: SearchSubredditsUseCase,
    private val getSubredditInfoUseCase: GetSubredditInfoUseCase,
    private val workerRepository: WorkerRepository
) : ViewModel(), ContainerHost<SubmissionListState, SubmissionListSideEffect> {

    override val container = container<SubmissionListState, SubmissionListSideEffect>(
        SubmissionListState()
    ) {
        loadSubmissions()
    }

    val settingsFlow: Flow<FlexSettings> = flexSettings.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception)
                emit(FlexSettings.getDefaultInstance())
            } else {
                throw exception
            }
        }

    private fun loadSubmissions() = intent {
        val pager = Pager(config = PagingConfig(pageSize = 15, prefetchDistance = 5)) {
            SubredditSubmissionsPagingSource(redditApi, authRedditApi, streamableApi,
                imgurApi, gfycatApi, redGifsApi, twitterApi, state.subreddit, "/${state.sort}", preLoginUserDao, userDao)
        }.flow.map { pagingData ->
                pagingData.map { submission -> submission.toUiSubmission() }
            }.cachedIn(viewModelScope)
        reduce {
            state.copy(submissions = pager)
        }
    }

    fun updateSubreddit(subreddit: String) = intent {
        reduce { state.copy(subreddit = subreddit) }
        loadSubmissions()
    }

    fun randomSubredditSelected(randomType: String) = intent {
        getSubredditInfoUseCase(randomType).collect { result ->
            when (result) {
                is RedditResult.Success -> {
                    result.data.displayName?.let { name ->
                        reduce { state.copy(subreddit = name) }
                        loadSubmissions()
                    } ?: run {
                        postSideEffect(SubmissionListSideEffect.Error(R.string.generic_network_error))
                    }
                }
                is RedditResult.Error -> {
                    postSideEffect(SubmissionListSideEffect.Error(R.string.generic_network_error))
                }
                else -> {
                    postSideEffect(SubmissionListSideEffect.Error(R.string.generic_network_error))
                }
            }
        }
    }

    fun postReminderSet(postId: String) = intent {
        val request = OneTimeWorkRequestBuilder<ScheduledNotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setInputData(workDataOf("THING_ID" to postId))
            .build()
        workerRepository.scheduleOneTimeWork(request)
        postSideEffect(SubmissionListSideEffect.SubmissionReminderSet)
    }

    suspend fun searchSubreddit(query: String): Flow<RedditResult<List<Subreddit>>> =
        searchSubredditsUseCase(SubredditSearchRequest(query))

    suspend fun initialiseDefaultSettings() {
        val defaultProfile = FlexSettings.Profile.newBuilder()
            .setName("Default")
            .setBlurNsfw(false)
            .setShowPreviews(true)
            .setSelected(true)
        flexSettings.updateData { it.toBuilder().addProfiles(defaultProfile).build() }
    }
}