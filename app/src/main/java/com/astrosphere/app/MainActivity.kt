
package com.astrosphere.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val SpaceBlack = Color(0xFF070B16)
private val Panel = Color(0xFF111C30)
private val Blue = Color(0xFF55B8FF)
private val Muted = Color(0xFF9AABC4)
private val Green = Color(0xFF8BE0A2)
private val Red = Color(0xFFFF7777)
private val Purple = Color(0xFFB69CFF)

// =====================================
// ACTIVITY
// =====================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AstroSphereApp() }
    }
}

// =====================================
// MAIN APPLICATION
// =====================================

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
                        "Home", "Explore", "Missions",
                        "Simulator", "Research"
                    )

                    val symbols = listOf(
                        "⌂", "◎", "◈", "◉", "⌘"
                    )

                    names.forEachIndexed { index, name ->
                        NavigationBarItem(
                            selected = selected == index,
                            onClick = { selected = index },
                            icon = {
                                Text(symbols[index], fontSize = 21.sp)
                            },
                            label = {
                                Text(name, fontSize = 9.sp)
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

                    3 -> SimulatorScreen()

                    4 -> ResearchScreen(
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
            it.first, it.second, it.third, "OPEN MODULE"
        ) {
            onSelect(it.second)
        }
    }
}

// =====================================
// REUSABLE CARD
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

                Text(source, color = Blue, fontSize = 10.sp)
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

    Text(subtitle, color = Muted, fontSize = 13.sp)

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
            it.first, it.second, it.third, "SELECT MODULE"
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
    ) { onSelect("WEATHER") }

    ObservatoryCard(
        "02", "ORBITAL SCIENCE",
        "Asteroid orbital observations",
        "NASA NEO"
    ) { onSelect("ASTEROIDS") }

    ObservatoryCard(
        "03", "STELLAR SCIENCE",
        "Star positions and measurements",
        "ESA GAIA"
    ) { onSelect("GAIA") }

    ObservatoryCard(
        "04", "EARTH SCIENCE",
        "Earth observation imagery",
        "NASA EPIC"
    ) { onSelect("EPIC") }

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
// MISSION DATA
// =====================================

data class MissionInfo(
    val number: String,
    val name: String,
    val agency: String,
    val spacecraft: String,
    val objective: String,
    val destination: String,
    val science: String,
    val status: String
)

private val missionList = listOf(

    MissionInfo(
        "01",
        "James Webb Space Telescope",
        "NASA / ESA / CSA",
        "JWST",
        "Observe the early Universe, distant galaxies, stars and exoplanet atmospheres.",
        "Sun-Earth L2 region",
        "Infrared astronomy, galaxy evolution and exoplanet spectroscopy.",
        "ACTIVE"
    ),

    MissionInfo(
        "02",
        "JUICE",
        "ESA",
        "Jupiter Icy Moons Explorer",
        "Investigate Jupiter and its ocean-bearing icy moons.",
        "Jupiter system",
        "Study Ganymede, Callisto, Europa and Jupiter's magnetosphere.",
        "IN TRANSIT"
    ),

    MissionInfo(
        "03",
        "Euclid",
        "ESA",
        "Euclid Space Telescope",
        "Map the geometry of the dark Universe.",
        "Sun-Earth L2 region",
        "Measure galaxy shapes, clustering and cosmic expansion.",
        "ACTIVE"
    ),

    MissionInfo(
        "04",
        "Artemis",
        "NASA",
        "Orion and Space Launch System",
        "Return humans to the Moon and develop sustainable lunar exploration.",
        "Moon",
        "Lunar exploration, habitation and preparation for Mars missions.",
        "PROGRAMME"
    ),

    MissionInfo(
        "05",
        "Parker Solar Probe",
        "NASA",
        "Parker Solar Probe",
        "Investigate the Sun's corona and solar wind.",
        "Solar corona",
        "Study magnetic fields, energetic particles and coronal heating.",
        "ACTIVE"
    ),

    MissionInfo(
        "06",
        "Europa Clipper",
        "NASA",
        "Europa Clipper",
        "Investigate whether Jupiter's moon Europa has conditions suitable for life.",
        "Jupiter / Europa",
        "Study Europa's ice shell, ocean, composition and geology.",
        "IN TRANSIT"
    )
)

