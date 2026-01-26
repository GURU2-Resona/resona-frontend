package com.example.resona.data.dto

import com.example.resona.data.enums.RecommendCategory
import com.example.resona.data.enums.RecommendScene
import com.google.gson.annotations.SerializedName

data class PostCreateRequestDto(
    @SerializedName("songTitle") val songTitle: String,      // 유튜브 영상 제목
    @SerializedName("songUrl") val songUrl: String,          // 유튜브 영상 URL
    @SerializedName("title") val title: String,            // 추천글 제목
    @SerializedName("content") val content: String,          // 추천글 내용
    @SerializedName("category") val category: RecommendCategory, // 카테고리 Enum
    @SerializedName("customCategory") val customCategory: String?, // 기타 선택 시 입력값
    @SerializedName("scene") val scene: RecommendScene,      // 상황 Enum
    @SerializedName("customScene") val customScene: String?    // 기타 선택 시 입력값
)