package com.example.openinapp.ui.dashboard.links

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
import com.example.openinapp.databinding.FragmentLinksBinding
import com.example.openinapp.ui.dashboard.links.adapter.MyAdapter
import com.example.openinapp.util.Resource
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class LinksFragment : Fragment() {

    private lateinit var linksViewModel: LinksViewModel

    private var _binding: FragmentLinksBinding? = null
    private val binding get() = _binding!!

    var adapter: MyAdapter? = null
    private var dataList = mutableListOf<RecentLink>()

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
        val chart = _binding?.chart as? LineChart
        val data = getData(36, 100.0f)

        // add some transparency to the color with "& 0x90FFFFFF"
        setupChart(chart, data!!, requireContext().getColor(R.color.white))
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
                    if(response.data?.data?.recent_links!= null){
                        dataList = response.data.data.recent_links
                    }
                    adapter = response.data?.data?.recent_links?.let { MyAdapter(it) }

                }
                is Resource.Error -> {

                }
                is Resource.Loading -> {

                }
            }
        })
    }

    private fun bindView() {
        // Set up RecyclerView

        _binding?.rvLinks?.layoutManager = LinearLayoutManager(requireContext())
        _binding?.rvLinks?.adapter = adapter

        val initialItems = dataList.take(4) // Get the first 4 items from the data list
        adapter?.addItems(initialItems)

        _binding?.ivSettings?.setOnClickListener {
            adapter?.setShowAllData(true)
        }
        _binding?.toggleGroup?.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnTopLinks -> {}
                    R.id.btnRecentLinks -> {

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

}