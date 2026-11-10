
package com.astrosphere.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.pow

@Composable
fun AstrophysicsSimulatorScreen() {

    val sections = listOf(
        "Planets",
        "Stars",
        "Moons",
        "Solar Systems",
        "Asteroids",
        "Comets",
        "Dwarf Planets",
        "Galaxies",
        "Explanations"
    )

    var selectedSection by remember {
        mutableIntStateOf(0)
    }

    var parameter by remember {
        mutableFloatStateOf(1.0f)
    }

    val section = sections[selectedSection]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "ASTROPHYSICS LAB",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Interactive scientific calculations",
            color = MaterialTheme.colorScheme.secondary
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "SELECT SIMULATION",
                    style = MaterialTheme.typography.titleMedium
                )

                sections.chunked(3).forEach { rowSections ->

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowSections.forEach { name ->

                            val index = sections.indexOf(name)

                            if (selectedSection == index) {
                                Button(
                                    onClick = {
                                        selectedSection = index
                                        parameter = 1.0f
                                    },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(5.dp)
                                ) {
                                    Text(
                                        name,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        selectedSection = index
                                        parameter = 1.0f
                                    },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(5.dp)
                                ) {
                                    Text(
                                        name,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }

                        repeat(3 - rowSections.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    section.uppercase(),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    "Adjust the parameter to recalculate the model.",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    "Parameter: ${"%.2f".format(parameter)}",
                    style = MaterialTheme.typography.titleMedium
                )

                Slider(
                    value = parameter,
                    onValueChange = { parameter = it },
                    valueRange = 0.5f..10.0f
                )

                when (selectedSection) {
                    0 -> PlanetModel(parameter)
                    1 -> StarModel(parameter)
                    2 -> MoonModel(parameter)
                    3 -> SolarSystemModel(parameter)
                    4 -> AsteroidModel(parameter)
                    5 -> CometModel(parameter)
                    6 -> DwarfPlanetModel(parameter)
                    7 -> GalaxyModel(parameter)
                    8 -> GravityModel(parameter)
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    "MODEL INFORMATION",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    when (selectedSection) {
                        0 -> "Planetary orbital calculations use Kepler's third law for a circular orbit around the Sun."

                        1 -> "Stellar luminosity is calculated using the Stefan-Boltzmann law."

                        2 -> "The moon model calculates orbital motion around Earth."

                        3 -> "The solar-system model calculates orbital periods and velocities around the Sun."

                        4 -> "The asteroid model estimates orbital eccentricity and period from perihelion and aphelion."

                        5 -> "The comet model estimates an elliptical orbit around the Sun."

                        6 -> "The dwarf-planet model estimates orbital properties around the Sun."

                        7 -> "The galaxy model approximates orbital velocity using enclosed mass treated as a central point mass."

                        else -> "Newton's law of universal gravitation calculates the attractive force between two masses."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    "These are idealised models. Real astronomical systems may require additional variables and numerical methods.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun ResultLine(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PlanetModel(value: Float) {

    val radiusAU = value.toDouble()

    val result = AstrophysicsEngine.calculateOrbit(
        AstrophysicsEngine.SOLAR_MASS,
        radiusAU * AstrophysicsEngine.AU
    )

    ResultLine("Orbital radius", "$radiusAU AU")
    ResultLine(
        "Orbital period",
        "${"%.3f".format(result.periodYears)} years"
    )
    ResultLine(
        "Orbital velocity",
        "${"%.2f".format(result.orbitalVelocity / 1000.0)} km/s"
    )
    ResultLine(
        "Escape velocity",
        "${"%.2f".format(result.escapeVelocity / 1000.0)} km/s"
    )
}

@Composable
private fun StarModel(value: Float) {

    val radius = value.toDouble()
    val temperature = 3000.0 + value * 900.0

    val result = AstrophysicsEngine.calculateStar(
        radiusSolar = radius,
        temperatureKelvin = temperature,
        massSolar = radius.pow(0.8)
    )

    ResultLine("Stellar radius", "$radius solar radii")
    ResultLine("Surface temperature", "${temperature.toInt()} K")
    ResultLine(
        "Luminosity",
        "${"%.3e".format(result.luminosityWatts)} W"
    )
    ResultLine(
        "Stellar mass",
        "${"%.2f".format(result.massSolar)} solar masses"
    )
}

@Composable
private fun MoonModel(value: Float) {

    val distanceKm = 384400.0 * value

    val result = AstrophysicsEngine.calculateOrbit(
        AstrophysicsEngine.EARTH_MASS,
        distanceKm * 1000.0
    )

    ResultLine("Orbital distance", "${"%.0f".format(distanceKm)} km")
    ResultLine(
        "Orbital period",
        "${"%.2f".format(result.periodDays)} days"
    )
    ResultLine(
        "Orbital velocity",
        "${"%.3f".format(result.orbitalVelocity / 1000.0)} km/s"
    )
    ResultLine(
        "Escape velocity",
        "${"%.3f".format(result.escapeVelocity / 1000.0)} km/s"
    )
}

@Composable
private fun SolarSystemModel(value: Float) {

    val distanceAU = value.toDouble()

    val result = AstrophysicsEngine.calculateOrbit(
        AstrophysicsEngine.SOLAR_MASS,
        distanceAU * AstrophysicsEngine.AU
    )

    ResultLine("Distance from Sun", "$distanceAU AU")
    ResultLine(
        "Orbital period",
        "${"%.3f".format(result.periodYears)} years"
    )
    ResultLine(
        "Orbital speed",
        "${"%.2f".format(result.orbitalVelocity / 1000.0)} km/s"
    )
}

@Composable
private fun AsteroidModel(value: Float) {

    val perihelion = value.toDouble()
    val aphelion = perihelion + 0.5

    val result = AstrophysicsEngine.calculateAsteroid(
        perihelionAU = perihelion,
        aphelionAU = aphelion
    )

    ResultLine("Perihelion", "${"%.2f".format(result.perihelionAU)} AU")
    ResultLine("Aphelion", "${"%.2f".format(result.aphelionAU)} AU")
    ResultLine(
        "Semi-major axis",
        "${"%.2f".format(result.semiMajorAxisAU)} AU"
    )
    ResultLine(
        "Eccentricity",
        "${"%.4f".format(result.eccentricity)}"
    )
    ResultLine(
        "Orbital period",
        "${"%.3f".format(result.periodYears)} years"
    )
}

@Composable
private fun CometModel(value: Float) {

    val perihelion = (value * 0.2).coerceAtLeast(0.1)
    val aphelion = perihelion + 10.0

    val result = AstrophysicsEngine.calculateAsteroid(
        perihelionAU = perihelion,
        aphelionAU = aphelion
    )

    ResultLine("Perihelion", "${"%.2f".format(result.perihelionAU)} AU")
    ResultLine("Aphelion", "${"%.2f".format(result.aphelionAU)} AU")
    ResultLine(
        "Eccentricity",
        "${"%.4f".format(result.eccentricity)}"
    )
    ResultLine(
        "Orbital period",
        "${"%.2f".format(result.periodYears)} years"
    )
}

@Composable
private fun DwarfPlanetModel(value: Float) {

    val distanceAU = 30.0 + value * 2.0

    val result = AstrophysicsEngine.calculateOrbit(
        AstrophysicsEngine.SOLAR_MASS,
        distanceAU * AstrophysicsEngine.AU
    )

    ResultLine("Orbital distance", "${"%.2f".format(distanceAU)} AU")
    ResultLine(
        "Orbital period",
        "${"%.2f".format(result.periodYears)} years"
    )
    ResultLine(
        "Orbital velocity",
        "${"%.3f".format(result.orbitalVelocity / 1000.0)} km/s"
    )
}

@Composable
private fun GalaxyModel(value: Float) {

    val radius = value * 1000.0

    val result = AstrophysicsEngine.calculateGalaxyRotation(
        enclosedMassSolar = 1.0e11,
        radiusLightYears = radius
    )

    ResultLine("Galactic radius", "$radius light-years")
    ResultLine("Enclosed mass", "100 billion solar masses")
    ResultLine(
        "Orbital velocity",
        "${"%.2f".format(result.orbitalVelocityKmS)} km/s"
    )
    ResultLine(
        "Orbital period",
        "${"%.2e".format(result.orbitalPeriodYears)} years"
    )
}

@Composable
private fun GravityModel(value: Float) {

    val distance = AstrophysicsEngine.EARTH_RADIUS * value

    val result = AstrophysicsEngine.calculateGravity(
        mass1Kg = AstrophysicsEngine.EARTH_MASS,
        mass2Kg = 1000.0,
        distanceMeters = distance
    )

    ResultLine("First mass", "Earth")
    ResultLine("Second mass", "1,000 kg")
    ResultLine(
        "Distance",
        "${"%.2f".format(distance / 1000.0)} km"
    )
    ResultLine(
        "Gravitational force",
        "${"%.3f".format(result.forceNewtons)} N"
    )
    ResultLine(
        "Acceleration",
        "${"%.4f".format(result.acceleration)} m/s²"
    )
}
