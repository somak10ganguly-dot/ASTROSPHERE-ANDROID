
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
private val Green = Color(0xFF8BE0A2)
private val Red = Color(0xFFFF7777)

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
                                Text(
                                    symbols[index],
                                    fontSize = 21.sp
                                )
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
                        onSelect = {
                            module = it
                            selected = 1
                        }
                    )

                    1 -> ExploreScreen(
                        selectedModule = module,
                        onSelect = { module = it }
                    )

                    2 -> MissionsScreen()

                    3 -> ResearchScreen(
                        selectedModule = module,
                        onSelect = { module = it }
                    )
                }
            }
        }
    }
}

// =====================================
// HOME SCREEN
// =====================================

@Composable
fun HomeScreen(onSelect: (String) -> Unit) {

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

            Button(onClick = { onSelect("APOD") }) {
                Text("ENTER OBSERVATORY")
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    Text(
        "OBSERVATORY SYSTEMS",
        color = Blue,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(12.dp))

    val systems = listOf(
        Triple("01", "APOD", "Daily astronomy image"),
        Triple("02", "ASTEROIDS", "Near-Earth object tracker"),
        Triple("03", "WEATHER", "Solar activity monitoring"),
        Triple("04", "EPIC", "Earth from deep space"),
        Triple("05", "EARTH", "Natural event monitoring"),
        Triple("06", "IMAGES", "NASA image search"),
        Triple("07", "GAIA", "ESA star catalogue")
    )

    systems.forEach {
        ObservatoryCard(
            it.first,
            it.second,
            it.third,
            "OPEN MODULE"
        ) {
            onSelect(it.second)
        }
    }
}

// =====================================
// REUSABLE OBSERVATORY CARD
// =====================================

@Composable
fun ObservatoryCard(
    number: String,
    title: String,
    description: String,
    source: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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

            Text("›", color = Blue, fontSize = 24.sp)
        }
    }
}

// =====================================
// SECTION HEADING
// =====================================

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

// =====================================
// EXPLORE SCREEN
// =====================================

@Composable
fun ExploreScreen(
    onSelect: (String) -> Unit,
    selectedModule: String
) {

    SectionTitle(
        "Explore Space",
        "Select a scientific observation module"
    )

    val modules = listOf(
        Triple("01", "APOD", "NASA astronomy picture"),
        Triple("02", "ASTEROIDS", "Near-Earth objects"),
        Triple("03", "WEATHER", "Solar flares and CMEs"),
        Triple("04", "EPIC", "Earth observation"),
        Triple("05", "EARTH", "Natural events"),
        Triple("06", "IMAGES", "NASA image library"),
        Triple("07", "GAIA", "ESA stellar catalogue")
    )

    modules.forEach {
        ObservatoryCard(
            it.first,
            it.second,
            it.third,
            "SELECT MODULE"
        ) {
            onSelect(it.second)
        }
    }

    Spacer(Modifier.height(20.dp))

    when (selectedModule) {
        "APOD" -> ApodViewer()
        "ASTEROIDS" -> AsteroidViewer()
        "WEATHER" -> WeatherViewer()
        "EPIC" -> EpicViewer()
        "EARTH" -> EarthEventsViewer()
        "IMAGES" -> NasaImageViewer()
        "GAIA" -> GaiaViewer()
    }
}

// =====================================
// RESEARCH SCREEN
// =====================================

