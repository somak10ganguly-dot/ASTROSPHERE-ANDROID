
package com.astrosphere.app

import android.text.Html
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// REPLACE THIS VALUE WITH YOUR REAL NASA API KEY.
private const val NASA_KEY = "LBgfJMk9bCdMWhFrHqGCFhGKNnfPJkkrHDtW7qjQ"

data class Apod(
    val title: String,
    val explanation: String,
    val imageUrl: String,
    val date: String,
    val mediaType: String
)

data class Asteroid(
    val name: String,
    val diameter: String,
    val velocity: String,
    val distance: String,
    val hazardous: Boolean
)

data class GaiaStar(
    val sourceId: String,
    val ra: String,
    val dec: String,
    val magnitude: String,
    val parallax: String
)

data class SpaceWeatherEvent(
    val type: String,
    val date: String,
    val message: String
)

data class EarthImage(
    val identifier: String,
    val caption: String,
    val imageUrl: String
)

data class EarthEvent(
    val title: String,
    val category: String,
    val date: String,
    val description: String
)

data class NasaImage(
    val title: String,
    val description: String,
    val imageUrl: String,
    val date: String
)

object SpaceData {

    private const val NASA_BASE = "https://api.nasa.gov"

    private const val ESA_GAIA =
        "https://gea.esac.esa.int/tap-server/tap/sync"

    private fun encode(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
    }

    private fun checkApiKey() {
        if (
            NASA_KEY.isBlank() ||
            NASA_KEY == "NASA_API_KEY" ||
            NASA_KEY == "YOUR_NASA_API_KEY"
        ) {
            throw Exception(
                "NASA API key is missing. Add your real NASA API key in SpaceData.kt."
            )
        }
    }

    // All HTTP GET requests run on the IO dispatcher.
    private suspend fun get(urlString: String): String =
        withContext(Dispatchers.IO) {

            val connection =
                URL(urlString).openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 20000
                connection.readTimeout = 40000
                connection.setRequestProperty(
                    "User-Agent",
                    "ASTROSPHERE-Android/2.0"
                )
                connection.setRequestProperty(
                    "Accept",
                    "application/json, text/plain, */*"
                )

                val code = connection.responseCode

                val stream = if (code in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val response = stream?.bufferedReader()?.use {
                    it.readText()
                } ?: ""

                if (code !in 200..299) {
                    throw Exception(
                        "HTTP $code: ${response.take(250)}"
                    )
                }

                response
            } finally {
                connection.disconnect()
            }
        }

    // All HTTP POST requests also run on the IO dispatcher.
    private suspend fun post(
        urlString: String,
        formBody: String
    ): String = withContext(Dispatchers.IO) {

        val connection =
            URL(urlString).openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 20000
            connection.readTimeout = 45000
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8"
            )

            connection.setRequestProperty(
                "Accept",
                "text/csv, text/plain, */*"
            )

            connection.outputStream.use {
                it.write(formBody.toByteArray(Charsets.UTF_8))
            }

            val code = connection.responseCode

