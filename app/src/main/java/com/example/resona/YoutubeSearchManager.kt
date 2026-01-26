package com.example.resona

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class YoutubeVideo(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val channelTitle: String
)

object YoutubeSearchManager {
    private const val API_KEY = "AIzaSyCAXy7g8n7-iek_2v2mG0TBmmXXj-fu1o0"

    suspend fun searchVideos(query: String): List<YoutubeVideo> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<YoutubeVideo>()
        try {
            val encodedQuery = query.replace(" ", "%20")
            val urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=$encodedQuery&maxResults=10&type=video&videoEmbeddable=true&videoSyndicated=true&key=$API_KEY"

            val response = URL(urlString).readText()
            val jsonObject = JSONObject(response)

            if (jsonObject.has("items")) {
                val items = jsonObject.getJSONArray("items")
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val idObj = item.getJSONObject("id")
                    val snippet = item.getJSONObject("snippet")
                    val thumbnails = snippet.getJSONObject("thumbnails")

                    // 고화질 썸네일 선택 (maxres > standard > high > medium > default)
                    val thumbnailUrl = when {
                        thumbnails.has("maxres") -> thumbnails.getJSONObject("maxres").getString("url")
                        thumbnails.has("standard") -> thumbnails.getJSONObject("standard").getString("url")
                        thumbnails.has("high") -> thumbnails.getJSONObject("high").getString("url")
                        thumbnails.has("medium") -> thumbnails.getJSONObject("medium").getString("url")
                        else -> thumbnails.getJSONObject("default").getString("url")
                    }

                    videoList.add(YoutubeVideo(
                        videoId = idObj.getString("videoId"),
                        title = snippet.getString("title"),
                        thumbnailUrl = thumbnailUrl,
                        channelTitle = snippet.getString("channelTitle")
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e("YoutubeError", "${e.message}")
        }
        return@withContext videoList
    }
}