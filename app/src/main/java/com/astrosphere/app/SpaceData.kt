
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

// --------------------------------------------------
// DATA MODELS
// --------------------------------------------------

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

// --------------------------------------------------
// SPACE DATA API
// --------------------------------------------------

object SpaceData {

    private const val NASA_KEY = BuildConfig.NASA_API_KEY

    private const val NASA_BASE =
        "https://api.nasa.gov"

    // --------------------------------------------------
    // HTTP CONNECTION
    // --------------------------------------------------

    private suspend fun get(urlString: String): String =
        withContext(Dispatchers.IO) {

            var connection: HttpURLConnection? = null

            try {
                connection =
                    URL(urlString).openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 20000
                connection.readTimeout = 30000
                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )
                connection.setRequestProperty(
                    "User-Agent",
                    "ASTROSPHERE-Android/2.0"
                )

                val responseCode = connection.responseCode

                val stream =
                    if (responseCode in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                val response = stream?.bufferedReader()?.use {
                    it.readText()
                } ?: ""

                if (responseCode !in 200..299) {
                    throw Exception(
                        "HTTP $responseCode: " +
                        response.take(500)
                    )
                }

                response

            } catch (e: Exception) {
                throw Exception(
                    e.message ?: "Network connection failed"
                )
            } finally {
                connection?.disconnect()
            }
        }

    // --------------------------------------------------
    // DATE UTILITIES
    // --------------------------------------------------

    private fun dateRange(days: Int): Pair<String, String> {

        val formatter =
            SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val endCalendar = Calendar.getInstance()
        val startCalendar = Calendar.getInstance()

        startCalendar.add(
            Calendar.DAY_OF_YEAR,
            -(days.coerceAtLeast(1) - 1)
        )

        return Pair(
            formatter.format(startCalendar.time),
            formatter.format(endCalendar.time)
        )
    }

    private fun encode(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
    }

    // --------------------------------------------------
    // 1. NASA ASTRONOMY PICTURE OF THE DAY
    // Updated WordPress API
    // --------------------------------------------------

    suspend fun getApod(): Apod {

        val url =
            "https://science.nasa.gov/wp-json/wp/v2/apod-basic/" +
            "?api_key=$NASA_KEY"

        val array = JSONArray(get(url))

        if (array.length() == 0) {
            throw Exception("NASA APOD returned no records")
        }

        val response = array.getJSONObject(0)

        val mediaType =
            response.optString("media_type", "image")

        if (mediaType != "image") {
            throw Exception(
                "Today's APOD is not an image. " +
                "Please try again later."
            )
        }

        val imageUrl =
            response.optString("hdurl")
                .ifBlank {
                    response.optString("url")
                }

        if (imageUrl.isBlank()) {
            throw Exception("APOD image URL is missing")
        }

        return Apod(
            title = response.optString(
                "title",
                "Astronomy Picture of the Day"
            ),
            explanation = response.optString(
                "explanation",
                "No explanation available"
            ),
            imageUrl = imageUrl,
            date = response.optString("date", "")
        )
    }

    // --------------------------------------------------
    // 2. NEAR EARTH ASTEROIDS
    // --------------------------------------------------

