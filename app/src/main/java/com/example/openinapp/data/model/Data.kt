package com.example.openinapp.data.model

data class Data(
    val overall_url_chart: OverallUrlChart,
    val recent_links: List<RecentLink>,
    val top_links: List<TopLink>
)