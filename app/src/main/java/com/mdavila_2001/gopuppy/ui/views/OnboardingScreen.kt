package com.mdavila_2001.gopuppy.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mdavila_2001.gopuppy.R
import com.mdavila_2001.gopuppy.data.classes.OnboardingPage
import com.mdavila_2001.gopuppy.ui.NavRoutes
import com.mdavila_2001.gopuppy.ui.components.global.buttons.Button
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navController: NavController) {
    val pages = listOf(
        OnboardingPage(
            "¡Bienvenido a GoPuppy!",
            "Conecta con paseadores de confianza y descubre los mejores lugares para tu mascota.",
            R.drawable.onboarding_1
        ),
        OnboardingPage(
            "Paseadores Verificados",
            "Encuentra paseadores cariñosos en tu zona, garantizando tranquilidad y seguridad.",
            R.drawable.onboarding_2
        ),
        OnboardingPage(
            "Elige tu Aventura",
            "¿Buscas quién cuide a tu amigo o quieres ganar dinero paseando? Aquí puedes hacer ambos.",
            R.drawable.onboarding_3
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                OnboardingPageContent(page = pages[pageIndex])
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.height(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { i ->
                    val color = if (pagerState.currentPage == i) {
                        Color(0xFF29B6CE)
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    }

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(if (pagerState.currentPage == i) 12.dp else 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            if (pagerState.currentPage == pages.lastIndex) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GoPuppyTheme(role = "owner") {
                        Button(
                            text = "Soy Dueño",
                            onClick = { navController.navigate(NavRoutes.Login.route) }
                        )
                    }

                    GoPuppyTheme(role = "walker") {
                        Button(
                            text = "Soy Paseador",
                            onClick = { navController.navigate(NavRoutes.Login.route) }
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF29B6CE),
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(
                        text = "Siguiente",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pages.lastIndex) }
                    }
                ) {
                    Text(
                        text = "Saltar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(1f),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    GoPuppyTheme {
        OnboardingScreen(navController = NavController(LocalContext.current) )
    }
}