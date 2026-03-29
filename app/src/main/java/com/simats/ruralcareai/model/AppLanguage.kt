package com.simats.ruralcareai.model

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeLabel: String?,
    val iconText: String,
) {
    PUNJABI(
        code = "pa",
        displayName = "Punjabi",
        nativeLabel = null,
        iconText = "ਪੰ",
    ),
    HINDI(
        code = "hi",
        displayName = "Hindi",
        nativeLabel = "हिन्दी",
        iconText = "हि",
    ),
    ENGLISH(
        code = "en",
        displayName = "English",
        nativeLabel = null,
        iconText = "En",
    ),
}