// =====================================
// MISSION CONTROL
// =====================================

@Composable
fun MissionsScreen() {

    var selectedMission by remember {
        mutableStateOf<MissionInfo?>(null)
    }

    SectionTitle(
        "Mission Control",
        "NASA and ESA exploration programmes"
    )

    if (selectedMission == null) {

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF132C4B)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    "MISSION DATABASE",
                    color = Blue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Explore spacecraft, destinations and scientific objectives.",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "${missionList.size} MISSION PROFILES",
                    color = Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        missionList.forEach { mission ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        selectedMission = mission
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Panel
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        mission.number,
                        color = Blue,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.width(14.dp))

                    Column(Modifier.weight(1f)) {

                        Text(
                            mission.name,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            mission.agency,
                            color = Muted,
                            fontSize = 11.sp
                        )

                        Text(
                            mission.status,
                            color = Green,
                            fontSize = 10.sp
                        )
                    }

                    Text("›", color = Blue, fontSize = 25.sp)
                }
            }
        }

    } else {

        val mission = selectedMission!!

        TextButton(
            onClick = { selectedMission = null }
        ) {
            Text("← ALL MISSIONS", color = Blue)
        }

        Text(
            mission.name,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            mission.agency,
            color = Blue,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(15.dp))

        MissionInfoCard("MISSION STATUS", mission.status)
        MissionInfoCard("SPACECRAFT", mission.spacecraft)
        MissionInfoCard("DESTINATION", mission.destination)
        MissionInfoCard("MISSION OBJECTIVE", mission.objective)
        MissionInfoCard("SCIENTIFIC PROGRAMME", mission.science)
    }
}

@Composable
fun MissionInfoCard(title: String, content: String) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Panel
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                title,
                color = Blue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                content,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

// =====================================
// NINE-SECTION SIMULATOR DATA
// =====================================

data class SimulatorSection(
    val number: String,
    val name: String,
    val description: String,
    val parameter: String,
    val minimum: String,
    val maximum: String,
    val explanation: String
)

private val simulatorSections = listOf(

    SimulatorSection(
        "01",
        "PLANETS",
        "Planetary rotation, revolution and orbital motion.",
        "Orbital speed",
        "Slow",
        "Fast",
        "Change orbital speed to observe how quickly a planet travels around its star."
    ),

    SimulatorSection(
        "02",
        "STARS",
        "Stellar temperature, colour and luminosity.",
        "Temperature",
        "Cool",
        "Hot",
        "Explore how stellar temperature changes a star's apparent colour and radiation."
    ),

    SimulatorSection(
        "03",
        "MOONS",
        "Natural satellites and planetary orbital systems.",
        "Orbital distance",
        "Near",
        "Far",
        "Change a moon's orbital distance and observe its path around a planet."
    ),

    SimulatorSection(
        "04",
        "SOLAR SYSTEMS",
        "Explore multi-planet orbital arrangements.",
        "System scale",
        "Compact",
        "Expanded",
        "Adjust the scale of a model planetary system."
    ),

    SimulatorSection(
        "05",
        "ASTEROIDS",
        "Study rocky bodies and their trajectories.",
        "Trajectory angle",
        "Low",
        "High",
        "Change the direction of a model asteroid's path."
    ),

    SimulatorSection(
        "06",
        "COMETS",
        "Explore comet orbits and solar approach.",
        "Solar distance",
        "Near",
        "Far",
        "Change the model comet's distance from the Sun."
    ),

    SimulatorSection(
        "07",
        "DWARF PLANETS",
        "Compare small planetary bodies.",
        "Body size",
        "Small",
        "Large",
        "Adjust the displayed relative size of a dwarf planet."
    ),

    SimulatorSection(
        "08",
        "GALAXIES",
        "Explore galaxy structures and rotation.",
        "Rotation",
        "Slow",
        "Fast",
        "Change the rotation rate of the model galaxy."
    ),

    SimulatorSection(
        "09",
        "EXPLANATIONS",
        "Scientific concepts and interactive learning.",
        "Learning level",
        "Basic",
        "Advanced",
        "Explore the scientific explanation associated with the selected model."
    )
)

// =====================================
// ASTROPHYSICS SIMULATOR SCREEN
// =====================================

