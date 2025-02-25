package ru.netology.weatherprophecyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.weatherprophecyapp.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class HourAdapter : ListAdapter<HourItem, HourAdapter.HourHolder>(HourComparator()) {

    class HourHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HourItem) = with(binding) {
            tvDate.text = item.time
            tvTemp.text = "${item.tempC}ºC"
            tvCondition.text = item.condition.text
            val imIcon = binding.root.findViewById<android.widget.ImageView>(ru.netology.weatherprophecyapp.R.id.im)
            Picasso.get().load("https:" + item.condition.icon).into(imIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourHolder(binding)
    }

    override fun onBindViewHolder(holder: HourHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HourComparator : androidx.recyclerview.widget.DiffUtil.ItemCallback<HourItem>() {
        override fun areItemsTheSame(oldItem: HourItem, newItem: HourItem): Boolean =
            oldItem.time == newItem.time

        override fun areContentsTheSame(oldItem: HourItem, newItem: HourItem): Boolean =
            oldItem == newItem
    }
}

