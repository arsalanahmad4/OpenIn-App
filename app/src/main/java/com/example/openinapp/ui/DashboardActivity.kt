package com.example.openinapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.openinapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class DashboardActivity : AppCompatActivity() {
    private lateinit var dashboardViewModel: DashboardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dashboardViewModel =
            ViewModelProvider(
                this,
                DashboardViewModelProvider(application)
            )[DashboardViewModel::class.java]
        dashboardViewModel.getAllThreads("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI")
        val chart = findViewById<View>(R.id.chart) as LineChart
        val data = getData(36, 100.0f)

        // add some transparency to the color with "& 0x90FFFFFF"
        setupChart(chart, data!!, getColor(R.color.white))
    }

    private fun setupChart(chart: LineChart, data: LineData, color: Int) {
        (data.getDataSetByIndex(0) as LineDataSet).circleHoleColor = color

        // no description text
        chart.description.isEnabled = false

        // chart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false)
        //        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true)

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false)
        chart.setBackgroundColor(color)

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(10f, 0f, 10f, 0f)

        // add data
        chart.data = data

        // get the legend (only possible after setting data)
        val l = chart.legend
        l.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.axisLeft.spaceTop = 40f
        chart.axisLeft.spaceBottom = 40f
        chart.axisRight.isEnabled = false
        chart.xAxis.isEnabled = false

        // animate calls invalidate()...
        chart.animateX(2500)
    }

    private fun getData(count: Int, range: Float): LineData? {
        val values: ArrayList<Entry> = ArrayList()
        for (i in 0 until count) {
            val `val` = (Math.random() * range).toFloat()
            values.add(Entry(i.toFloat(),`val`))
        }

        // create a dataset and give it a type
        val set1 = LineDataSet(values, "DataSet 1")
        set1.lineWidth = 1.75f
        set1.color = getColor(R.color.blue)
        set1.highLightColor = getColor(R.color.blue)
        set1.setDrawValues(false)
        set1.setDrawCircles(false)

        // create a data object with the data sets
        return LineData(set1)
    }

}