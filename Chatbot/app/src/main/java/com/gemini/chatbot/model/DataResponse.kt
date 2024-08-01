package com.gemini.chatbot.model


data class DataResponse(
    val isUser: Int,
    val prompt: String,
    val imageUri: String,
)