@Composable
fun ResearchScreen(
    onSelect: (String) -> Unit,
    selectedModule: String
) {

    SectionTitle(
        "Research Laboratory",
        "Scientific observation and data analysis"
    )

    ObservatoryCard(
        "01", "SPACE WEATHER",
        "Solar flares and geomagnetic events",
        "NASA DONKI"
    ) {
        onSelect("WEATHER")
    }

    ObservatoryCard(
        "02", "ORBITAL SCIENCE",
        "Asteroid orbital observations",
        "NASA NEO"
    ) {
        onSelect("ASTEROIDS")
    }

    ObservatoryCard(
        "03", "STELLAR SCIENCE",
        "Star positions and measurements",
        "ESA GAIA"
    ) {
        onSelect("GAIA")
    }

    ObservatoryCard(
        "04", "EARTH SCIENCE",
        "Earth observation imagery",
        "NASA EPIC"
    ) {
        onSelect("EPIC")
    }

    Spacer(Modifier.height(20.dp))

    when (selectedModule) {
        "APOD" -> ApodViewer()
        "ASTEROIDS" -> AsteroidViewer()
        "WEATHER" -> WeatherViewer()
        "EPIC" -> EpicViewer()
        "EARTH" -> EarthEventsViewer()
        "IMAGES" -> NasaImageViewer()
        "GAIA" -> GaiaViewer()
    }
}

