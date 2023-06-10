package com.example.openinapp.data.model

data class Data(
    val overall_url_chart: OverallUrlChart,
    val recent_links: MutableList<RecentLink>,
    val top_links: MutableList<TopLink>
)