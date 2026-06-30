package contributors

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

// Fetches contributors concurrently and sends updates through a callback
suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) = coroutineScope {
    val reposResponse = service.getOrgRepos(req.org)
    val repos = reposResponse.body() ?: emptyList()
    
    val channel = Channel<List<User>>()
    
    // Launch a coroutine for each repository to fetch its contributors concurrently
    for (repo in repos) {
        launch(Dispatchers.IO) {
            val contributorsResponse = service.getRepoContributors(req.org, repo.name)
            val users = contributorsResponse.body() ?: emptyList()
            channel.send(users)
        }
    }
    
    var allUsers = emptyList<User>()
    
    // Receive contributions from the channel and aggregate them
    for (i in 0 until repos.size) {
        val users = channel.receive()
        allUsers = (allUsers + users).aggregate()
        updateResults(allUsers, i == repos.lastIndex)
    }
    
    channel.close() // Close the channel when done!
}
