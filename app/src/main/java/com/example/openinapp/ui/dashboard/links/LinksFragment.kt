package com.example.openinapp.ui.dashboard.links

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openinapp.R
import com.example.openinapp.data.model.RecentLink
import com.example.openinapp.data.model.TopLink
import com.example.openinapp.databinding.FragmentLinksBinding
import com.example.openinapp.ui.dashboard.links.adapter.RecentLinksAdapter
import com.example.openinapp.ui.dashboard.links.adapter.TopLinksAdapter
import com.example.openinapp.util.DividerItemDecoration
import com.example.openinapp.util.DrawableUtils
import com.example.openinapp.util.Resource
import com.example.openinapp.util.getGreetingMessage
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
        linksViewModel.getDashboardApiData()

        bindView()
        bindObserver()
    }

    private fun bindObserver() {
        linksViewModel.dashboardResponseResult.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    handleShimmer(false)
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

                    setGraphPropertiesAndValue(response.data?.data?.overall_url_chart)

                }
                is Resource.Error -> {
                    handleShimmer(false)
                    Toast.makeText(requireContext(),"Error loading data. Please try again later!",Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    handleShimmer(true)

                }
            }
        })
    }

    private fun bindView() {

        _binding?.rvRecentLinks?.layoutManager = LinearLayoutManager(requireContext())
        _binding?.rvTopLinks?.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(requireContext())
        _binding?.rvRecentLinks?.addItemDecoration(dividerItemDecoration)
        _binding?.rvTopLinks?.addItemDecoration(dividerItemDecoration)

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
            }
        }

        _binding?.tvGreetings?.text = getGreetingMessage()
    }

    private fun setGraphPropertiesAndValue(urlChartResponse:Map<String,Int>?){
        lineList = ArrayList() // Initialize the lineList

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate = dateFormat.parse(urlChartResponse?.keys?.minOrNull())
        val endDate = dateFormat.parse(urlChartResponse?.keys?.maxOrNull())
        val calendar = Calendar.getInstance()

        val textFormat = SimpleDateFormat("d MMM", Locale.US)
        _binding?.tvDuration?.text = textFormat.format(startDate) + "-" + textFormat.format(endDate)

        urlChartResponse?.forEach { (key, value) ->
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
    private fun handleShimmer(isShimmering : Boolean){
        if(isShimmering){
            _binding?.shimmerFrameLayout?.root?.visibility = View.VISIBLE
            _binding?.mainLayout?.visibility = View.GONE
        }else{
            _binding?.shimmerFrameLayout?.root?.visibility = View.GONE
            _binding?.mainLayout?.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClickLoadMoreRecentLinks() {
        recentLinksAdapter!!.setWithFooter(false) // hide footer

        //dummyList.clear()

        for (i in 4 until mainList.size) {
            dummyList.add(mainList[i])
        }

        recentLinksAdapter!!.notifyDataSetChanged()
    }

    override fun onRecentLinksItemClicked(recentLink:RecentLink) {
        val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Create a new ClipData object
        val clipData = ClipData.newPlainText("text", recentLink.web_link)

        // Set the clipboard's primary clip
        clipboardManager.setPrimaryClip(clipData)
    }

    override fun onClickLoadMoreTopLinks() {
        topLinksAdapter!!.setWithFooter(false)
        for (i in 4 until topLinksList.size) {
            dummyTopLinksList.add(topLinksList[i])
        }
        topLinksAdapter!!.notifyDataSetChanged()
    }

    override fun onTopLinksItemClicked(topLink:TopLink) {
        val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Create a new ClipData object
        val clipData = ClipData.newPlainText("text", topLink.web_link)

        // Set the clipboard's primary clip
        clipboardManager.setPrimaryClip(clipData)
    }

}