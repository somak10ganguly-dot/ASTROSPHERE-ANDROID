
package com.astrosphere.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class Apod(
    val title: String,
    val explanation: String,
    val imageUrl: String,
    val date: String
)

data class Asteroid(
    val name: String,
    val diameter: Double,
    val velocity: String,
    val distance: String,
    val hazardous: Boolean
)

data class GaiaStar(
    val id: String,
    val ra: String,
    val dec: String,
    val magnitude: String,
    val parallax: String
)

object SpaceData {

    private const val NASA_KEY = "DEMO_KEY"

    private suspend fun get(url: String): String =
        withContext(Dispatchers.IO) {

            val connection =
                URL(url).openConnection() as HttpURLConnection

            try {
                connection.connectTimeout = 15000
                connection.readTimeout = 20000
                connection.requestMethod = "GET"

                val code = connection.responseCode

                if (code !in 200..299) {
                    throw Exception("HTTP error: $code")
                }

                connection.inputStream.bufferedReader().use {
                    it.readText()
                }

            } finally {
                connection.disconnect()
            }
        }

    // NASA Astronomy Picture of the Day
    suspend fun getApod(): Apod {

        val url =
            "https://api.nasa.gov/planetary/apod?api_key=$NASA_KEY"

        val response = JSONObject(get(url))

        return Apod(
            title = response.optString(
                "title",
                "Astronomy Picture of the Day"
            ),

            explanation = response.optString(
                "explanation",
                "No explanation available"
            ),

            imageUrl = response.optString(
                "hdurl",
                response.optString("url", "")
            ),

            date = response.optString("date", "")
        )
    }

    // NASA Near Earth Objects
    suspend fun getAsteroids(): List<Asteroid> {

        val url =
            "https://api.nasa.gov/neo/rest/v1/feed" +
            "?api_key=$NASA_KEY"

        val response = JSONObject(get(url))

        val dates =
            response.getJSONObject("near_earth_objects")

        val results = mutableListOf<Asteroid>()

        val keys = dates.keys()

        while (keys.hasNext()) {

            val date = keys.next()
            val objects = dates.getJSONArray(date)

            for (i in 0 until objects.length()) {

                val item = objects.getJSONObject(i)

                val approach = item
                    .getJSONArray("close_approach_data")
                    .optJSONObject(0) ?: continue

                val diameter = item
                    .getJSONObject("estimated_diameter")
                    .getJSONObject("kilometers")
                    .getDouble("estimated_diameter_max")

                val distance = approach
                    .getJSONObject("miss_distance")
                    .optString("kilometers", "0")

                results.add(
                    Asteroid(
                        name = item.optString("name", "Unknown"),
                        diameter = diameter,

                        velocity = approach
                            .getJSONObject("relative_velocity")
                            .optString(
                                "kilometers_per_hour",
                                "0"
                            ),

                        distance = distance,

                        hazardous = item.optBoolean(
                            "is_potentially_hazardous_asteroid",
                            false
                        )
                    )
                )
            }
        }

        return results.sortedBy {
            it.distance.toDoubleOrNull()
                ?: Double.MAX_VALUE
        }
    }

    // ESA Gaia DR3 Star Catalogue
    suspend fun getGaiaStars(): List<GaiaStar> {

        val query = """
            SELECT TOP 15
            source_id, ra, dec,
            phot_g_mean_mag, parallax
            FROM gaiadr3.gaia_source
            WHERE phot_g_mean_mag IS NOT NULL
            ORDER BY phot_g_mean_mag ASC
        """.trimIndent()

        val encoded = URLEncoder.encode(query, "UTF-8")

        val url =
            "https://gea.esac.esa.int/tap-server/tap/sync" +
            "?REQUEST=doQuery&LANG=ADQL&FORMAT=csv&QUERY=$encoded"

        val csv = get(url)

        val lines = csv.lines().filter {
            it.isNotBlank()
        }

        if (lines.size < 2) {
            return emptyList()
        }

        return lines.drop(1).mapNotNull { line ->

            val columns = line.split(",")

            if (columns.size < 5) {
                return@mapNotNull null
            }

            GaiaStar(
                id = columns[0],
                ra = columns[1],
                dec = columns[2],
                magnitude = columns[3],
                parallax = columns[4]
            )
        }
    }
}
