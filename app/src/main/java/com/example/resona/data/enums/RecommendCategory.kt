package com.example.resona.data.enums // 패키지 경로 변경

enum class RecommendCategory(val label: String) {
    ROMANCE("사랑/설렘"),
    FAREWELL("이별/슬픔"),
    COMFORT("위로/응원"),
    ENERGY("신남/에너지"),
    LONGING("그리움/추억"),
    RELAX("일상/여유"),
    OTHER("기타");

    companion object {
        fun fromLabel(label: String): RecommendCategory? =
            RecommendCategory.entries.find { it.label == label }
    }
}