package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TriviaQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

data class LiveMetricsPayload(
    val apiName: String,
    val eccentricity: Double?,
    val inclinationDegrees: Double?,
    val axialTiltDegrees: Double?,
    val meanRadiusKm: Double?,
    val gravityScore: Double?,
    val resolvedTempKelvin: Double?,
    val discoveredBy: String?,
    val discoveryDate: String?,
    val moonsTracked: Int?
)

class SpaceViewModel : ViewModel() {
    private val repository = SpaceRepository()

    // Screen Modes: "KIDS" vs "ADULTS"
    private val _explorationMode = MutableStateFlow("ADULTS")
    val explorationMode: StateFlow<String> = _explorationMode.asStateFlow()

    // Primary selection
    private val _selectedPlanet = MutableStateFlow(CelestialDatabase.Planets.first { it.id == "earth" })
    val selectedPlanet: StateFlow<Planet> = _selectedPlanet.asStateFlow()

    // Public API data overlays
    private val _apiSyncStates = MutableStateFlow<Map<String, ApiSyncState>>(emptyMap())
    val apiSyncStates: StateFlow<Map<String, ApiSyncState>> = _apiSyncStates.asStateFlow()

    private val _livePlanetMetrics = MutableStateFlow<Map<String, LiveMetricsPayload>>(emptyMap())
    val livePlanetMetrics: StateFlow<Map<String, LiveMetricsPayload>> = _livePlanetMetrics.asStateFlow()

    // Orbital Clock Simulation parameters
    private val _isSimulatingOrbits = MutableStateFlow(true)
    val isSimulatingOrbits: StateFlow<Boolean> = _isSimulatingOrbits.asStateFlow()

    // Speed multiplier: units in visual earth-days passing per real second
    private val _orbitSpeedMultiplier = MutableStateFlow(5f) // default to 5 simulated Earth days per real second
    val orbitSpeedMultiplier: StateFlow<Float> = _orbitSpeedMultiplier.asStateFlow()

    private val _simulatedEarthDays = MutableStateFlow(0.0)
    val simulatedEarthDays: StateFlow<Double> = _simulatedEarthDays.asStateFlow()

    // Rocket Journey Simulator
    private val _activeRocketDestination = MutableStateFlow<Planet?>(null)
    val activeRocketDestination: StateFlow<Planet?> = _activeRocketDestination.asStateFlow()

    private val _rocketFlightProgress = MutableStateFlow(0f)
    val rocketFlightProgress: StateFlow<Float> = _rocketFlightProgress.asStateFlow()

    // Interactive Quiz Bank
    val quizQuestions = listOf(
        TriviaQuestion(
            question = "Which giant planet could float in a massive bathtub like a rubber ducky? 🛁🦆",
            options = listOf("Jupiter", "Mars", "Saturn", "Mercury"),
            correctAnswerIndex = 2,
            explanation = "Saturn is primarily a giant ball of gas with a density lower than liquid water. If you found a big enough ocean, it would float on top!"
        ),
        TriviaQuestion(
            question = "Which planet has howling storms with winds blowing faster than a bullet train? 🌬️🌀",
            options = listOf("Earth", "Neptune", "Venus", "Mercury"),
            correctAnswerIndex = 1,
            explanation = "Neptune is the windiest world! Supersonic gale winds blow up to 2,100 km/h in its blue atmosphere, faster than the speed of sound!"
        ),
        TriviaQuestion(
            question = "Which planet is tilting so much that it rolls around the sun on its side? 🔄💤",
            options = listOf("Uranus", "Jupiter", "Saturn", "Mars"),
            correctAnswerIndex = 0,
            explanation = "Uranus has an extreme tilt of 98 degrees. It effectively rolls along its orbital path like a giant rolling ice ball!"
        ),
        TriviaQuestion(
            question = "What is the name of the monster, 3-times Earth's giant volcano on Mars? 🌋🔴",
            options = listOf("Mount Everest", "Olympus Mons", "Mauna Kea", "Fuji Crater"),
            correctAnswerIndex = 1,
            explanation = "Olympus Mons is a massive shield volcano on Mars, rising three times higher than Mount Everest - making it the tallest in the Solar System!"
        ),
        TriviaQuestion(
            question = "Which planet is are we standing on right now that has rich liquid water oceans? 🌊💙",
            options = listOf("Venus", "Mars", "Earth", "Saturn"),
            correctAnswerIndex = 2,
            explanation = "Earth! Over 70% of Earth is sparkling water, enabling life as we know it."
        )
    )

    private val _quizQuestionIndex = MutableStateFlow(0)
    val quizQuestionIndex: StateFlow<Int> = _quizQuestionIndex.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    private val _confettiTrigger = MutableStateFlow(0) // Trigger state increments
    val confettiTrigger: StateFlow<Int> = _confettiTrigger.asStateFlow()