            val stream = if (code in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val response = stream?.bufferedReader()?.use {
                it.readText()
            } ?: ""

            if (code !in 200..299) {
                throw Exception(
                    "Gaia HTTP $code: ${response.take(250)}"
                )
            }

            response
        } finally {
            connection.disconnect()
        }
    }

    private suspend fun postWithRetry(
        url: String,
        body: String
    ): String {
        var lastError: Exception? = null

        for (attempt in 0 until 3) {
            try {
                return post(url, body)
            } catch (e: Exception) {
                lastError = e

                if (attempt < 2) {
                    delay(1500L * (attempt + 1))
                }
            }
        }

        throw Exception(
            "ESA Gaia service is unavailable after 3 attempts. " +
                (lastError?.message ?: "")
        )
    }

    private fun cleanHtml(value: String): String {
        return Html.fromHtml(
            value,
            Html.FROM_HTML_MODE_COMPACT
        ).toString()
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun utcDate(daysAgo: Int): String {
        val calendar = Calendar.getInstance(
            TimeZone.getTimeZone("UTC")
        )

        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        val formatter = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        formatter.timeZone = TimeZone.getTimeZone("UTC")

        return formatter.format(calendar.time)
    }

    // NASA Astronomy Picture of the Day
    suspend fun getApod(): Apod {
        checkApiKey()

        val url =
            "$NASA_BASE/planetary/apod?api_key=$NASA_KEY"

        val json = JSONObject(get(url))

        return Apod(
            title = json.optString("title", "Astronomy Picture"),
            explanation = cleanHtml(
                json.optString(
                    "explanation",
                    "No description available."
                )
            ),
            imageUrl = json.optString("url", ""),
            date = json.optString("date", ""),
            mediaType = json.optString("media_type", "image")
        )
    }

    // NASA Near Earth Objects
    suspend fun getAsteroids(): List<Asteroid> {
        checkApiKey()

        val url =
            "$NASA_BASE/neo/rest/v1/feed?api_key=$NASA_KEY"

        val json = JSONObject(get(url))

        val objects =
            json.optJSONObject("near_earth_objects")
                ?: JSONObject()

        val results = mutableListOf<Asteroid>()
        val dates = objects.keys()

        while (dates.hasNext()) {
            val date = dates.next()

            val array =
                objects.optJSONArray(date) ?: JSONArray()

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)

                val diameter =
                    item.optJSONObject("estimated_diameter")
                        ?.optJSONObject("kilometers")
                        ?.optDouble("estimated_diameter_max", 0.0)
                        ?: 0.0

                val approach =
                    item.optJSONArray("close_approach_data")
                        ?.optJSONObject(0)

                val velocity =
                    approach
                        ?.optJSONObject("relative_velocity")
                        ?.optString(
                            "kilometers_per_hour",
                            "Unknown"
                        ) ?: "Unknown"

                val distance =
                    approach
                        ?.optJSONObject("miss_distance")
                        ?.optString(
                            "kilometers",
                            "Unknown"
                        ) ?: "Unknown"

                results.add(
                    Asteroid(
                        name = item.optString("name", "Unknown"),
                        diameter = "%.3f km".format(
                            Locale.US,
                            diameter
                        ),
                        velocity = "$velocity km/h",
                        distance = "$distance km",
                        hazardous = item.optBoolean(
                            "is_potentially_hazardous_asteroid",
                            false
                        )
                    )
                )
            }
        }

        return results
    }

    // ESA Gaia DR3
    suspend fun getGaiaStars(): List<GaiaStar> {

        val query = """
            SELECT TOP 20
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

        val body =
            "REQUEST=doQuery" +
                "&LANG=ADQL" +
                "&FORMAT=csv" +
                "&QUERY=${encode(query)}"

        val response = postWithRetry(
            ESA_GAIA,
            body
        )

        return parseGaiaCsv(response)
    }

    private fun parseGaiaCsv(csv: String): List<GaiaStar> {
        val lines = csv.lines()
            .filter { it.isNotBlank() }

        if (lines.size < 2) {
            throw Exception(
                "Gaia returned no star records."
            )
        }

        val headers = parseCsvLine(lines.first())
            .map { it.trim().lowercase(Locale.US) }

        val results = mutableListOf<GaiaStar>()

        for (line in lines.drop(1)) {
            val values = parseCsvLine(line)

            if (values.size < headers.size) continue

            val row = headers.indices.associate {
                headers[it] to values[it].trim()
            }

            results.add(
                GaiaStar(
                    sourceId = row["source_id"] ?: "",
                    ra = row["ra"] ?: "",
                    dec = row["dec"] ?: "",
                    magnitude = row["phot_g_mean_mag"] ?: "",
                    parallax = row["parallax"] ?: ""
                )
            )
        }

        if (results.isEmpty()) {
            throw Exception(
                "Gaia response contained no readable star records."
            )
        }

        return results
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()

        var insideQuotes = false
        var i = 0

        while (i < line.length) {
            val char = line[i]

            when {
                char == '"' &&
                    insideQuotes &&
                    i + 1 < line.length &&
                    line[i + 1] == '"' -> {
                    current.append('"')
                    i++
                }

                char == '"' -> {
                    insideQuotes = !insideQuotes
                }

                char == ',' && !insideQuotes -> {
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

    // NASA DONKI Space Weather Notifications
    suspend fun getSpaceWeather(): List<SpaceWeatherEvent> {
        checkApiKey()

        val startDate = utcDate(30)
        val endDate = utcDate(0)

        val url =
            "$NASA_BASE/DONKI/notifications" +
                "?startDate=$startDate" +
                "&endDate=$endDate" +
                "&type=all" +
                "&api_key=$NASA_KEY"

        val array = JSONArray(get(url))
        val results = mutableListOf<SpaceWeatherEvent>()

        for (i in 0 until minOf(array.length(), 20)) {
            val item = array.getJSONObject(i)

            results.add(
                SpaceWeatherEvent(
                    type = item.optString(
                        "messageType",
                        "Space Weather"
                    ),
                    date = item.optString(
                        "messageIssueTime",
                        ""
                    ),
                    message = cleanHtml(
                        item.optString(
                            "messageBody",
                            ""
                        )
                    )
                )
            )
        }

        return results
    }

    // NASA EPIC Earth Images
    suspend fun getEpicImages(): List<EarthImage> {
        val url = "https://epic.gsfc.nasa.gov/api/natural"

        val array = JSONArray(get(url))
        val results = mutableListOf<EarthImage>()

        for (i in 0 until minOf(array.length(), 10)) {
            val item = array.getJSONObject(i)

            val identifier = item.optString("image", "")
            val date = item.optString("date", "")
            val dateParts =
                date.substringBefore(" ").split("-")

            if (dateParts.size != 3) continue

            val imageUrl =
                "https://epic.gsfc.nasa.gov/archive/natural/" +
                    "${dateParts[0]}/${dateParts[1]}/${dateParts[2]}/" +
                    "jpg/$identifier.jpg"

            results.add(
                EarthImage(
                    identifier = identifier,
                    caption = item.optString(
                        "caption",
                        "Earth from NASA EPIC"
                    ),
                    imageUrl = imageUrl
                )
            )
        }

        return results
    }

    // NASA Earth Observatory Natural Events
    suspend fun getEarthEvents(): List<EarthEvent> {
        val url =
            "https://eonet.gsfc.nasa.gov/api/v3/events?limit=20"

        val json = JSONObject(get(url))

        val array =
            json.optJSONArray("events") ?: JSONArray()

        val results = mutableListOf<EarthEvent>()

        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)

            val categories =
                item.optJSONArray("categories")

            val category =
                categories?.optJSONObject(0)
                    ?.optString("title", "Earth Event")
                    ?: "Earth Event"

            val geometry =
                item.optJSONArray("geometry")

            val date =
                geometry?.optJSONObject(0)
                    ?.optString("date", "")
                    ?: ""

            results.add(
                EarthEvent(
                    title = item.optString(
                        "title",
                        "Unknown Event"
                    ),
                    category = category,
                    date = date,
                    description = item.optString(
                        "description",
                        "No additional description."
                    )
                )
            )
        }

        return results
    }

    // NASA Image and Video Library
    suspend fun searchNasaImages(
        searchTerm: String
    ): List<NasaImage> {

        if (searchTerm.isBlank()) {
            throw Exception("Enter a search term.")
        }

        val url =
            "https://images-api.nasa.gov/search" +
                "?q=${encode(searchTerm)}" +
                "&media_type=image"

        val json = JSONObject(get(url))

        val collection =
            json.optJSONObject("collection")
                ?: JSONObject()

        val items =
            collection.optJSONArray("items")
                ?: JSONArray()

        val results = mutableListOf<NasaImage>()

        for (i in 0 until minOf(items.length(), 20)) {
            val item = items.getJSONObject(i)

            val data =
                item.optJSONArray("data")
                    ?.optJSONObject(0)
                    ?: JSONObject()

            val links =
                item.optJSONArray("links")
                    ?: JSONArray()

            val imageUrl =
                links.optJSONObject(0)
                    ?.optString("href", "")
                    ?: ""

            results.add(
                NasaImage(
                    title = data.optString(
                        "title",
                        "NASA Image"
                    ),
                    description = cleanHtml(
                        data.optString(
                            "description",
                            "No description available."
                        )
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
