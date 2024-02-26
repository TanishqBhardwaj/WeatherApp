package com.example.apps10x.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apps10x.R
import com.example.apps10x.presentation.model.ForecastData

// Adapter class for Forecast item card
class ForecastAdapter(
    private val forecastList: List<ForecastData>
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.forecast_item, parent, false)
        return ForecastViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val currentItem = forecastList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }

    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDay: TextView = itemView.findViewById(R.id.textViewDay)
        private val textViewTemp: TextView = itemView.findViewById(R.id.textViewTemp)

        @SuppressLint("SetTextI18n")
        fun bind(currentItem: ForecastData) {
            textViewDay.text = currentItem.day
            textViewTemp.text = "${currentItem.temperature} C"
        }
    }
}