
package com.astrosphere.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ===============================
// DATA MODELS
// ===============================

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

data class SpaceWeatherEvent(
    val id: String,
    val type: String,
    val title: String,
    val date: String,
    val description: String,
    val link: String
)

data class EarthImage(
    val identifier: String,
    val caption: String,
    val date: String,
    val imageUrl: String
)

data class EarthEvent(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val date: String,
    val link: String
)

data class NasaImage(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val date: String
)

// ===============================
// SPACE DATA ENGINE
// ===============================

object SpaceData {

    private val NASA_KEY = BuildConfig.NASA_API_KEY

    private const val NASA_BASE =
        "https://api.nasa.gov"

    private const val EONET_BASE =
        "https://eonet.gsfc.nasa.gov/api/v3"

    private const val ESA_BASE =
        "https://gea.esac.esa.int/tap-server/tap/sync"

    private const val NASA_IMAGES =
        "https://images-api.nasa.gov/search"

    // ===============================
    // HTTP ENGINE
    // ===============================

    private suspend fun get(url: String): String =
        withContext(Dispatchers.IO) {

            val connection =
                URL(url).openConnection() as HttpURLConnection

            try {
                connection.connectTimeout = 20000
                connection.readTimeout = 25000
                connection.requestMethod = "GET"

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                connection.setRequestProperty(
                    "User-Agent",
                    "ASTROSPHERE-Android/2.0"
                )

                val code = connection.responseCode

                if (code !in 200..299) {
                    val error = connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        .orEmpty()

                    throw Exception(
                        "HTTP $code: ${error.take(300)}"
                    )
                }

                connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

            } finally {
                connection.disconnect()
            }
        }

    private fun dateRange(days: Int): Pair<String, String> {

        val format = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val end = Calendar.getInstance()
        val start = end.clone() as Calendar

        start.add(Calendar.DAY_OF_YEAR, -days)

        return Pair(
            format.format(start.time),
            format.format(end.time)
        )
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, "UTF-8")

    // ===============================
    // 1. NASA APOD
    // ===============================

