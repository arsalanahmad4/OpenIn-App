package com.example.openinapp.data.model

data class TopLink(
    val app: String,
    val created_at: String,
    val domain_id: String,
    val original_image: String,
    val smart_link: String,
    val thumbnail: Any,
    val times_ago: String,
    val title: String,
    val total_clicks: Int,
    val url_id: Int,
    val url_prefix: String,
    val url_suffix: String,
    val web_link: String
)