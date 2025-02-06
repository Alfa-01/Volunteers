package ru.sicampus.bootcamp2025.ui.mainscreen.allcenters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.sicampus.bootcamp2025.databinding.CenterItemBinding
import ru.sicampus.bootcamp2025.domain.entities.CenterEntity
import kotlin.math.hypot

class CenterListAdapter(private val location: Pair<Double, Double>) :
    PagingDataAdapter<CenterEntity, CenterListAdapter.ViewHolder>(CenterDiff) {

    class ViewHolder(
        private val binding: CenterItemBinding,
        private val location: Pair<Double, Double>
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CenterEntity) {
            binding.name.text = item.name
            binding.address.text = item.address
            binding.tag.text = item.tag
            binding.phone.text = item.phone
            binding.distanceInfo.text =
                "${hypot(item.latitude - location.first, item.longitude - location.second)}"
            Picasso.get().load(item.imageUrl).into(binding.centerImage)
        }
    }

    object CenterDiff : DiffUtil.ItemCallback<CenterEntity>() {
        override fun areContentsTheSame(oldItem: CenterEntity, newItem: CenterEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: CenterEntity, newItem: CenterEntity): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CenterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            location
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            getItem(position) ?: CenterEntity(
                id = -1,
                name = "Loading...",
                address = "г. N, ул. M, д. 123",
                phone = "+7 987 654 32 10",
                latitude = 100.0,
                longitude = 100.0,
                tag = "Loading...",
                imageUrl = "https://www.alleycat.org/wp-content/uploads/2019/03/FELV-cat.jpg"
            )
        )
    }
}