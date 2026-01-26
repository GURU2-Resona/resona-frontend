package com.example.resona.data.enums

enum class RecommendScene(val label: String) {
    COMMUTE("출퇴근길"),
    RAIN("비오는 날"),
    DAWN("새벽 감성"),
    TRAVEL("산책/여행"),
    FOCUS("집중/작업"),
    EXERCISE("운동/활동"),
    OTHER("기타");

    companion object {
        fun fromLabel(label: String): RecommendScene? =
            RecommendScene.entries.find { it.label == label }
    }
}