@Composable
fun SimulatorScreen() {

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    var parameter by remember {
        mutableFloatStateOf(50f)
    }

    var animationRunning by remember {
        mutableStateOf(true)
    }

    var showExplanation by remember {
        mutableStateOf(true)
    }

    val section = simulatorSections[selectedIndex]

    SectionTitle(
        "UNIVERSE SIMULATOR",
        "Nine interactive scientific environments"
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF132C4B)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(18.dp)) {

            Text(
                "ASTROPHYSICS LAB",
                color = Blue,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Explore. Adjust. Observe.",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Interactive conceptual models of astronomical systems.",
                color = Muted,
                fontSize = 12.sp
            )
        }
    }

    Spacer(Modifier.height(20.dp))

    Text(
        "SELECT SIMULATION",
        color = Blue,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(10.dp))

    simulatorSections.forEachIndexed { index, item ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable {
                    selectedIndex = index
                    parameter = 50f
                },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedIndex == index)
                    Color(0xFF18385A)
                else
                    Panel
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    item.number,
                    color = Blue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(Modifier.width(13.dp))

                Column(Modifier.weight(1f)) {

                    Text(
                        item.name,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        item.description,
                        color = Muted,
                        fontSize = 10.sp
                    )
                }

                if (selectedIndex == index) {
                    Text("ACTIVE", color = Green, fontSize = 10.sp)
                }
            }
        }
    }

    Spacer(Modifier.height(22.dp))

    Divider(color = Color(0xFF263B57))

    Spacer(Modifier.height(18.dp))

    Text(
        "${section.number} / ${section.name}",
        color = Blue,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )

    Text(
        section.description,
        color = Muted,
        fontSize = 12.sp
    )

    Spacer(Modifier.height(18.dp))

    // Scientific visualisation

    OrbitalVisualisation(
        sectionIndex = selectedIndex,
        parameter = parameter,
        animationRunning = animationRunning
    )

    Spacer(Modifier.height(15.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "SIMULATION CONTROL",
            color = Blue,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        Spacer(Modifier.weight(1f))

        Text(
            if (animationRunning) "RUNNING" else "PAUSED",
            color = if (animationRunning) Green else Red,
            fontSize = 10.sp
        )
    }

    Spacer(Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(section.minimum, color = Muted, fontSize = 11.sp)
        Text(section.parameter, color = Color.White, fontWeight = FontWeight.Bold)
        Text(section.maximum, color = Muted, fontSize = 11.sp)
    }

    Slider(
        value = parameter,
        onValueChange = { parameter = it },
        valueRange = 0f..100f,
        colors = SliderDefaults.colors(
            thumbColor = Blue,
            activeTrackColor = Blue,
            inactiveTrackColor = Panel
        )
    )

    Text(
        "PARAMETER VALUE: ${parameter.toInt()}%",
        color = Green,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(12.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Button(
            onClick = {
                animationRunning = !animationRunning
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(
                if (animationRunning) "PAUSE" else "RESUME"
            )
        }

        OutlinedButton(
            onClick = {
                parameter = 50f
                animationRunning = true
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("RESET")
        }
    }

    Spacer(Modifier.height(18.dp))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Panel
        ),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SCIENTIFIC EXPLANATION",
                    color = Blue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(Modifier.weight(1f))

                TextButton(
                    onClick = {
                        showExplanation = !showExplanation
                    }
                ) {
                    Text(
                        if (showExplanation) "HIDE" else "SHOW",
                        color = Blue
                    )
                }
            }

            if (showExplanation) {
                Text(
                    section.explanation,
                    color = Muted,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    "Model parameter: ${parameter.toInt()}%",
                    color = Green,
                    fontSize = 12.sp
                )
            }
        }
    }

    Spacer(Modifier.height(15.dp))

    Text(
        "MODEL NOTE",
        color = Purple,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )

    Text(
        "This version uses conceptual visual models. It is not a numerical N-body solver or a validated astrophysical prediction engine.",
        color = Muted,
        fontSize = 11.sp
    )
}

// =====================================
// ORBITAL VISUALISATION
// =====================================