    suspend fun getAsteroids(): List<Asteroid> {

        val (startDate, endDate) = dateRange(7)

        val url =
            "$NASA_BASE/neo/rest/v1/feed" +
            "?start_date=$startDate" +
            "&end_date=$endDate" +
            "&api_key=$NASA_KEY"

        val response = JSONObject(get(url))

        val objects =
            response.optJSONObject("near_earth_objects")
                ?: return emptyList()

        val results = mutableListOf<Asteroid>()

        val dates = objects.keys()

        while (dates.hasNext()) {

            val date = dates.next()
            val dailyObjects = objects.optJSONArray(date)
                ?: continue

            for (i in 0 until dailyObjects.length()) {

                val item =
                    dailyObjects.getJSONObject(i)

                val diameterData =
                    item.optJSONObject(
                        "estimated_diameter"
                    )?.optJSONObject("kilometers")

                val diameter =
                    diameterData?.optDouble(
                        "estimated_diameter_max",
                        0.0
                    ) ?: 0.0

                val closeApproach =
                    item.optJSONArray(
                        "close_approach_data"
                    )?.optJSONObject(0)

                val velocity =
                    closeApproach
                        ?.optJSONObject(
                            "relative_velocity"
                        )
                        ?.optString(
                            "kilometers_per_hour",
                            "Unknown"
                        ) ?: "Unknown"

                val distance =
                    closeApproach
                        ?.optJSONObject(
                            "miss_distance"
                        )
                        ?.optString(
                            "kilometers",
                            "Unknown"
                        ) ?: "Unknown"

                results.add(
                    Asteroid(
                        name = item.optString(
                            "name",
                            "Unknown asteroid"
                        ),
                        diameter = diameter,
                        velocity = velocity,
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

    // --------------------------------------------------
    // 3. ESA GAIA STAR CATALOGUE
    // Gaia DR3 TAP QUERY
    // --------------------------------------------------

    suspend fun getGaiaStars(): List<GaiaStar> {

        val query = """
            SELECT TOP 40
            source_id,
            ra,
            dec,
            phot_g_mean_mag,
            parallax
            FROM gaiadr3.gaia_source
            WHERE phot_g_mean_mag IS NOT NULL
            AND parallax IS NOT NULL
            ORDER BY phot_g_mean_mag ASC
        """.trimIndent()

        val url =
            "https://gea.esac.esa.int/tap-server/tap/sync" +
            "?REQUEST=doQuery" +
            "&LANG=ADQL" +
            "&FORMAT=csv" +
            "&QUERY=${encode(query)}"

        val csv = get(url)

        val lines = csv.lines()
            .filter { it.isNotBlank() }

        if (lines.size <= 1) {
            return emptyList()
        }

        val results = mutableListOf<GaiaStar>()

        for (line in lines.drop(1)) {

            val columns = parseCsvLine(line)

            if (columns.size < 5) {
                continue
            }

            results.add(
                GaiaStar(
                    id = columns[0],
                    ra = columns[1],
                    dec = columns[2],
                    magnitude = columns[3],
                    parallax = columns[4]
                )
            )
        }

        return results
    }

    // --------------------------------------------------
    // CSV PARSER
    // --------------------------------------------------

    private fun parseCsvLine(line: String): List<String> {

        val result = mutableListOf<String>()
        val current = StringBuilder()

        var insideQuotes = false
        var i = 0

        while (i < line.length) {

            val character = line[i]

            when {
                character == '"' -> {

                    if (
                        insideQuotes &&
                        i + 1 < line.length &&
                        line[i + 1] == '"'
                    ) {
                        current.append('"')
                        i++
                    } else {
                        insideQuotes = !insideQuotes
                    }
                }

                character == ',' && !insideQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }

                else -> current.append(character)
            }

            i++
        }

        result.add(current.toString())

        return result
    }

    // --------------------------------------------------
    // 4. NASA SPACE WEATHER / DONKI
    // --------------------------------------------------

    suspend fun getDonkiEvents(
        type: String = "all",
        days: Int = 7
    ): List<SpaceWeatherEvent> {

        val (startDate, endDate) = dateRange(days)

        val url =
            "$NASA_BASE/DONKI/notifications" +
            "?startDate=$startDate" +
            "&endDate=$endDate" +
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
                        "event_$i"
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
                    link = item.optString("messageURL", "")
                )
            )
        }

        return results
    }

    // --------------------------------------------------
    // 5. NASA EPIC EARTH IMAGES
    // Official Goddard endpoint
    // --------------------------------------------------

    suspend fun getEarthImages(): List<EarthImage> {

        val url =
            "https://epic.gsfc.nasa.gov/api/natural"

        val array = JSONArray(get(url))

        val results = mutableListOf<EarthImage>()

        for (i in 0 until array.length()) {

            val item = array.getJSONObject(i)

            val identifier =
                item.optString("image", "")

            val date =
                item.optString("date", "")

            if (
                identifier.isBlank() ||
                date.isBlank()
            ) {
                continue
            }

            val dateOnly =
                date.substringBefore("T")
                    .substringBefore(" ")

            val parts = dateOnly.split("-")

            if (parts.size != 3) {
                continue
            }

            val year = parts[0]
            val month = parts[1]
            val day = parts[2]

            val imageUrl =
                "https://epic.gsfc.nasa.gov/archive/" +
                "natural/$year/$month/$day/jpg/" +
                "$identifier.jpg"

            results.add(
                EarthImage(
                    identifier = identifier,
                    caption = item.optString(
                        "caption",
                        "Earth observed by DSCOVR"
                    ),
                    date = date,
                    imageUrl = imageUrl
                )
            )
        }

        return results
    }

    // --------------------------------------------------
    // 6. NASA EONET EARTH EVENTS
    // --------------------------------------------------

    suspend fun getEarthEvents(): List<EarthEvent> {

        val url =
            "https://eonet.gsfc.nasa.gov/api/v3/events" +
            "?status=open&limit=50"

        val response = JSONObject(get(url))

        val array =
            response.optJSONArray("events")
                ?: return emptyList()

        val results = mutableListOf<EarthEvent>()

        for (i in 0 until array.length()) {

            val item = array.getJSONObject(i)

            val categories =
                item.optJSONArray("categories")

            val category =
                categories
                    ?.optJSONObject(0)
                    ?.optString("title", "Earth Event")
                    ?: "Earth Event"

            val geometry =
                item.optJSONArray("geometry")

            val date =
                geometry
                    ?.optJSONObject(0)
                    ?.optString("date", "")
                    ?: ""

            results.add(
                EarthEvent(
                    id = item.optString(
                        "id",
                        "earth_event_$i"
                    ),
                    title = item.optString(
                        "title",
                        "Unnamed Earth Event"
                    ),
                    description = item.optString(
                        "description",
                        "No additional description available"
                    ),
                    category = category,
                    date = date,
                    link = item.optString("link", "")
                )
            )
        }

        return results
    }

    // --------------------------------------------------
    // 7. NASA IMAGE AND VIDEO LIBRARY
    // --------------------------------------------------

    suspend fun searchNasaImages(
        query: String
    ): List<NasaImage> {

        if (query.isBlank()) {
            throw Exception("Enter a search term")
        }

        val url =
            "https://images-api.nasa.gov/search" +
            "?q=${encode(query)}" +
            "&media_type=image"

        val response = JSONObject(get(url))

        val collection =
            response.optJSONObject("collection")
                ?: return emptyList()

        val items =
            collection.optJSONArray("items")
                ?: return emptyList()

        val results = mutableListOf<NasaImage>()

        for (i in 0 until items.length()) {

            val item = items.getJSONObject(i)

            val data =
                item.optJSONArray("data")
                    ?.optJSONObject(0)
                    ?: continue

            val links =
                item.optJSONArray("links")

            val imageUrl =
                links
                    ?.optJSONObject(0)
                    ?.optString("href", "")
                    ?: ""

            if (imageUrl.isBlank()) {
                continue
            }

            results.add(
                NasaImage(
                    id = data.optString(
                        "nasa_id",
                        "nasa_image_$i"
                    ),
                    title = data.optString(
                        "title",
                        "Untitled NASA Image"
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
