package net.adhikary.mrtbuddy.ui.screens.farecalculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.ui.screens.components.FareDisplayCard
import net.adhikary.mrtbuddy.ui.screens.components.StationSelectionSection
import net.adhikary.mrtbuddy.ui.screens.components.TravelInfoCard
import net.adhikary.mrtbuddy.ui.screens.components.QuickTipsCard
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun FareCalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: FareCalculatorViewModel = koinViewModel(),
    cardState: CardState
) {
    val uiState by viewModel.state.collectAsState()
    var headerVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    // Animation specs
    val headerAnimSpec = spring<androidx.compose.ui.unit.IntOffset>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val contentAnimSpec = tween<androidx.compose.ui.unit.IntOffset>(400, delayMillis = 100)
    val infoAnimSpec = tween<androidx.compose.ui.unit.IntOffset>(400, delayMillis = 300)
    val tipsAnimSpec = tween<androidx.compose.ui.unit.IntOffset>(400, delayMillis = 400)
    val fadeInAnimSpec = tween<Float>(500, delayMillis = 200)
    val scaleInAnimSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    // Animate entrance
    LaunchedEffect(Unit) {
        headerVisible = true
        delay(200)
        contentVisible = true
    }

    // Update card state when it changes
    LaunchedEffect(cardState) {
        viewModel.onAction(FareCalculatorAction.UpdateCardState(cardState))
    }

    // Initialize ViewModel
    LaunchedEffect(Unit) {
        viewModel.onAction(FareCalculatorAction.OnInit)
    }

    // Handle events from ViewModel
    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is FareCalculatorEvent.Error -> {
                    // Handle error event
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background // Set layout background to theme background
            )
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(key = "header") {
                AnimatedVisibility(
                    visible = headerVisible,
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = headerAnimSpec
                    ) + fadeIn()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background // Use layout background for header card
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.size(56.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = "Fare Calculator",
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "MRT Fare Calculator",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Plan your journey • Check fares • Track balance",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            item(key = "stationSelection") {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = contentAnimSpec
                    ) + fadeIn(animationSpec = fadeInAnimSpec)
                ) {
                    StationSelectionSection(uiState, viewModel)
                }
            }

            item(key = "fareDisplay") {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = scaleIn(
                        initialScale = 0.8f,
                        animationSpec = scaleInAnimSpec
                    ) + fadeIn(animationSpec = fadeInAnimSpec)
                ) {
                    FareDisplayCard(uiState, viewModel)
                }
            }

            if (uiState.fromStation != null && uiState.toStation != null) {
                item(key = "travelInfo") {
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = infoAnimSpec
                        ) + fadeIn(animationSpec = fadeInAnimSpec)
                    ) {
                        TravelInfoCard(uiState)
                    }
                }
            }

            item(key = "tips") {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tipsAnimSpec
                    ) + fadeIn(animationSpec = fadeInAnimSpec)
                ) {
                    QuickTipsCard()
                }
            }

            item(key = "bottomPad") {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
