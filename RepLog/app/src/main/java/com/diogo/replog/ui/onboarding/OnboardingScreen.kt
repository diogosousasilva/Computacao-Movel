package com.diogo.replog.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diogo.replog.data.preferences.OnboardingDataStore
import com.diogo.replog.data.repository.UserRepository
import com.diogo.replog.ui.theme.*
import kotlinx.coroutines.launch

// ─── Design tokens ────────────────────────────────────────────────────────────
private val OrangeAccent = Color(0xFFFF6B35)
private val OrangeAccentLight = Color(0x1AFF6B35)
private val SelectionBlue = Blue40
private val SelectionBlueLight = Color(0x1A1A6BB5)

// ─── Main composable ──────────────────────────────────────────────────────────

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val totalPages = 8
    val pagerState = rememberPagerState { totalPages }
    val userRepository = remember { UserRepository() }

    // User selections state
    var selectedUnit by remember { mutableStateOf("kg") }
    var selectedGender by remember { mutableStateOf("") }
    var selectedHeightCm by remember { mutableIntStateOf(175) }
    var selectedWeight by remember { mutableFloatStateOf(70f) }
    var selectedFrequency by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableStateOf("") }
    var selectedExperience by remember { mutableStateOf("") }

    // Notification permission launcher
    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // After granting (or not), finish onboarding
        scope.launch {
            OnboardingDataStore.completeOnboarding(
                context = context,
                unit = selectedUnit,
                gender = selectedGender,
                weightKg = selectedWeight,
                goal = selectedGoal,
                experience = selectedExperience,
                heightCm = selectedHeightCm,
                weeklyFrequency = selectedFrequency
            )
            // Save to Firestore
            userRepository.updateProfile(mapOf(
                "weightKg" to selectedWeight,
                "gender" to selectedGender,
                "goal" to selectedGoal,
                "experience" to selectedExperience,
                "heightCm" to selectedHeightCm,
                "weeklyFrequency" to selectedFrequency
            ))
            onFinished()
        }
    }

    fun goNext() {
        scope.launch {
            if (pagerState.currentPage < totalPages - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    // Determine if current page has a valid selection
    val canContinue = when (pagerState.currentPage) {
        0 -> selectedUnit.isNotEmpty()
        1 -> selectedGender.isNotEmpty()
        2 -> true // height has default
        3 -> true // weight has default
        4 -> selectedFrequency.isNotEmpty()
        5 -> selectedGoal.isNotEmpty()
        6 -> selectedExperience.isNotEmpty()
        7 -> true
        else -> true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Navy5, Navy10, Navy20)))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Progress bar ──────────────────────────────────────────────
            OnboardingProgressBar(
                current = pagerState.currentPage + 1,
                total = totalPages,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )

            // ── Page content ──────────────────────────────────────────────
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> UnitsPage(selected = selectedUnit) { selectedUnit = it }
                    1 -> GenderPage(selected = selectedGender) { selectedGender = it }
                    2 -> HeightPage(heightCm = selectedHeightCm) { selectedHeightCm = it }
                    3 -> WeightPage(weightKg = selectedWeight) { selectedWeight = it }
                    4 -> FrequencyPage(selected = selectedFrequency) { selectedFrequency = it }
                    5 -> GoalPage(selected = selectedGoal) { selectedGoal = it }
                    6 -> ExperiencePage(selected = selectedExperience) { selectedExperience = it }
                    7 -> NotificationsPage()
                }
            }

            // ── Continue / Let's Go button ────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                val isLastPage = pagerState.currentPage == totalPages - 1
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (isLastPage) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    scope.launch {
                                        OnboardingDataStore.completeOnboarding(
                                            context = context,
                                            unit = selectedUnit,
                                            gender = selectedGender,
                                            weightKg = selectedWeight,
                                            goal = selectedGoal,
                                            experience = selectedExperience,
                                            heightCm = selectedHeightCm,
                                            weeklyFrequency = selectedFrequency
                                        )
                                        // Save to Firestore
                                        userRepository.updateProfile(mapOf(
                                            "weightKg" to selectedWeight,
                                            "gender" to selectedGender,
                                            "goal" to selectedGoal,
                                            "experience" to selectedExperience,
                                            "heightCm" to selectedHeightCm,
                                            "weeklyFrequency" to selectedFrequency
                                        ))
                                        onFinished()
                                    }
                                }
                            } else {
                                goNext()
                            }
                        },
                        enabled = canContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeAccent,
                            disabledContainerColor = OrangeAccent.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = if (isLastPage) "Let's Go! 🚀" else "Continue",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (isLastPage) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = {
                                scope.launch {
                                    OnboardingDataStore.completeOnboarding(
                                        context = context,
                                        unit = selectedUnit,
                                        gender = selectedGender,
                                        weightKg = selectedWeight,
                                        goal = selectedGoal,
                                        experience = selectedExperience,
                                        heightCm = selectedHeightCm,
                                        weeklyFrequency = selectedFrequency
                                    )
                                    // Save to Firestore
                                    userRepository.updateProfile(mapOf(
                                        "weightKg" to selectedWeight,
                                        "gender" to selectedGender,
                                        "goal" to selectedGoal,
                                        "experience" to selectedExperience,
                                        "heightCm" to selectedHeightCm,
                                        "weeklyFrequency" to selectedFrequency
                                    ))
                                    onFinished()
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "not right now",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Progress bar ─────────────────────────────────────────────────────────────

@Composable
private fun OnboardingProgressBar(current: Int, total: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(total) { index ->
            val isActive = index < current
            val animatedWidth: Dp by animateDpAsState(
                targetValue = if (isActive) 32.dp else 12.dp,
                animationSpec = tween(300),
                label = "bar_width"
            )
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(animatedWidth)
                    .clip(CircleShape)
                    .background(if (isActive) OrangeAccent else Color.White.copy(alpha = 0.2f))
            )
        }
    }
}

