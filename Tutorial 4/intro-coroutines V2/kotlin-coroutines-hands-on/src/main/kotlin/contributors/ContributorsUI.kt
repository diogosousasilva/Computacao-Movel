package contributors

import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ContributorsUI : JFrame(), Contributors, CoroutineScope by CoroutineScope(Dispatchers.Main) {

    // Backing property pattern for StateFlow
    private val _loadingState = MutableStateFlow(LoadingStateData())
    override val loadingState: StateFlow<LoadingStateData> = _loadingState.asStateFlow()

    private val orgField = JTextField("kotlin", 15)
    private val loadButton = JButton("Load")
    private val cancelButton = JButton("Cancel")
    private val resultsArea = JTextArea(20, 40)
    private val statusLabel = JLabel("Loading status: init")

    private var job: Job? = null

    init {
        title = "GitHub Contributors (Coroutines & Channels)"
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        // Top Panel: inputs and buttons
        val topPanel = JPanel(FlowLayout())
        topPanel.add(JLabel("Organization:"))
        topPanel.add(orgField)
        topPanel.add(loadButton)
        topPanel.add(cancelButton)
        add(topPanel, BorderLayout.NORTH)

        // Center Panel: results
        resultsArea.isEditable = false
        add(JScrollPane(resultsArea), BorderLayout.CENTER)

        // Bottom Panel: status
        val bottomPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        bottomPanel.add(statusLabel)
        add(bottomPanel, BorderLayout.SOUTH)

        pack()
        setLocationRelativeTo(null)

        loadButton.addActionListener {
            loadData()
        }

        cancelButton.addActionListener {
            job?.cancel()
        }

        // Start observing loading status flow
        observeLoadingStatus()
    }

    override fun updateLoadingStatus(newStatus: LoadingStateData) {
        _loadingState.value = newStatus
    }

    override fun observeLoadingStatus() {
        launch {
            loadingState.collect { state ->
                val text = "Loading status: " + when (state.status) {
                    LoadingStatus.COMPLETED -> "completed in ${state.elapsedTime}"
                    LoadingStatus.IN_PROGRESS -> "in progress ${state.elapsedTime}"
                    LoadingStatus.CANCELED -> "canceled"
                    LoadingStatus.INIT -> "init"
                }
                statusLabel.text = text
            }
        }
    }

    private fun loadData() {
        val org = orgField.text.trim()
        if (org.isEmpty()) return

        job?.cancel()
        resultsArea.text = ""

        val service = RetrofitClient.gitHubService
        val req = RequestData(org, "", "")
        val startTime = System.currentTimeMillis()

        updateLoadingStatus(LoadingStateData(LoadingStatus.IN_PROGRESS, startTime))

        job = launch(Dispatchers.Default) {
            val progressChannel = Channel<Pair<List<User>, Boolean>>()
            
            // Fetch concurrently and send to progress channel
            launch(Dispatchers.Default) {
                try {
                    loadContributorsChannels(service, req) { users, completed ->
                        progressChannel.send(users to completed)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        resultsArea.text = "Error: ${e.message}"
                        updateLoadingStatus(LoadingStateData(LoadingStatus.CANCELED))
                    }
                } finally {
                    progressChannel.close()
                }
            }

            // Periodically check elapsed time and update UI loading status
            val timeJob = launch(Dispatchers.Default) {
                while (isActive) {
                    delay(100)
                    val elapsed = calculateElapsedTime(startTime)
                    withContext(Dispatchers.Main) {
                        updateLoadingStatus(LoadingStateData(LoadingStatus.IN_PROGRESS, startTime, elapsed))
                    }
                }
            }

            try {
                // Collect results from progress channel
                for ((users, completed) in progressChannel) {
                    withContext(Dispatchers.Main) {
                        updateResults(users, startTime, completed)
                    }
                }
            } finally {
                timeJob.cancel()
            }
        }

        job?.setUpCancellation {
            updateLoadingStatus(LoadingStateData(LoadingStatus.CANCELED))
        }
    }

    private fun updateResults(users: List<User>, startTime: Long, completed: Boolean) {
        val sb = StringBuilder()
        for ((index, user) in users.withIndex()) {
            sb.append("${index + 1}. ${user.login} (${user.contributions})\n")
        }
        resultsArea.text = sb.toString()

        if (completed) {
            val elapsed = calculateElapsedTime(startTime)
            updateLoadingStatus(LoadingStateData(LoadingStatus.COMPLETED, startTime, elapsed))
        }
    }

    private fun calculateElapsedTime(startTime: Long): String {
        val time = System.currentTimeMillis() - startTime
        return "${(time / 1000)}.${time % 1000 / 100} sec"
    }
}

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val gitHubService: GitHubService = retrofit.create(GitHubService::class.java)
}
