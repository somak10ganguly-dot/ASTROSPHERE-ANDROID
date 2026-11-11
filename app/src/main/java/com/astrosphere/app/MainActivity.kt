
package com.astrosphere.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
private val Green = Color(0xFF8BE0A2)
private val Red = Color(0xFFFF7777)
private val Purple = Color(0xFFB69CFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = SpaceBlack,
                    surface = Panel,
                    primary = Blue,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                AstroSphereApp()
            }
        }
    }
}

@Composable
fun AstroSphereApp() {
    var selectedTab by remember { mutableStateOf("Home") }

    val tabs = listOf(
        "Home",
        "Explore",
        "Missions",
        "Research"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "ASTROSPHERE",
                color = Blue,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "SPACE OBSERVATORY 2.0",
                color = Muted,
                fontSize = 11.sp,
                letterSpacing = 3.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                Text(
                    text = tab,
                    color = if (selectedTab == tab) Blue else Muted,
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == tab) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    modifier = Modifier
                        .clickable { selectedTab = tab }
                        .padding(9.dp)
                )
            }
        }

        HorizontalDivider(color = Panel)

        when (selectedTab) {
            "Home" -> HomeScreen(
                onExplore = { selectedTab = "Explore" }
            )

            "Explore" -> ExploreScreen()

            "Missions" -> MissionsScreen()

            "Research" -> ResearchScreen()
        }
    }
}

