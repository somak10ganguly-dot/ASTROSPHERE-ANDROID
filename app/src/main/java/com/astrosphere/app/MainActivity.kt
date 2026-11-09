package com.astrosphere.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SpaceBlack = Color(0xFF070B16)
private val Panel = Color(0xFF111C30)
private val Blue = Color(0xFF55B8FF)
private val Muted = Color(0xFF9AABC4)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AstroSphereApp()
        }
    }
}

@Composable
fun AstroSphereApp() {
    var selected by remember { mutableIntStateOf(0) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Blue,
            background = SpaceBlack,
            surface = Panel,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Scaffold(
            containerColor = SpaceBlack,
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0C1424)
                ) {
                    val names = listOf(
                        "Home", "Explore", "Missions", "Research"
                    )
                    val symbols = listOf("⌂", "◎", "◈", "⌘")

                    names.forEachIndexed { index, name ->
                        NavigationBarItem(
                            selected = selected == index,
                            onClick = { selected = index },
                            icon = {
                                Text(symbols[index], fontSize = 21.sp)
                            },
                            label = {
                                Text(name, fontSize = 10.sp)
                            }
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                when (selected) {
                    0 -> HomeScreen(
                        onExplore = { selected = 1 }
                    )
                    1 -> ExploreScreen()
                    2 -> MissionsScreen()
                    3 -> ResearchScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onExplore: () -> Unit) {
    Text(
        "ASTROSPHERE",
        color = Blue,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 3.sp
    )

    Spacer(Modifier.height(8.dp))

    Text(
        "SPACE OBSERVATORY",
        fontSize = 27.sp,
        fontWeight = FontWeight.Bold
    )

    Text(
        "NASA • ESA • DEEP SPACE RESEARCH",
        color = Muted,
        fontSize = 10.sp,
        letterSpacing = 1.sp
    )

    Spacer(Modifier.height(24.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF132C4B),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                "MISSION CONTROL",
                color = Blue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Explore the Universe.",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Your scientific gateway to planets, " +
                "stars, galaxies and space exploration.",
                color = Color.LightGray,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(18.dp))

            Button(onClick = onExplore) {
                Text("ENTER OBSERVATORY")
            }
        }
    }

    Spacer(Modifier.height(22.dp))

    Text(
        "OBSERVATORY SYSTEMS",
        color = Blue,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(12.dp))

    ObservatoryCard(
        "01",
        "ASTRONOMY",
        "Daily images and deep-space discoveries",
        "NASA"
    )

    ObservatoryCard(
        "02",
        "SOLAR SYSTEM",
        "Planets, moons and orbital science",
        "NASA"
    )

    ObservatoryCard(
        "03",
        "ASTEROID TRACKER",
        "Near-Earth objects and close approaches",
        "NASA"
    )

    ObservatoryCard(
        "04",
        "SPACE WEATHER",
        "Solar activity and geomagnetic events",
        "NASA + ESA"
    )

    ObservatoryCard(
        "05",
        "MILKY WAY",
        "Stellar catalogue and star measurements",
        "ESA GAIA"
    )

    ObservatoryCard(
        "06",
        "EARTH OBSERVATORY",
        "Earth imagery and natural events",
        "NASA"
    )

    Spacer(Modifier.height(18.dp))

    Text(
        "DATA CONNECTION: NOT YET INITIALISED",
        color = Color(0xFFFFC76A),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(8.dp))

    Text(
        "NASA and ESA live connections will be activated " +
        "in the next development stages.",
        color = Muted,
        fontSize = 12.sp
    )
}

@Composable
fun ObservatoryCard(
    number: String,
    title: String,
    description: String,
    source: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Panel
        )
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                number,
                color = Blue,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    description,
                    color = Muted,
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    source,
                    color = Blue,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String) {
    Text(
        "ASTROSPHERE 2.0",
        color = Blue,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(20.dp))

    Text(
        title,
        fontSize = 27.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(6.dp))

    Text(
        subtitle,
        color = Muted,
        fontSize = 13.sp
    )

    Spacer(Modifier.height(20.dp))
}

@Composable
fun ExploreScreen() {
    SectionTitle(
        "Explore Space",
        "Astronomical objects and scientific discoveries"
    )

    ObservatoryCard(
        "01", "PLANETS",
        "Planetary systems and orbital science", "NASA"
    )

    ObservatoryCard(
        "02", "STARS",
        "Stellar properties and measurements", "ESA GAIA"
    )

    ObservatoryCard(
        "03", "GALAXIES",
        "Galaxies, nebulae and deep space", "NASA"
    )

    ObservatoryCard(
        "04", "EXOPLANETS",
        "Worlds beyond our solar system", "NASA"
    )

    ObservatoryCard(
        "05", "ASTEROIDS",
        "Near-Earth object observations", "NASA"
    )
}

@Composable
fun MissionsScreen() {
    SectionTitle(
        "Mission Centre",
        "Space agencies and exploration programmes"
    )

    ObservatoryCard(
        "01", "JAMES WEBB",
        "Infrared space observatory", "NASA / ESA / CSA"
    )

    ObservatoryCard(
        "02", "JUICE",
        "Jupiter and its icy moons explorer", "ESA"
    )

    ObservatoryCard(
        "03", "EUCLID",
        "Dark matter and dark energy survey", "ESA"
    )

    ObservatoryCard(
        "04", "ARTEMIS",
        "Human lunar exploration programme", "NASA"
    )
}

@Composable
fun ResearchScreen() {
    SectionTitle(
        "Research Laboratory",
        "Tools for astronomical investigation"
    )

    ObservatoryCard(
        "01", "STELLAR DATA",
        "Explore Gaia star measurements", "ESA"
    )

    ObservatoryCard(
        "02", "ORBITAL SCIENCE",
        "Planetary and asteroid calculations", "ASTROSPHERE"
    )

    ObservatoryCard(
        "03", "SPACE WEATHER",
        "Investigate solar events", "NASA / ESA"
    )

    ObservatoryCard(
        "04", "DATA ARCHIVE",
        "Saved research and observations", "LOCAL"
    )
}