// ─── Selection card (transparent with border) ────────────────────────────────

@Composable
private fun SelectionCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderColor = if (selected) SelectionBlue else Color.White.copy(alpha = 0.15f)
    val bgColor = if (selected) SelectionBlueLight else Color.Transparent

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// ─── Page header ─────────────────────────────────────────────────────────────

@Composable
private fun PageHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

// ─── Page 1: Units ───────────────────────────────────────────────────────────

@Composable
private fun UnitsPage(selected: String, onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        PageHeader(
            title = "Choose Your\nUnit System",
            subtitle = "This is used to display weights throughout the app"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // KG card
            SelectionCard(
                selected = selected == "kg",
                onClick = { onSelect("kg") },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🏋️",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "KG",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (selected == "kg") SelectionBlue else Color.White
                    )
                    Text(
                        text = "Kilograms",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (selected == "kg") {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = SelectionBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // LBS card
            SelectionCard(
                selected = selected == "lbs",
                onClick = { onSelect("lbs") },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🇺🇸",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "LBS",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (selected == "lbs") SelectionBlue else Color.White
                    )
                    Text(
                        text = "Pounds",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (selected == "lbs") {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = SelectionBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Page 2: Gender ──────────────────────────────────────────────────────────

@Composable
private fun GenderPage(selected: String, onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        PageHeader(
            title = "What's Your\nGender?",
            subtitle = "Helps us calibrate your fitness metrics"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Male card
            SelectionCard(
                selected = selected == "male",
                onClick = { onSelect("male") },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected == "male") SelectionBlueLight
                                else Color.White.copy(alpha = 0.05f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("♂", fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Male",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (selected == "male") SelectionBlue else Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (selected == "male") {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = SelectionBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Female card
            SelectionCard(
                selected = selected == "female",
                onClick = { onSelect("female") },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected == "female") OrangeAccentLight
                                else Color.White.copy(alpha = 0.05f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("♀", fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Female",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (selected == "female") OrangeAccent else Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (selected == "female") {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = OrangeAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Page 3: Body Weight ─────────────────────────────────────────────────────

@Composable
private fun WeightPage(weightKg: Float, onWeightChange: (Float) -> Unit) {
    val weights = (30..200).toList()
    val initialIndex = (weightKg.toInt() - 30).coerceIn(weights.indices)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )

    // Center item calculation based on layout coordinates
    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) 0
            else {
                val centerOffset = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val closest = visibleItems.minByOrNull {
                    kotlin.math.abs((it.offset + it.size / 2) - centerOffset)
                }
                closest?.index ?: 0
            }
        }
    }

    // Scroll to the selected weight if the list is not scrolling
    LaunchedEffect(weightKg) {
        if (!listState.isScrollInProgress) {
            val targetIndex = (weightKg.toInt() - 30).coerceIn(weights.indices)
            listState.animateScrollToItem(targetIndex)
        }
    }

    // Update selected weight on scroll end or scroll change
    LaunchedEffect(centerIndex) {
        if (centerIndex in weights.indices) {
            onWeightChange(weights[centerIndex].toFloat())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeader(
            title = "What's Your\nBody Weight?",
            subtitle = "You can change this later in your profile"
        )

        // Large weight display
        Text(
            text = "${weightKg.toInt()} kg",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = OrangeAccent
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Scrollable weight picker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            // Center highlight box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrangeAccentLight)
                    .border(1.dp, OrangeAccent, RoundedCornerShape(12.dp))
            )

            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = 50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                items(weights.size) { index ->
                    val w = weights[index]
                    val isSelected = w.toFloat() == weightKg
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable {
                                onWeightChange(w.toFloat())
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$w",
                            style = if (isSelected) MaterialTheme.typography.titleLarge
                            else MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isSelected) OrangeAccent else Color.White.copy(alpha = 0.35f)
                        )
                    }
                }
            }
        }
    }
}

// ─── Page 4: Goals ───────────────────────────────────────────────────────────

private data class GoalOption(val id: String, val emoji: String, val label: String, val subtitle: String)

private val goals = listOf(
    GoalOption("muscle", "💪", "Build Muscle", "Hypertrophy & size gains"),
    GoalOption("strength", "🏆", "Get Stronger", "Increase max lifts & power"),
    GoalOption("endurance", "🏃", "Improve Endurance", "Stamina & cardiovascular fitness"),
    GoalOption("weight_loss", "🔥", "Lose Weight", "Fat loss & body recomposition"),
    GoalOption("general", "⚡", "General Fitness", "Stay healthy & active"),
)

@Composable
private fun GoalPage(selected: String, onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        PageHeader(
            title = "What's Your\nMain Goal?",
            subtitle = "We'll personalize your experience based on this"
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            goals.forEach { goal ->
                SelectionCard(
                    selected = selected == goal.id,
                    onClick = { onSelect(goal.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(goal.emoji, fontSize = 28.sp, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = goal.label,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected == goal.id) SelectionBlue else Color.White
                            )
                            Text(
                                text = goal.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                        if (selected == goal.id) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SelectionBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Page 5: Experience ──────────────────────────────────────────────────────

private data class ExperienceOption(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val subtitle: String
)

@Composable
private fun ExperiencePage(selected: String, onSelect: (String) -> Unit) {
    val options = listOf(
        ExperienceOption(
            "beginner", Icons.Filled.StarBorder,
            "Beginner", "Less than 1 year of training"
        ),
        ExperienceOption(
            "intermediate", Icons.Filled.Star,
            "Intermediate", "1–3 years of consistent training"
        ),
        ExperienceOption(
            "advanced", Icons.Filled.WorkspacePremium,
            "Advanced", "3+ years, serious athlete"
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        PageHeader(
            title = "Your Experience\nLevel",
            subtitle = "Be honest — it helps us tailor your coaching tips"
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            options.forEach { option ->
                SelectionCard(
                    selected = selected == option.id,
                    onClick = { onSelect(option.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected == option.id) SelectionBlueLight
                                    else Color.White.copy(alpha = 0.05f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                option.icon,
                                contentDescription = null,
                                tint = if (selected == option.id) SelectionBlue else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (selected == option.id) SelectionBlue else Color.White
                            )
                            Text(
                                text = option.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                        if (selected == option.id) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SelectionBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Page 6: Notifications ───────────────────────────────────────────────────

@Composable
private fun NotificationsPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Bell icon with glow
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(OrangeAccentLight),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(OrangeAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Stay in the Loop!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Enable notifications to get reminders,\nworkout streaks and AI coach insights.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Feature list
        listOf(
            "🏋️ Daily workout reminders",
            "🔥 Streak alerts to keep you consistent",
            "🤖 AI Coach tips & recommendations"
        ).forEach { feature ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ─── Height Page ─────────────────────────────────────────────────────────────

@Composable
private fun HeightPage(heightCm: Int, onHeightChange: (Int) -> Unit) {
    val heights = (100..250).toList()
    val initialIndex = (heightCm - 100).coerceIn(heights.indices)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )

    // Center item calculation based on layout coordinates
    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) 0
            else {
                val centerOffset = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val closest = visibleItems.minByOrNull {
                    kotlin.math.abs((it.offset + it.size / 2) - centerOffset)
                }
                closest?.index ?: 0
            }
        }
    }

    // Scroll to the selected height if the list is not scrolling
    LaunchedEffect(heightCm) {
        if (!listState.isScrollInProgress) {
            val targetIndex = (heightCm - 100).coerceIn(heights.indices)
            listState.animateScrollToItem(targetIndex)
        }
    }

    // Update selected height on scroll end or scroll change
    LaunchedEffect(centerIndex) {
        if (centerIndex in heights.indices) {
            onHeightChange(heights[centerIndex])
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeader(
            title = "What's Your\nHeight?",
            subtitle = "This helps us calculate your BMI and progression metrics"
        )

        // Large height display
        Text(
            text = "$heightCm cm",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = OrangeAccent
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Scrollable height picker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            // Center highlight box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrangeAccentLight)
                    .border(1.dp, OrangeAccent, RoundedCornerShape(12.dp))
            )

            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = 50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                items(heights.size) { index ->
                    val h = heights[index]
                    val isSelected = h == heightCm
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable {
                                onHeightChange(h)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$h",
                            style = if (isSelected) MaterialTheme.typography.titleLarge
                            else MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isSelected) OrangeAccent else Color.White.copy(alpha = 0.35f)
                        )
                    }
                }
            }
        }
    }
}

// ─── Weekly Frequency Page ───────────────────────────────────────────────────

private data class FrequencyOption(val id: String, val label: String, val subtitle: String)

@Composable
private fun FrequencyPage(selected: String, onSelect: (String) -> Unit) {
    val options = listOf(
        FrequencyOption("1-2x", "1 - 2x per week", "Light or starting routine"),
        FrequencyOption("3-4x", "3 - 4x per week", "Moderate, consistent training"),
        FrequencyOption("5x+", "5x+ per week", "Serious, high frequency routine")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        PageHeader(
            title = "How Often Do You\nTrain Weekly?",
            subtitle = "Helps us calibrate your progression recommendations"
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            options.forEach { option ->
                SelectionCard(
                    selected = selected == option.id,
                    onClick = { onSelect(option.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected == option.id) SelectionBlueLight
                                    else Color.White.copy(alpha = 0.05f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = if (selected == option.id) SelectionBlue else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (selected == option.id) SelectionBlue else Color.White
                            )
                            Text(
                                text = option.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                        if (selected == option.id) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SelectionBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