@Composable
fun OrbitalVisualisation(
    sectionIndex: Int,
    parameter: Float,
    animationRunning: Boolean
) {

    var angle by remember(sectionIndex) {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(
        sectionIndex,
        parameter,
        animationRunning
    ) {
        if (animationRunning) {
            while (true) {
                angle += (1.0f + parameter / 20f)
                if (angle >= 360f) angle -= 360f
                kotlinx.coroutines.delay(35)
            }
        }
    }

    val radius = 0.25f + parameter / 250f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF030916)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val center = Offset(centerX, centerY)

            val orbitRadius = size.minDimension * radius.coerceIn(0.25f, 0.48f)

            val planetAngle = angle * PI.toFloat() / 180f

            val planetX =
                centerX + cos(planetAngle) * orbitRadius

            val planetY =
                centerY + sin(planetAngle) * orbitRadius

            // Background stars

            for (i in 0 until 45) {
                val x = (i * 83f % size.width)
                val y = (i * 47f % size.height)

                drawCircle(
                    color = Color(0xFF637C9C),
                    radius = 1.5f,
                    center = Offset(x, y)
                )
            }

            when (sectionIndex) {

                // Planets, moons, solar systems,
                // asteroids, comets and dwarf planets

                0, 2, 3, 4, 5, 6 -> {

                    drawCircle(
                        color = Blue.copy(alpha = 0.65f),
                        radius = orbitRadius,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2f
                        )
                    )

                    drawCircle(
                        color = Color(0xFFFFB347),
                        radius = 15f,
                        center = center
                    )

                    drawCircle(
                        color = Green,
                        radius = if (sectionIndex == 6) 6f else 9f,
                        center = Offset(planetX, planetY)
                    )

                    if (sectionIndex == 2) {
                        drawCircle(
                            color = Color(0xFFB69CFF),
                            radius = 4f,
                            center = Offset(
                                planetX + 16f,
                                planetY
                            )
                        )
                    }

                    if (sectionIndex == 4 || sectionIndex == 5) {
                        drawCircle(
                            color = Red,
                            radius = 4f,
                            center = Offset(planetX, planetY)
                        )
                    }
                }

                // Stars

                1 -> {

                    drawCircle(
                        color = Color(0xFFFFB347).copy(alpha = 0.12f),
                        radius = 65f,
                        center = center
                    )

                    drawCircle(
                        color = Color(
                            red = 1f,
                            green = 0.4f + parameter / 250f,
                            blue = 0.1f
                        ),
                        radius = 28f,
                        center = center
                    )

                    for (i in 0 until 8) {
                        val a = i * PI.toFloat() / 4f
                        val x = centerX + cos(a) * 43f
                        val y = centerY + sin(a) * 43f

                        drawLine(
                            color = Color(0xFFFFB347),
                            start = center,
                            end = Offset(x, y),
                            strokeWidth = 2f
                        )
                    }
                }

                // Galaxies

                7 -> {

                    for (i in 0 until 4) {
                        val r = 30f + i * 20f

                        drawCircle(
                            color = Purple.copy(alpha = 0.65f),
                            radius = r,
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 3f
                            )
                        )
                    }

                    for (i in 0 until 16) {
                        val a = (
                            i * 22.5f + angle
                        ) * PI.toFloat() / 180f

                        val r = 35f + (i % 4) * 16f

                        drawCircle(
                            color = Blue,
                            radius = 3f,
                            center = Offset(
                                centerX + cos(a) * r,
                                centerY + sin(a) * r
                            )
                        )
                    }

                    drawCircle(
                        color = Color.White,
                        radius = 8f,
                        center = center
                    )
                }

                // Explanations

                8 -> {

                    drawCircle(
                        color = Blue.copy(alpha = 0.15f),
                        radius = 75f,
                        center = center
                    )

                    drawCircle(
                        color = Blue,
                        radius = 38f,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 3f
                        )
                    )

                    drawCircle(
                        color = Green,
                        radius = 10f,
                        center = center
                    )

                    drawLine(
                        color = Purple,
                        start = Offset(centerX - 85f, centerY),
                        end = Offset(centerX + 85f, centerY),
                        strokeWidth = 2f
                    )
                }
            }
        }
    }
}

// =====================================
// APOD VIEWER
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

    Button(onClick = { load() }, enabled = !loading) {
        Text("REFRESH APOD")
    }

    if (loading) LoadingIndicator()
    error?.let { ErrorPanel(it) }

    data?.let { apod ->

        Text(apod.title, fontSize = 21.sp, fontWeight = FontWeight.Bold)
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

        Text(apod.explanation, color = Muted, fontSize = 13.sp)
    }
}

