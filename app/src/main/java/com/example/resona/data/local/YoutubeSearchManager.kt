package com.example.resona.data.local

import android.text.Html
import android.util.Log
import com.example.resona.BuildConfig
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
    private const val API_KEY = BuildConfig.YOUTUBE_API_KEY

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

                    // 1. HTML 디코딩 적용: &quot;, &amp; 등을 실제 문자로 변환
                    val rawTitle = snippet.getString("title")
                    val decodedTitle = Html.fromHtml(rawTitle, Html.FROM_HTML_MODE_LEGACY).toString()

                    // 2. 고화질 썸네일 선택 로직
                    val thumbnailUrl = when {
                        thumbnails.has("maxres") -> thumbnails.getJSONObject("maxres").getString("url")
                        thumbnails.has("standard") -> thumbnails.getJSONObject("standard").getString("url")
                        thumbnails.has("high") -> thumbnails.getJSONObject("high").getString("url")
                        thumbnails.has("medium") -> thumbnails.getJSONObject("medium").getString("url")
                        else -> thumbnails.getJSONObject("default").getString("url")
                    }

                    videoList.add(YoutubeVideo(
                        videoId = idObj.getString("videoId"),
                        title = decodedTitle,
                        thumbnailUrl = thumbnailUrl,
                        channelTitle = snippet.getString("channelTitle")
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e("YoutubeError", "검색 실패: ${e.message}")
        }
        return@withContext videoList
    }
}