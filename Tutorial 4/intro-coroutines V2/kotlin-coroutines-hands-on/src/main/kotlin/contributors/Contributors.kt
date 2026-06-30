package contributors

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

enum class LoadingStatus { INIT, COMPLETED, CANCELED, IN_PROGRESS }

data class LoadingStateData(
    val status: LoadingStatus = LoadingStatus.INIT,
    val startTime: Long? = null,
    val elapsedTime: String = ""
)

data class RequestData(
    val org: String,
    val username: String,
    val passwordOrToken: String
)

interface Contributors {
    val loadingState: StateFlow<LoadingStateData>
    fun updateLoadingStatus(newStatus: LoadingStateData)
    fun observeLoadingStatus()
}

// Utility to aggregate contributions by login
fun List<User>.aggregate(): List<User> {
    return this.groupBy { it.login }
        .map { (login, group) -> User(login, group.sumOf { it.contributions }) }
        .sortedByDescending { it.contributions }
}

// Extension to handle job completion/cancellation callback
fun Job.setUpCancellation(onCancel: () -> Unit) {
    invokeOnCompletion { throwable ->
        if (throwable is CancellationException) {
            onCancel()
        }
    }
}
