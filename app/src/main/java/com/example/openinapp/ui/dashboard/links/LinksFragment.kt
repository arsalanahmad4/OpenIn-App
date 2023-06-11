package com.example.openinapp.ui.dashboard.links

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openinapp.R
import com.example.openinapp.data.model.RecentLink
import com.example.openinapp.data.model.TopLink
import com.example.openinapp.databinding.FragmentLinksBinding
import com.example.openinapp.ui.dashboard.links.adapter.RecentLinksAdapter
import com.example.openinapp.ui.dashboard.links.adapter.TopLinksAdapter
import com.example.openinapp.util.Resource
import com.example.openinapp.util.getDay
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import android.graphics.drawable.GradientDrawable
import com.example.openinapp.util.DrawableUtils

class LinksFragment : Fragment(),RecentLinksAdapter.Callbacks,TopLinksAdapter.Callbacks {

    private lateinit var linksViewModel: LinksViewModel

    private var _binding: FragmentLinksBinding? = null
    private val binding get() = _binding!!

    private var recentLinksAdapter: RecentLinksAdapter? = null
    private var mainList: MutableList<RecentLink> = ArrayList()
    private val dummyList: MutableList<RecentLink> = ArrayList()

    private var topLinksAdapter : TopLinksAdapter? = null

    private var topLinksList : MutableList<TopLink> = ArrayList()
    private var dummyTopLinksList : MutableList<TopLink> = ArrayList()

    lateinit var lineList : ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData : LineData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLinksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linksViewModel =
            ViewModelProvider(
                this,
                LinksViewModelProvider(requireActivity().application)
            )[LinksViewModel::class.java]
        linksViewModel.getAllThreads("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI")
//        val chart = _binding?.chart as? LineChart
//        val data = getData(36, 100.0f)
//
//        // add some transparency to the color with "& 0x90FFFFFF"
//        setupChart(chart, data!!, requireContext().getColor(R.color.white))


