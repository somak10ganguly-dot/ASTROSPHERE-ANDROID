
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
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

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
    var module by remember { mutableStateOf("APOD") }

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
                            label = { Text(name, fontSize = 10.sp) }
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
                    0 -> HomeScreen {
                        selected = 1
                        module = "APOD"
                    }

                    1 -> ExploreScreen(
                        onSelect = { module = it },
                        selectedModule = module
                    )

                    2 -> MissionsScreen()

                    3 -> ResearchScreen(
                        onSelect = { module = it },
                        selectedModule = module
                    )
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
        fontSize = 10.sp
    )

    Spacer(Modifier.height(24.dp))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF132C4B)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(20.dp)) {

            Text(
                "MISSION CONTROL",
                color = Blue,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Explore the Universe.",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Your scientific gateway to planets, stars, galaxies and space exploration.",
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
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(12.dp))

    val systems = listOf(
        Triple("01", "APOD", "NASA Astronomy Picture of the Day"),
        Triple("02", "ASTEROIDS", "Near-Earth object tracker"),
        Triple("03", "GAIA STARS", "ESA stellar catalogue")
    )

    systems.forEach { item ->
        ObservatoryCard(
            item.first,
            item.second,
            item.third,
            "LIVE DATA"
        )
    }
}

@Composable
fun ObservatoryCard(
    number: String,
    title: String,
    description: String,
    source: String,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        enabled = onClick != null,
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

            Text("›", color = Blue, fontSize = 24.sp)
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
fun ExploreScreen(
    onSelect: (String) -> Unit,
    selectedModule: String
) {
    SectionTitle(
        "Explore Space",
        "Select a live astronomical data module"
    )

    ObservatoryCard(
        "01", "APOD",
        "NASA daily astronomical image and explanation",
        "NASA",
        onClick = { onSelect("APOD") }
    )

    ObservatoryCard(
        "02", "ASTEROIDS",
        "Near-Earth objects and orbital information",
        "NASA NEO",
        onClick = { onSelect("ASTEROIDS") }
    )

    ObservatoryCard(
        "03", "GAIA STARS",
        "ESA Gaia DR3 star measurements",
        "ESA",
        onClick = { onSelect("GAIA") }
    )

    Spacer(Modifier.height(20.dp))

    when (selectedModule) {
        "APOD" -> ApodViewer()
        "ASTEROIDS" -> AsteroidViewer()
        "GAIA" -> GaiaViewer()
    }
}

@Composable
fun ResearchScreen(
    onSelect: (String) -> Unit,
    selectedModule: String
) {
    SectionTitle(
        "Research Laboratory",
        "Live scientific data and observations"
    )

    ObservatoryCard(
        "01", "ASTRONOMY DATA",
        "Daily image and astronomical explanation",
        "NASA",
        onClick = { onSelect("APOD") }
    )

    ObservatoryCard(
        "02", "ORBITAL SCIENCE",
        "Near-Earth asteroid observations",
        "NASA",
        onClick = { onSelect("ASTEROIDS") }
    )

    ObservatoryCard(
        "03", "STELLAR DATA",
        "Gaia star catalogue measurements",
        "ESA",
        onClick = { onSelect("GAIA") }
    )

    Spacer(Modifier.height(20.dp))

    when (selectedModule) {
        "APOD" -> ApodViewer()
        "ASTEROIDS" -> AsteroidViewer()
        "GAIA" -> GaiaViewer()
    }
}

@Composable
fun MissionsScreen() {
    SectionTitle(
        "Mission Centre",
        "Space agencies and exploration programmes"
    )

    val missions = listOf(
        Triple("01", "JAMES WEBB", "Infrared space observatory"),
        Triple("02", "JUICE", "Jupiter and icy moons explorer"),
        Triple("03", "EUCLID", "Dark matter and dark energy survey"),
        Triple("04", "ARTEMIS", "Human lunar exploration")
    )

    missions.forEach {
        ObservatoryCard(
            it.first,
            it.second,
            it.third,
            "NASA / ESA"
        )
    }
}

@Composable
fun ApodViewer() {

    var data by remember { mutableStateOf<Apod?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            loading = true
            error = null

            try {
                data = SpaceData.getApod()
            } catch (e: Exception) {
                error = e.message ?: "Unable to load APOD"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        load()
    }

    ModuleHeading("NASA APOD", "Astronomy Picture of the Day")

    Button(
        onClick = { load() },
        enabled = !loading
    ) {
        Text("REFRESH APOD")
    }

    if (loading) {
        LoadingIndicator()
    }

    error?.let {
        ErrorPanel(it)
    }

    data?.let { apod ->

        Text(
            apod.title,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            apod.date,
            color = Blue
        )

        Spacer(Modifier.height(12.dp))

        if (apod.imageUrl.isNotBlank()) {
            AsyncImage(
                model = apod.imageUrl,
                contentDescription = apod.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            apod.explanation,
            color = Muted,
            fontSize = 13.sp
        )
    }
}

@Composable
fun AsteroidViewer() {

    var data by remember {
        mutableStateOf<List<Asteroid>>(emptyList())
    }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            loading = true
            error = null

            try {
                data = SpaceData.getAsteroids()
            } catch (e: Exception) {
                error = e.message ?: "Unable to load asteroids"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        load()
    }

    ModuleHeading(
        "ASTEROID TRACKER",
        "NASA Near-Earth Objects"
    )

    Button(
        onClick = { load() },
        enabled = !loading
    ) {
        Text("REFRESH ASTEROIDS")
    }

    if (loading) LoadingIndicator()

    error?.let { ErrorPanel(it) }

    data.forEachIndexed { index, asteroid ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Panel
            )
        ) {
            Column(Modifier.padding(14.dp)) {

                Text(
                    "${index + 1}. ${asteroid.name}",
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )

                Text("Diameter: ${asteroid.diameter} km")
                Text("Velocity: ${asteroid.velocity} km/h")
                Text("Distance: ${asteroid.distance} km")

                Text(
                    if (asteroid.hazardous)
                        "Potentially hazardous"
                    else
                        "Not classified as potentially hazardous",
                    color = if (asteroid.hazardous)
                        Color(0xFFFF7777)
                    else
                        Color(0xFF8BE0A2)
                )
            }
        }
    }
}

@Composable
fun GaiaViewer() {

    var data by remember {
        mutableStateOf<List<GaiaStar>>(emptyList())
    }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            loading = true
            error = null

            try {
                data = SpaceData.getGaiaStars()
            } catch (e: Exception) {
                error = e.message ?: "Unable to load Gaia data"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        load()
    }

    ModuleHeading(
        "GAIA STAR CATALOGUE",
        "ESA Gaia DR3 observations"
    )

    Button(
        onClick = { load() },
        enabled = !loading
    ) {
        Text("REFRESH STAR DATA")
    }

    if (loading) LoadingIndicator()

    error?.let { ErrorPanel(it) }

    data.forEach { star ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Panel
            )
        ) {
            Column(Modifier.padding(14.dp)) {

                Text(
                    "Source ID: ${star.id}",
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                Text("Right Ascension: ${star.ra}")
                Text("Declination: ${star.dec}")
                Text("Magnitude: ${star.magnitude}")
                Text("Parallax: ${star.parallax}")
            }
        }
    }
}

@Composable
fun ModuleHeading(title: String, subtitle: String) {
    Spacer(Modifier.height(10.dp))

    Text(
        title,
        color = Blue,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold
    )

    Text(
        subtitle,
        color = Muted,
        fontSize = 12.sp
    )

    Spacer(Modifier.height(12.dp))
}

@Composable
fun LoadingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = Blue,
            strokeWidth = 3.dp
        )

        Spacer(Modifier.width(12.dp))

        Text(
            "Connecting to space data...",
            color = Muted
        )
    }

    Spacer(Modifier.height(12.dp))
}

@Composable
fun ErrorPanel(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF351D2A)
        )
    ) {
        Text(
            "DATA CONNECTION ERROR\n$message",
            modifier = Modifier.padding(14.dp),
            color = Color(0xFFFFAAAA),
            fontSize = 12.sp
        )
    }
}