// =====================================
// MISSION CENTRE
// =====================================

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
        Triple("04", "ARTEMIS", "Human lunar exploration"),
        Triple("05", "PARKER SOLAR PROBE", "Solar corona investigation"),
        Triple("06", "EUROPA CLIPPER", "Jupiter's moon Europa")
    )

    missions.forEach {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Panel
            )
        ) {
            Column(Modifier.padding(15.dp)) {

                Text(
                    it.second,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    it.third,
                    color = Muted,
                    fontSize = 12.sp
                )

                Text(
                    "SPACE MISSION",
                    color = Green,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// =====================================
// 1. APOD VIEWER
// =====================================

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

    LaunchedEffect(Unit) { load() }

    ModuleHeading("NASA APOD", "Astronomy Picture of the Day")

    Button(
        onClick = { load() },
        enabled = !loading
    ) {
        Text("REFRESH APOD")
    }

    if (loading) LoadingIndicator()

    error?.let { ErrorPanel(it) }

    data?.let { apod ->

        Text(
            apod.title,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Text(apod.date, color = Blue)

        Spacer(Modifier.height(12.dp))

        AsyncImage(
            model = apod.imageUrl,
            contentDescription = apod.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            apod.explanation,
            color = Muted,
            fontSize = 13.sp
        )
    }
}

// =====================================
// 2. ASTEROID VIEWER
// =====================================

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

    LaunchedEffect(Unit) { load() }

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

    data.take(25).forEachIndexed { index, asteroid ->

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
                        "POTENTIALLY HAZARDOUS"
                    else
                        "NOT CLASSIFIED AS POTENTIALLY HAZARDOUS",
                    color = if (asteroid.hazardous) Red else Green,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// =====================================
// 3. NASA DONKI SPACE WEATHER
// =====================================

@Composable
fun WeatherViewer() {

    var data by remember {
        mutableStateOf<List<SpaceWeatherEvent>>(emptyList())
    }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            loading = true
            error = null

            try {
                data = SpaceData.getDonkiEvents()
            } catch (e: Exception) {
                error = e.message ?: "Unable to load space weather"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    ModuleHeading(
        "SPACE WEATHER",
        "NASA DONKI event notifications"
    )

    Button(
        onClick = { load() },
        enabled = !loading
    ) {
        Text("REFRESH SPACE WEATHER")
    }

    if (loading) LoadingIndicator()

    error?.let { ErrorPanel(it) }

    if (!loading && error == null && data.isEmpty()) {
        Text("No notifications returned for this period.", color = Muted)
    }

    data.forEach { event ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = Panel
            )
        ) {
            Column(Modifier.padding(15.dp)) {

                Text(
                    event.type,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    event.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(event.date, color = Muted, fontSize = 11.sp)

                Spacer(Modifier.height(10.dp))

                Text(
                    event.description.take(1500),
                    color = Muted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// =====================================
// 4. NASA EPIC EARTH OBSERVATORY
// =====================================

@Composable
fun EpicViewer() {

    var data by remember {
        mutableStateOf<List<EarthImage>>(emptyList())
    }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            loading = true
            error = null

            try {
                data = SpaceData.getEarthImages()
            } catch (e: Exception) {
                error = e.message ?: "Unable to load Earth images"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    ModuleHeading(
        "EARTH OBSERVATORY",
        "DSCOVR spacecraft natural Earth images"
    )

    Button(
        onClick = { load() },
        enabled = !loading
    ) {
        Text("REFRESH EARTH IMAGES")
    }

    if (loading) LoadingIndicator()

    error?.let { ErrorPanel(it) }

    data.take(8).forEach { earth ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Panel
            )
        ) {
            Column(Modifier.padding(12.dp)) {

                AsyncImage(
                    model = earth.imageUrl,
                    contentDescription = earth.caption,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    earth.caption,
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                Text(earth.date, color = Muted, fontSize = 11.sp)
            }
        }
    }
}

// =====================================
// 5. NASA EONET EARTH EVENTS
// =====================================

@Composable
fun EarthEventsViewer() {

    var data by remember {
        mutableStateOf<List<EarthEvent>>(emptyList())
    }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            loading = true
            error = null

            try {
                data = SpaceData.getEarthEvents()
            } catch (e: Exception) {
                error = e.message ?: "Unable to load Earth events"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    ModuleHeading(
        "EARTH EVENT MONITOR",
        "NASA EONET active natural events"
    )

    Button(
        onClick = { load() },
        enabled = !loading
    ) {
        Text("REFRESH EARTH EVENTS")
    }

    if (loading) LoadingIndicator()

    error?.let { ErrorPanel(it) }

    if (!loading && error == null && data.isEmpty()) {
        Text("No active events returned.", color = Muted)
    }

    data.forEach { event ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Panel
            )
        ) {
            Column(Modifier.padding(15.dp)) {

                Text(
                    event.category.uppercase(),
                    color = Blue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    event.date,
                    color = Muted,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    event.description.ifBlank {
                        "No additional description available."
                    },
                    color = Muted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// =====================================
// 6. NASA IMAGE LIBRARY
// =====================================

@Composable
fun NasaImageViewer() {

    var query by remember {
        mutableStateOf("nebula")
    }

    var data by remember {
        mutableStateOf<List<NasaImage>>(emptyList())
    }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun search() {

        if (query.isBlank()) {
            error = "Enter a search term."
            return
        }

        scope.launch {
            loading = true
            error = null

            try {
                data = SpaceData.searchNasaImages(query.trim())
            } catch (e: Exception) {
                error = e.message ?: "Image search failed"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { search() }

    ModuleHeading(
        "NASA IMAGE EXPLORER",
        "Search the NASA public image collection"
    )

    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Search space images") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue,
            focusedLabelColor = Blue,
            cursorColor = Blue
        )
    )

    Spacer(Modifier.height(10.dp))

    Button(
        onClick = { search() },
        enabled = !loading
    ) {
        Text("SEARCH NASA LIBRARY")
    }

    if (loading) LoadingIndicator()

    error?.let { ErrorPanel(it) }

    data.forEach { image ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),
            colors = CardDefaults.cardColors(
                containerColor = Panel
            )
        ) {
            Column(Modifier.padding(12.dp)) {

                AsyncImage(
                    model = image.imageUrl,
                    contentDescription = image.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    image.title,
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )

                Text(
                    image.date,
                    color = Muted,
                    fontSize = 10.sp
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    image.description.take(700),
                    color = Muted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// =====================================
// 7. ESA GAIA STAR CATALOGUE
// =====================================

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

    LaunchedEffect(Unit) { load() }

    ModuleHeading(
        "GAIA STAR CATALOGUE",
        "ESA Gaia DR3 stellar observations"
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
                    "SOURCE ID: ${star.id}",
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

// =====================================
// COMMON UI COMPONENTS
// =====================================

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