    // Background Orbit Clock thread
    private var orbitClockJob: Job? = null
    private var rocketAnimationJob: Job? = null

    init {
        startOrbitSimulation()
        // Proactively fetch Earth's initial live parameters to verify endpoint
        syncLiveSpaceData(CelestialDatabase.Planets.first { it.id == "earth" })
    }

    fun setExplorationMode(mode: String) {
        _explorationMode.value = mode
    }

    fun selectPlanet(planet: Planet) {
        if (_selectedPlanet.value.id != planet.id) {
            _selectedPlanet.value = planet
            cancelActiveRocket()
            syncLiveSpaceData(planet)
        }
    }

    fun toggleOrbitSimulation() {
        _isSimulatingOrbits.value = !_isSimulatingOrbits.value
    }

    fun setSpeedMultiplier(multiplier: Float) {
        _orbitSpeedMultiplier.value = multiplier
    }

    private fun startOrbitSimulation() {
        orbitClockJob?.cancel()
        orbitClockJob = viewModelScope.launch {
            var lastTime = System.currentTimeMillis()
            while (true) {
                if (_isSimulatingOrbits.value) {
                    val now = System.currentTimeMillis()
                    val deltaSec = (now - lastTime) / 1000.0
                    // Increments visual Earth days passed
                    val deltaDays = deltaSec * _orbitSpeedMultiplier.value
                    _simulatedEarthDays.value += deltaDays
                }
                lastTime = System.currentTimeMillis()
                delay(30) // ~30 fps updates to reduce cpu load
            }
        }
    }

    // Dynamic fetch from solar system open api
    fun syncLiveSpaceData(planet: Planet) {
        val pid = planet.id
        // Check if already synced to avoid redundant fetches
        if (_apiSyncStates.value[pid] is ApiSyncState.Synced) return

        _apiSyncStates.value = _apiSyncStates.value + (pid to ApiSyncState.Syncing)

        viewModelScope.launch {
            val response = repository.fetchLiveCelestialBody(pid)
            if (response != null) {
                val overlay = LiveMetricsPayload(
                    apiName = response.name,
                    eccentricity = response.eccentricity,
                    inclinationDegrees = response.inclination,
                    axialTiltDegrees = response.axialTilt,
                    meanRadiusKm = response.meanRadius,
                    gravityScore = response.gravity,
                    resolvedTempKelvin = response.avgTemp,
                    discoveredBy = response.discoveredBy,
                    discoveryDate = response.discoveryDate,
                    moonsTracked = response.moons?.size
                )
                _livePlanetMetrics.value = _livePlanetMetrics.value + (pid to overlay)
                _apiSyncStates.value = _apiSyncStates.value + (pid to ApiSyncState.Synced(
                    apiSource = "Système Solaire REST v1",
                    lastUpdated = System.currentTimeMillis()
                ))
            } else {
                _apiSyncStates.value = _apiSyncStates.value + (pid to ApiSyncState.Failed("API timeout. Working offline."))
            }
        }
    }

    // Rocket journey logic
    fun launchRocketJourney(destination: Planet) {
        if (destination.id == "earth") return // Already home!
        cancelActiveRocket()
        _activeRocketDestination.value = destination
        _rocketFlightProgress.value = 0f

        rocketAnimationJob = viewModelScope.launch {
            var progress = 0f
            while (progress < 1.0f) {
                delay(40)
                progress += 0.02f
                _rocketFlightProgress.value = progress.coerceAtMost(1.0f)
            }
            triggerConfettiBurst()
        }
    }

    fun cancelActiveRocket() {
        rocketAnimationJob?.cancel()
        _activeRocketDestination.value = null
        _rocketFlightProgress.value = 0f
    }

    // Interactive Quiz answers
    fun selectQuizAnswer(index: Int) {
        if (_selectedAnswerIndex.value != null) return // Already locked in
        _selectedAnswerIndex.value = index

        val activeQuestion = quizQuestions[_quizQuestionIndex.value]
        if (index == activeQuestion.correctAnswerIndex) {
            _quizScore.value += 1
            triggerConfettiBurst()
        }
    }

    fun advanceQuizQuestion() {
        _selectedAnswerIndex.value = null
        val nextIdx = _quizQuestionIndex.value + 1
        if (nextIdx < quizQuestions.size) {
            _quizQuestionIndex.value = nextIdx
        } else {
            _quizCompleted.value = true
        }
    }

    fun resetQuiz() {
        _quizQuestionIndex.value = 0
        _selectedAnswerIndex.value = null
        _quizScore.value = 0
        _quizCompleted.value = false
    }

    private fun triggerConfettiBurst() {
        _confettiTrigger.value += 1
    }

    override fun onCleared() {
        super.onCleared()
        orbitClockJob?.cancel()
        rocketAnimationJob?.cancel()
    }
}
