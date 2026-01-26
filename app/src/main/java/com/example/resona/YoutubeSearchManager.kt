package com.example.resona

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object YoutubeSearchManager {

    private const val API_KEY = "AIzaSyCAXy7g8n7-iek_2v2mG0TBmmXXj-fu1o0"

    suspend fun getFirstVideoId(songTitle: String, artistName: String): String? = withContext(Dispatchers.IO) {
        try {
            val query = "$songTitle $artistName lyrics".replace(" ", "%20")

            val urlString = "https://www.googleapis.com/youtube/v3/search?part=id&q=$query&maxResults=1&type=video&videoEmbeddable=true&key=$API_KEY"

            val response = URL(urlString).readText()
            val jsonObject = JSONObject(response)

            if (jsonObject.has("items")) {
                val items = jsonObject.getJSONArray("items")
                if (items.length() > 0) {
                    val videoId = items.getJSONObject(0).getJSONObject("id").getString("videoId")
                    Log.d("YoutubeSearch", "videoId: $videoId")
                    return@withContext videoId
                }
            }
        } catch (e: Exception) {
            Log.e("YoutubeError", "${e.message}")
        }
        return@withContext null
    }
}