        bindView()
        bindObserver()
    }

    private fun bindObserver() {
        linksViewModel.dashboardResponseResult.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    _binding?.layoutClicks?.tvMainClicksText?.text = response.data?.today_clicks.toString()
                    _binding?.layoutSource?.tvMainSourceText?.text = response.data?.top_source
                    _binding?.layoutLocation?.tvMainLocationText?.text = response.data?.top_location
                    mainList.clear()
                    dummyList.clear()
                    topLinksList.clear()
                    dummyTopLinksList.clear()
                    if(response.data?.data?.recent_links!= null){
                        mainList = (response.data.data.recent_links)
                        if(mainList.size != 0 && mainList.size>=4){
                            for (i in 0..3) {
                                dummyList.add(mainList[i])
                            }
                        }
                        recentLinksAdapter?.notifyDataSetChanged()
                    }

                    if(response.data?.data?.top_links!= null){
                        topLinksList = (response.data.data.top_links)
                        if(topLinksList.size != 0 && topLinksList.size>=4){
                            for (i in 0..3) {
                                dummyTopLinksList.add(topLinksList[i])
                            }
                        }
                        topLinksAdapter?.notifyDataSetChanged()
                    }

                    lineList = ArrayList() // Initialize the lineList

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val startDate = dateFormat.parse(response.data?.data?.overall_url_chart?.keys?.minOrNull())
                    val endDate = dateFormat.parse(response.data?.data?.overall_url_chart?.keys?.maxOrNull())
                    val calendar = Calendar.getInstance()

                    response.data?.data?.overall_url_chart?.forEach { (key, value) ->
                        val date = dateFormat.parse(key)
                        calendar.time = date

                        if (date in startDate..endDate) {
                            val daysSinceStart = TimeUnit.MILLISECONDS.toDays(date.time - startDate.time).toFloat()
                            lineList.add(Entry(daysSinceStart, value.toFloat()))
                        }
                    }

                    lineDataSet = LineDataSet(lineList, null)
                    lineDataSet.color = Color.parseColor("#0E6FFF")
                    lineDataSet.setDrawValues(false) // Disable value text
                    lineDataSet.setDrawCircles(false) // Disable drawing circles for data points
                    lineDataSet.setDrawFilled(true)

                    val startColor = Color.parseColor("#0E6FFF")
                    val endColor = Color.TRANSPARENT
                    val gradientFill = DrawableUtils.createGradientDrawable(startColor, endColor)
                    lineDataSet.fillDrawable = gradientFill

                    lineData = LineData(lineDataSet)
                    _binding?.chart?.data = lineData

                    val xAxis = _binding?.chart?.xAxis
                    xAxis?.position = XAxis.XAxisPosition.BOTTOM // Set X-axis label position to the bottom
                    xAxis?.setDrawAxisLine(true) // Enable drawing the axis line
                    xAxis?.setDrawLabels(true) // Enable drawing the X-axis labels

                    // Set custom labels for X-axis
                    val labelCount = TimeUnit.MILLISECONDS.toDays(endDate.time - startDate.time).toInt()
                    val xAxisValueFormatter = object : ValueFormatter() {
                        private val format = SimpleDateFormat("d MMM", Locale.US)
                        override fun getFormattedValue(value: Float): String {
                            calendar.time = startDate
                            calendar.add(Calendar.DAY_OF_YEAR, value.toInt())
                            val date = calendar.time
                            return if (value.toInt() % 5 == 0) {
                                format.format(date)
                            } else {
                                ""
                            }
                        }
                    }
                    xAxis?.valueFormatter = xAxisValueFormatter
                    xAxis?.granularity = 1f
                    xAxis?.labelCount = labelCount

                    val yAxisRight = _binding?.chart?.axisRight
                    yAxisRight?.isEnabled = false // Disable right-side label

                    _binding?.chart?.description?.isEnabled = false // Disable graph description

                    _binding?.chart?.legend?.isEnabled = false // Disable legend

                    _binding?.chart?.invalidate() // Refresh the chart

                    // Rest of your code...

                }
                is Resource.Error -> {

                }
                is Resource.Loading -> {

                }
            }
        })
    }

    private fun bindView() {

        _binding?.rvRecentLinks?.layoutManager = LinearLayoutManager(requireContext())
        _binding?.rvTopLinks?.layoutManager = LinearLayoutManager(requireContext())

        topLinksAdapter = TopLinksAdapter(dummyTopLinksList)
        topLinksAdapter!!.setCallback(this)
        topLinksAdapter!!.setWithFooter(true)
        _binding?.rvTopLinks?.adapter = topLinksAdapter

        recentLinksAdapter = RecentLinksAdapter(dummyList)
        recentLinksAdapter!!.setCallback(this)
        recentLinksAdapter!!.setWithFooter(true)
        _binding?.rvRecentLinks?.adapter = recentLinksAdapter

        _binding?.toggleGroup?.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnTopLinks -> {
                        _binding?.rvRecentLinks?.visibility = View.GONE
                        _binding?.rvTopLinks?.visibility = View.VISIBLE
                    }
                    R.id.btnRecentLinks -> {
                        _binding?.rvTopLinks?.visibility = View.GONE
                        _binding?.rvRecentLinks?.visibility = View.VISIBLE
                    }
                }
            } else {
                if (toggleButtonGroup.checkedButtonId == View.NO_ID) {

                }
            }
        }
    }

    private fun setupChart(chart: LineChart?, data: LineData, color: Int) {
        (data.getDataSetByIndex(0) as LineDataSet).circleHoleColor = color

        // no description text
        chart?.description?.isEnabled = false

        // chart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart?.setDrawGridBackground(false)
        //        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart?.setTouchEnabled(true)

        // enable scaling and dragging
        chart?.isDragEnabled = true
        chart?.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately
        chart?.setPinchZoom(false)
        chart?.setBackgroundColor(color)

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart?.setViewPortOffsets(10f, 0f, 10f, 0f)

        // add data
        chart?.data = data

        // get the legend (only possible after setting data)
        val l = chart?.legend
        l?.isEnabled = false
        chart?.axisLeft?.isEnabled = false
        chart?.axisLeft?.spaceTop = 40f
        chart?.axisLeft?.spaceBottom = 40f
        chart?.axisRight?.isEnabled = false
        chart?.xAxis?.isEnabled = false

        // animate calls invalidate()...
        chart?.animateX(2500)
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
        set1.color = requireContext().getColor(R.color.blue)
        set1.highLightColor = requireContext().getColor(R.color.blue)
        set1.setDrawValues(false)
        set1.setDrawCircles(false)

        // create a data object with the data sets
        return LineData(set1)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClickLoadMore() {
        recentLinksAdapter!!.setWithFooter(false) // hide footer

        //dummyList.clear()

        for (i in 4 until mainList.size) {
            dummyList.add(mainList[i])
        }

        recentLinksAdapter!!.notifyDataSetChanged()
    }

    override fun onItemClicked(genreName: String) {

    }

    override fun onClickLoadMoreTopLinks() {
        topLinksAdapter!!.setWithFooter(false)
        for (i in 4 until topLinksList.size) {
            dummyTopLinksList.add(topLinksList[i])
        }
        topLinksAdapter!!.notifyDataSetChanged()
    }

    override fun onTopLinksItemClicked(genreName: String) {

    }

}