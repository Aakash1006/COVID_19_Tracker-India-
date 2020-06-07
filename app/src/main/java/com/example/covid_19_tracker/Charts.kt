package com.example.covid_19_tracker

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Charts : AppCompatActivity() {

    lateinit var data:Response
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts)


        fetchResults()


    }

    private fun fetchResults() {
        GlobalScope.launch {
            val response = withContext(Dispatchers.IO) { Client.api.clone().execute() }
            if (response.isSuccessful) {
                data = Gson().fromJson(response.body?.string(), Response::class.java)
                launch(Dispatchers.Main) {
                spinnerupdate()
                }
            }
        }
    }

    private fun spinnerupdate() {
        var arraySpinner=ArrayList<String>()
        for(i in 0..data.statewise.size-1){
            data.statewise[i].state?.let { arraySpinner.add(it) }
        }

        val s = findViewById(R.id.Spinner) as Spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, arraySpinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s.adapter = adapter

        s?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                var pieChart=findViewById<PieChart>(R.id.piechart)

                pieChart.setUsePercentValues(true)
                pieChart.getDescription().setEnabled(true)
                pieChart.setExtraOffsets(5f,10f,5f,5f)

                pieChart.setDragDecelerationFrictionCoef(0.95f)

                pieChart.setDrawHoleEnabled(true)
                pieChart.setHoleColor(Color.WHITE)
                pieChart.setTransparentCircleRadius(61f)

                val yValues=ArrayList<PieEntry>()

                data.statewise[0].active?.toFloat()?.let { PieEntry(it,"Active") }?.let {
                    yValues.add(it)
                }
                data.statewise[0].recovered?.toFloat()?.let { PieEntry(it,"Recovered") }?.let {
                    yValues.add(it)
                }
                data.statewise[0].deaths?.toFloat()?.let { PieEntry(it,"Deceased") }?.let {
                    yValues.add(it)
                }

                pieChart.animateY(1000,Easing.EasingOption.EaseInOutCubic)

                val dataSet=PieDataSet(yValues,"")
                dataSet.setSliceSpace(1f)
                dataSet.setSelectionShift(5f)

                val colors: ArrayList<Int> = ArrayList()
                for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

                colors.add(ColorTemplate.getHoloBlue())
                dataSet.setColors(colors)

                val pdata=PieData(dataSet)
                pdata.setValueTextSize(10f)
                pdata.setValueTextColor(Color.BLACK)

                pieChart.setData(pdata)
                pieChart.invalidate();
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                var pieChart=findViewById<PieChart>(R.id.piechart)

                pieChart.setUsePercentValues(true)
                pieChart.getDescription().setEnabled(false)
                pieChart.setExtraOffsets(5f,10f,5f,5f)

                pieChart.setDragDecelerationFrictionCoef(0.95f)

                pieChart.setDrawHoleEnabled(true)
                pieChart.setHoleColor(Color.WHITE)
                pieChart.setTransparentCircleRadius(61f)


                var yValues=ArrayList<PieEntry>()

                data.statewise[position].active?.toFloat()?.let { PieEntry(it,"Active") }?.let {
                    yValues.add(it)
                }
                data.statewise[position].recovered?.toFloat()?.let { PieEntry(it,"Recovered") }?.let {
                    yValues.add(it)
                }
                data.statewise[position].deaths?.toFloat()?.let { PieEntry(it,"Deceased") }?.let {
                    yValues.add(it)
                }

                pieChart.animateY(1000,Easing.EasingOption.EaseInOutCubic)

                val dataSet=PieDataSet(yValues,"")
                dataSet.setSliceSpace(1f)
                dataSet.setSelectionShift(5f)

                val colors: ArrayList<Int> = ArrayList()
                for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

                colors.add(ColorTemplate.getHoloBlue())
                dataSet.setColors(colors)

                val pdata=PieData(dataSet)
                pdata.setValueTextSize(10f)
                pdata.setValueTextColor(Color.BLACK)

                pieChart.setData(pdata)
                pieChart.invalidate();
                Log.i("tag", position.toString())
            }

        }
    }
}