    suspend fun getApod(): Apod {

        val url =
            "$NASA_BASE/planetary/apod?api_key=$NASA_KEY"

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

    // ===============================
    // 2. NASA ASTEROID TRACKER
    // ===============================

    suspend fun getAsteroids(): List<Asteroid> {

        val url =
            "$NASA_BASE/neo/rest/v1/feed?api_key=$NASA_KEY"

        val response = JSONObject(get(url))

        val dates =
            response.getJSONObject("near_earth_objects")

        val results = mutableListOf<Asteroid>()

        val keys = dates.keys()

        while (keys.hasNext()) {

            val objects =
                dates.getJSONArray(keys.next())

            for (i in 0 until objects.length()) {

                val item = objects.getJSONObject(i)

                val approach = item
                    .optJSONArray("close_approach_data")
                    ?.optJSONObject(0) ?: continue

                val diameter = item
                    .getJSONObject("estimated_diameter")
                    .getJSONObject("kilometers")
                    .optDouble(
                        "estimated_diameter_max",
                        0.0
                    )

                results.add(
                    Asteroid(
                        name = item.optString(
                            "name",
                            "Unknown"
                        ),

                        diameter = diameter,

                        velocity = approach
                            .optJSONObject("relative_velocity")
                            ?.optString(
                                "kilometers_per_hour",
                                "0"
                            ) ?: "0",

                        distance = approach
                            .optJSONObject("miss_distance")
                            ?.optString(
                                "kilometers",
                                "0"
                            ) ?: "0",

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

    // ===============================
    // 3. ESA GAIA DR3
    // ===============================

    suspend fun getGaiaStars(): List<GaiaStar> {

        val query = """
            SELECT TOP 15
            source_id, ra, dec,
            phot_g_mean_mag, parallax
            FROM gaiadr3.gaia_source
            WHERE phot_g_mean_mag IS NOT NULL
            ORDER BY phot_g_mean_mag ASC
        """.trimIndent()

        val url =
            "$ESA_BASE?REQUEST=doQuery" +
            "&LANG=ADQL&FORMAT=csv" +
            "&QUERY=${encode(query)}"

        val csv = get(url)

        val lines = csv.lines().filter {
            it.isNotBlank()
        }

        if (lines.size < 2) return emptyList()

        return lines.drop(1).mapNotNull { line ->

            val columns = parseCsvLine(line)

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

    private fun parseCsvLine(line: String): List<String> {

        val result = mutableListOf<String>()
        val current = StringBuilder()
        var quoted = false
        var i = 0

        while (i < line.length) {

            val char = line[i]

            when {
                char == '"' && quoted &&
                    i + 1 < line.length &&
                    line[i + 1] == '"' -> {
                    current.append('"')
                    i++
                }

                char == '"' -> {
                    quoted = !quoted
                }

                char == ',' && !quoted -> {
                    result.add(current.toString())
                    current.clear()
                }

                else -> current.append(char)
            }

            i++
        }

        result.add(current.toString())

        return result
    }

    // ===============================
    // 4. NASA DONKI SPACE WEATHER
    // ===============================

    suspend fun getDonkiEvents(
        type: String = "all",
        days: Int = 7
    ): List<SpaceWeatherEvent> {

        val range = dateRange(days)

        val url =
            "$NASA_BASE/DONKI/notifications" +
            "?startDate=${range.first}" +
            "&endDate=${range.second}" +
            "&type=${encode(type)}" +
            "&api_key=$NASA_KEY"

        val array = JSONArray(get(url))

        val results = mutableListOf<SpaceWeatherEvent>()

        for (i in 0 until array.length()) {

            val item = array.getJSONObject(i)

            results.add(
                SpaceWeatherEvent(
                    id = item.optString(
                        "messageID",
                        i.toString()
                    ),

                    type = item.optString(
                        "messageType",
                        "Space Weather"
                    ),

                    title = item.optString(
                        "messageType",
                        "Space Weather Event"
                    ),

                    date = item.optString(
                        "messageIssueTime",
                        ""
                    ),

                    description = item.optString(
                        "messageBody",
                        "No description available"
                    ),

                    link = item.optString(
                        "messageURL",
                        ""
                    )
                )
            )
        }

        return results
    }

    // ===============================
    // 5. NASA EPIC EARTH IMAGES
    // ===============================

    suspend fun getEarthImages(): List<EarthImage> {

        val url =
            "$NASA_BASE/EPIC/api/natural/images" +
            "?api_key=$NASA_KEY"

        val array = JSONArray(get(url))

        val results = mutableListOf<EarthImage>()

        for (i in 0 until array.length()) {

            val item = array.getJSONObject(i)

            val identifier =
                item.optString("image", "")

            val date =
                item.optString("date", "")

            val parts = date.split("-")

            if (parts.size < 3 || identifier.isBlank()) {
                continue
            }

            val year = parts[0]
            val month = parts[1]
            val day = parts[2].take(2)

            val imageUrl =
                "https://epic.gsfc.nasa.gov/archive/natural/" +
                "$year/$month/$day/png/$identifier.png"

            results.add(
                EarthImage(
                    identifier = identifier,
                    caption = item.optString(
                        "caption",
                        "Earth from DSCOVR"
                    ),
                    date = date,
                    imageUrl = imageUrl
                )
            )
        }

        return results
    }

    // ===============================
    // 6. NASA EONET EARTH EVENTS
    // ===============================

    suspend fun getEarthEvents(): List<EarthEvent> {

        val url =
            "$EONET_BASE/events?status=open&limit=50"

        val response = JSONObject(get(url))

        val events =
            response.optJSONArray("events")
                ?: JSONArray()

        val results = mutableListOf<EarthEvent>()

        for (i in 0 until events.length()) {

            val item = events.getJSONObject(i)

            val categories =
                item.optJSONArray("categories")

            val category =
                categories?.optJSONObject(0)
                    ?.optString("title")
                    ?: "Natural Event"

            val geometry =
                item.optJSONArray("geometry")

            val date =
                geometry?.optJSONObject(0)
                    ?.optString("date")
                    ?: ""

            results.add(
                EarthEvent(
                    id = item.optString("id", ""),
                    title = item.optString(
                        "title",
                        "Unknown Event"
                    ),
                    description = item.optString(
                        "description",
                        "No description available"
                    ),
                    category = category,
                    date = date,
                    link = item.optString("link", "")
                )
            )
        }

        return results
    }

    // ===============================
    // 7. NASA IMAGE LIBRARY SEARCH
    // ===============================

    suspend fun searchNasaImages(
        query: String
    ): List<NasaImage> {

        require(query.isNotBlank()) {
            "Search query cannot be empty"
        }

        val url =
            "$NASA_IMAGES?q=${encode(query)}" +
            "&media_type=image&page_size=20"

        val response = JSONObject(get(url))

        val collection =
            response.getJSONObject("collection")

        val items =
            collection.optJSONArray("items")
                ?: JSONArray()

        val results = mutableListOf<NasaImage>()

        for (i in 0 until items.length()) {

            val item = items.getJSONObject(i)

            val data =
                item.optJSONArray("data")
                    ?.optJSONObject(0) ?: continue

            val links =
                item.optJSONArray("links")

            val imageUrl =
                links?.optJSONObject(0)
                    ?.optString("href", "")
                    ?: ""

            if (imageUrl.isBlank()) continue

            results.add(
                NasaImage(
                    id = data.optString(
                        "nasa_id",
                        ""
                    ),
                    title = data.optString(
                        "title",
                        "Untitled"
                    ),
                    description = data.optString(
                        "description",
                        "No description available"
                    ),
                    imageUrl = imageUrl,
                    date = data.optString(
                        "date_created",
                        ""
                    )
                )
            )
        }

        return results
    }
}
