
package com.astrosphere.app

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

object AstrophysicsEngine {

    // Fundamental constants — SI units
    const val G = 6.67430e-11
    const val SOLAR_MASS = 1.98847e30
    const val EARTH_MASS = 5.9722e24
    const val EARTH_RADIUS = 6.371e6
    const val AU = 1.495978707e11
    const val SOLAR_RADIUS = 6.957e8
    const val STEFAN_BOLTZMANN = 5.670374419e-8

    data class OrbitResult(
        val periodSeconds: Double,
        val periodDays: Double,
        val periodYears: Double,
        val orbitalVelocity: Double,
        val escapeVelocity: Double
    )

    data class GravityResult(
        val forceNewtons: Double,
        val acceleration: Double
    )

    data class StarResult(
        val luminosityWatts: Double,
        val radiusMeters: Double,
        val massSolar: Double,
        val temperatureKelvin: Double
    )

    data class AsteroidResult(
        val perihelionAU: Double,
        val aphelionAU: Double,
        val semiMajorAxisAU: Double,
        val eccentricity: Double,
        val periodYears: Double
    )

    data class GalaxyResult(
        val orbitalVelocityKmS: Double,
        val orbitalPeriodYears: Double
    )

    // Kepler's third law and circular orbital velocity
    fun calculateOrbit(
        centralMassKg: Double,
        orbitalRadiusMeters: Double
    ): OrbitResult {
        require(centralMassKg > 0 && orbitalRadiusMeters > 0)

        val period = 2.0 * PI * sqrt(
            orbitalRadiusMeters.pow(3) /
                (G * centralMassKg)
        )

        val velocity = sqrt(
            G * centralMassKg / orbitalRadiusMeters
        )

        val escape = sqrt(
            2.0 * G * centralMassKg / orbitalRadiusMeters
        )

        return OrbitResult(
            periodSeconds = period,
            periodDays = period / 86400.0,
            periodYears = period / (86400.0 * 365.25),
            orbitalVelocity = velocity,
            escapeVelocity = escape
        )
    }

    // Newtonian gravity
    fun calculateGravity(
        mass1Kg: Double,
        mass2Kg: Double,
        distanceMeters: Double
    ): GravityResult {
        require(mass1Kg > 0 && mass2Kg > 0 && distanceMeters > 0)

        val force = G * mass1Kg * mass2Kg /
            distanceMeters.pow(2)

        val acceleration = force / mass2Kg

        return GravityResult(
            forceNewtons = force,
            acceleration = acceleration
        )
    }

    // Stellar luminosity using Stefan-Boltzmann law
    fun calculateStar(
        radiusSolar: Double,
        temperatureKelvin: Double,
        massSolar: Double
    ): StarResult {
        require(radiusSolar > 0)
        require(temperatureKelvin > 0)
        require(massSolar > 0)

        val radius = radiusSolar * SOLAR_RADIUS

        val luminosity = 4.0 * PI *
            radius.pow(2) *
            STEFAN_BOLTZMANN *
            temperatureKelvin.pow(4)

        return StarResult(
            luminosityWatts = luminosity,
            radiusMeters = radius,
            massSolar = massSolar,
            temperatureKelvin = temperatureKelvin
        )
    }

    // Asteroid orbit from perihelion and aphelion
    fun calculateAsteroid(
        perihelionAU: Double,
        aphelionAU: Double
    ): AsteroidResult {
        require(perihelionAU > 0)
        require(aphelionAU >= perihelionAU)

        val semiMajor = (perihelionAU + aphelionAU) / 2.0

        val eccentricity =
            (aphelionAU - perihelionAU) /
                (aphelionAU + perihelionAU)

        // Kepler's third law for an orbit around the Sun
        val periodYears = sqrt(semiMajor.pow(3))

        return AsteroidResult(
            perihelionAU = perihelionAU,
            aphelionAU = aphelionAU,
            semiMajorAxisAU = semiMajor,
            eccentricity = eccentricity,
            periodYears = periodYears
        )
    }

    // Simplified galaxy rotation model:
    // enclosed mass treated as a point mass
    fun calculateGalaxyRotation(
        enclosedMassSolar: Double,
        radiusLightYears: Double
    ): GalaxyResult {
        require(enclosedMassSolar > 0)
        require(radiusLightYears > 0)

        val mass = enclosedMassSolar * SOLAR_MASS
        val radius = radiusLightYears * 9.4607e15

        val velocity = sqrt(G * mass / radius)

        val period = 2.0 * PI * radius / velocity

        return GalaxyResult(
            orbitalVelocityKmS = velocity / 1000.0,
            orbitalPeriodYears =
                period / (86400.0 * 365.25)
        )
    }

    // Convert metres per second to kilometres per second
    fun kmPerSecond(metersPerSecond: Double): Double {
        return metersPerSecond / 1000.0
    }

    // Convert seconds into a readable duration
    fun formatDuration(seconds: Double): String {
        val days = seconds / 86400.0

        return when {
            days >= 365.25 ->
                "%.3f years".format(days / 365.25)

            days >= 1 ->
                "%.2f days".format(days)

            else ->
                "%.2f hours".format(seconds / 3600.0)
        }
    }
}