// =====================================
// ASTEROID VIEWER
// =====================================

@Composable
fun AsteroidViewer() {

    var data by remember { mutableStateOf<List<Asteroid>>(emptyList()) }
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

    ModuleHeading("ASTEROID TRACKER", "NASA Near-Earth Objects")

    Button(onClick = { load() }, enabled = !loading) {
        Text("REFRESH ASTEROIDS")
    }

    if (loading) LoadingIndicator()
    error?.let { ErrorPanel(it) }

    data.take(25).forEachIndexed { index, asteroid ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            colors = CardDefaults.cardColors(containerColor = Panel)
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
// WEATHER VIEWER
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

    ModuleHeading("SPACE WEATHER", "NASA DONKI event notifications")

    Button(onClick = { load() }, enabled = !loading) {
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
            colors = CardDefaults.cardColors(containerColor = Panel)
        ) {
            Column(Modifier.padding(15.dp)) {

                Text(event.type, color = Blue, fontWeight = FontWeight.Bold)

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
// EPIC VIEWER
// =====================================

@Composable
fun EpicViewer() {

    var data by remember { mutableStateOf<List<EarthImage>>(emptyList()) }
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

    Button(onClick = { load() }, enabled = !loading) {
        Text("REFRESH EARTH IMAGES")
    }

    if (loading) LoadingIndicator()
    error?.let { ErrorPanel(it) }

    data.take(8).forEach { earth ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Panel)
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

                Text(earth.caption, color = Blue, fontWeight = FontWeight.Bold)
                Text(earth.date, color = Muted, fontSize = 11.sp)
            }
        }
    }
}

// =====================================
// EARTH EVENTS VIEWER
// =====================================

@Composable
fun EarthEventsViewer() {

    var data by remember { mutableStateOf<List<EarthEvent>>(emptyList()) }
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

    ModuleHeading("EARTH EVENT MONITOR", "NASA EONET active natural events")

    Button(onClick = { load() }, enabled = !loading) {
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
            colors = CardDefaults.cardColors(containerColor = Panel)
        ) {
            Column(Modifier.padding(15.dp)) {

                Text(
                    event.category.uppercase(),
                    color = Blue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )

                Text(event.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(event.date, color = Muted, fontSize = 11.sp)

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
// NASA IMAGE VIEWER
// =====================================

@Composable
fun NasaImageViewer() {

    var query by remember { mutableStateOf("nebula") }
    var data by remember { mutableStateOf<List<NasaImage>>(emptyList()) }
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

    ModuleHeading("NASA IMAGE EXPLORER", "Search the NASA public image collection")

    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Search space images") },
        singleLine = true
    )

    Spacer(Modifier.height(10.dp))

    Button(onClick = { search() }, enabled = !loading) {
        Text("SEARCH NASA LIBRARY")
    }

    if (loading) LoadingIndicator()
    error?.let { ErrorPanel(it) }

    data.forEach { image ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),
            colors = CardDefaults.cardColors(containerColor = Panel)
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

                Text(image.title, fontWeight = FontWeight.Bold, color = Blue)
                Text(image.date, color = Muted, fontSize = 10.sp)

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
// GAIA VIEWER
// =====================================

@Composable
fun GaiaViewer() {

    var data by remember { mutableStateOf<List<GaiaStar>>(emptyList()) }
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

    ModuleHeading("GAIA STAR CATALOGUE", "ESA Gaia DR3 stellar observations")

    Button(onClick = { load() }, enabled = !loading) {
        Text("REFRESH STAR DATA")
    }

    if (loading) LoadingIndicator()
    error?.let { ErrorPanel(it) }

    data.forEach { star ->

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            colors = CardDefaults.cardColors(containerColor = Panel)
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
// COMMON UI
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

    Text(subtitle, color = Muted, fontSize = 12.sp)

    Spacer(Modifier.height(12.dp))
}

@Composable
fun LoadingIndicator() {

    Row(verticalAlignment = Alignment.CenterVertically) {

        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = Blue,
            strokeWidth = 3.dp
        )

        Spacer(Modifier.width(12.dp))

        Text("Connecting to space data...", color = Muted)
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