@Composable
fun HomeScreen(onExplore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            "Explore the Universe",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Your gateway to NASA and ESA space data.",
            color = Muted
        )

        Spacer(Modifier.height(22.dp))

        CardPanel {
            Text(
                "ASTROSPHERE 2.0",
                color = Blue,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Explore astronomical observations, " +
                    "near-Earth objects, solar activity, " +
                    "Earth imagery and scientific missions.",
                color = Muted
            )

            Spacer(Modifier.height(15.dp))

            Button(
                onClick = onExplore,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                )
            ) {
                Text("Open Observatory", color = SpaceBlack)
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            "OBSERVATORY MODULES",
            color = Blue,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        val modules = listOf(
            "Astronomy Picture of the Day",
            "Near Earth Asteroids",
            "Space Weather",
            "Earth Observation - EPIC",
            "Earth Events",
            "NASA Image Search",
            "ESA Gaia Star Catalogue"
        )

        modules.forEachIndexed { index, module ->
            CardPanel {
                Text(
                    "${index + 1}. $module",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun ExploreScreen() {
    var selectedModule by remember {
        mutableStateOf("APOD")
    }

    val modules = listOf(
        "APOD",
        "Asteroids",
        "Space Weather",
        "EPIC",
        "Earth Events",
        "NASA Images",
        "Gaia"
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            modules.forEach { module ->
                Surface(
                    color = if (selectedModule == module) {
                        Blue
                    } else {
                        Panel
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable {
                        selectedModule = module
                    }
                ) {
                    Text(
                        module,
                        color = if (selectedModule == module) {
                            SpaceBlack
                        } else {
                            Muted
                        },
                        fontSize = 10.sp,
                        modifier = Modifier.padding(9.dp)
                    )
                }
            }
        }

        when (selectedModule) {
            "APOD" -> ApodScreen()
            "Asteroids" -> AsteroidScreen()
            "Space Weather" -> WeatherScreen()
            "EPIC" -> EpicScreen()
            "Earth Events" -> EarthEventsScreen()
            "NASA Images" -> NasaImagesScreen()
            "Gaia" -> GaiaScreen()
        }
    }
}

@Composable
fun ApodScreen() {
    var apod by remember { mutableStateOf<Apod?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loading = true
        try {
            apod = SpaceData.getApod()
            error = ""
        } catch (e: Exception) {
            error = e.message ?: "Unable to load APOD."
        } finally {
            loading = false
        }
    }

    ModuleColumn("Astronomy Picture of the Day") {
        if (loading) LoadingMessage()

        ErrorMessage(error)

        apod?.let {
            Text(
                it.title,
                color = Blue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(it.date, color = Muted, fontSize = 12.sp)

            Spacer(Modifier.height(12.dp))

            if (it.mediaType == "image" && it.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = it.imageUrl,
                    contentDescription = it.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                )
            } else {
                Text(
                    "Today's media is not an image.",
                    color = Muted
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                it.explanation,
                color = Muted,
                fontSize = 13.sp,
                lineHeight = 21.sp
            )
        }

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    try {
                        apod = SpaceData.getApod()
                        error = ""
                    } catch (e: Exception) {
                        error = e.message ?: "Unable to load APOD."
                    } finally {
                        loading = false
                    }
                }
            }
        ) {
            Text("Refresh")
        }
    }
}

@Composable
fun AsteroidScreen() {
    var asteroids by remember {
        mutableStateOf<List<Asteroid>>(emptyList())
    }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loading = true
        try {
            asteroids = SpaceData.getAsteroids()
        } catch (e: Exception) {
            error = e.message ?: "Unable to load asteroids."
        } finally {
            loading = false
        }
    }

    ModuleColumn("Near Earth Objects") {
        if (loading) LoadingMessage()
        ErrorMessage(error)

        asteroids.forEach { asteroid ->
            CardPanel {
                Text(
                    asteroid.name,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                InfoLine("Diameter", asteroid.diameter)
                InfoLine("Velocity", asteroid.velocity)
                InfoLine("Distance", asteroid.distance)

                Text(
                    if (asteroid.hazardous) {
                        "Potentially hazardous"
                    } else {
                        "Not classified as potentially hazardous"
                    },
                    color = if (asteroid.hazardous) Red else Green,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun WeatherScreen() {
    var events by remember {
        mutableStateOf<List<SpaceWeatherEvent>>(emptyList())
    }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loading = true
        try {
            events = SpaceData.getSpaceWeather()
        } catch (e: Exception) {
            error = e.message ?: "Unable to load space weather."
        } finally {
            loading = false
        }
    }

    ModuleColumn("NASA Space Weather") {
        if (loading) LoadingMessage()
        ErrorMessage(error)

        events.forEach { event ->
            CardPanel {
                Text(event.type, color = Blue, fontWeight = FontWeight.Bold)
                Text(event.date, color = Muted, fontSize = 11.sp)

                Spacer(Modifier.height(8.dp))

                Text(
                    event.message,
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun EpicScreen() {
    var images by remember {
        mutableStateOf<List<EarthImage>>(emptyList())
    }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loading = true
        try {
            images = SpaceData.getEpicImages()
        } catch (e: Exception) {
            error = e.message ?: "Unable to load EPIC images."
        } finally {
            loading = false
        }
    }

    ModuleColumn("Earth Observation - NASA EPIC") {
        if (loading) LoadingMessage()
        ErrorMessage(error)

        images.forEach { earth ->
            CardPanel {
                Text(
                    earth.caption,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                AsyncImage(
                    model = earth.imageUrl,
                    contentDescription = earth.caption,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 250.dp)
                )

                Text(
                    earth.identifier,
                    color = Muted,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun EarthEventsScreen() {
    var events by remember {
        mutableStateOf<List<EarthEvent>>(emptyList())
    }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loading = true
        try {
            events = SpaceData.getEarthEvents()
        } catch (e: Exception) {
            error = e.message ?: "Unable to load Earth events."
        } finally {
            loading = false
        }
    }

    ModuleColumn("Earth Events - EONET") {
        if (loading) LoadingMessage()
        ErrorMessage(error)

        events.forEach { event ->
            CardPanel {
                Text(
                    event.title,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                InfoLine("Category", event.category)
                InfoLine("Date", event.date)

                Text(
                    event.description,
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun NasaImagesScreen() {
    var search by remember { mutableStateOf("galaxy") }
    var images by remember {
        mutableStateOf<List<NasaImage>>(emptyList())
    }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    ModuleColumn("NASA Image Library") {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Search NASA images") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    error = ""

                    try {
                        images = SpaceData.searchNasaImages(search)
                    } catch (e: Exception) {
                        error = e.message ?: "Image search failed."
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = search.isNotBlank()
        ) {
            Text("Search")
        }

        if (loading) LoadingMessage()
        ErrorMessage(error)

        images.forEach { item ->
            CardPanel {
                Text(
                    item.title,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    )
                }

                Text(item.date, color = Muted, fontSize = 11.sp)

                Text(
                    item.description,
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun GaiaScreen() {
    var stars by remember {
        mutableStateOf<List<GaiaStar>>(emptyList())
    }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loading = true
        try {
            stars = SpaceData.getGaiaStars()
        } catch (e: Exception) {
            error = e.message ?: "Unable to load Gaia data."
        } finally {
            loading = false
        }
    }

    ModuleColumn("ESA Gaia DR3 Star Catalogue") {
        Text(
            "Selected stellar measurements from ESA Gaia Data Release 3.",
            color = Muted,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(12.dp))

        if (loading) LoadingMessage()
        ErrorMessage(error)

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    error = ""

                    try {
                        stars = SpaceData.getGaiaStars()
                    } catch (e: Exception) {
                        error = e.message ?: "Gaia request failed."
                    } finally {
                        loading = false
                    }
                }
            }
        ) {
            Text("Retry Gaia Connection")
        }

        stars.forEach { star ->
            CardPanel {
                Text(
                    "Source ID: ${star.sourceId}",
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                InfoLine("Right Ascension", star.ra)
                InfoLine("Declination", star.dec)
                InfoLine("G Magnitude", star.magnitude)
                InfoLine("Parallax", star.parallax)
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun MissionsScreen() {
    ModuleColumn("Space Missions") {
        val missions = listOf(
            "NASA Artemis" to "Human exploration of the Moon.",
            "James Webb Space Telescope" to
                "Observing the early universe and distant worlds.",
            "Mars Perseverance" to
                "Searching for evidence of ancient microbial life.",
            "Parker Solar Probe" to
                "Studying the Sun's corona and solar wind.",
            "ESA JUICE" to
                "Exploring Jupiter and its icy moons.",
            "ESA Gaia" to
                "Mapping the Milky Way in three dimensions."
        )

        missions.forEach { mission ->
            CardPanel {
                Text(
                    mission.first,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    mission.second,
                    color = Muted,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun ResearchScreen() {
    ModuleColumn("Research Laboratory") {
        val topics = listOf(
            "Astrophysics",
            "Cosmology",
            "Stellar Evolution",
            "Exoplanet Science",
            "Solar Physics",
            "Planetary Science",
            "Galactic Astronomy",
            "Space Exploration"
        )

        topics.forEach { topic ->
            CardPanel {
                Text(
                    topic,
                    color = Purple,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Research topic and scientific study area.",
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(9.dp))
        }
    }
}

@Composable
fun ModuleColumn(
    heading: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        content = content
    ) {
        Text(
            heading,
            color = Blue,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(15.dp))

        content()
    }
}

@Composable
fun CardPanel(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Panel
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            content = content
        )
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Muted, fontSize = 12.sp)

        Text(
            value,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun LoadingMessage() {
    Row(
        modifier = Modifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = Blue,
            strokeWidth = 2.dp
        )

        Spacer(Modifier.width(12.dp))

        Text("Connecting to space data services...", color = Muted)
    }
}

@Composable
fun ErrorMessage(message: String) {
    if (message.isNotBlank()) {
        CardPanel {
            Text(
                "DATA CONNECTION ERROR",
                color = Red,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(5.dp))

            Text(
                message,
                color = Muted,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(10.dp))
    }
}
