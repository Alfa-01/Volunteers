package ru.sicampus.bootcamp2025.ui.mainscreen.allcenters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.sicampus.bootcamp2025.databinding.CenterItemBinding
import ru.sicampus.bootcamp2025.domain.entities.CenterEntity
import ru.sicampus.bootcamp2025.ui.utils.distanceBetweenTwoPoints
import kotlin.math.roundToInt

class CenterListAdapter(private var location: Pair<Double, Double>, private val onClick: (centerId: Int, centerName: String) -> Unit) :
    PagingDataAdapter<CenterEntity, CenterListAdapter.ViewHolder>(CenterDiff) {

    class ViewHolder(
        private val binding: CenterItemBinding,
        private val location: Pair<Double, Double>,
        private val onClick: (centerId: Int, centerName: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CenterEntity) {
            binding.name.text = item.name
            binding.address.text = item.address
            binding.tag.text = item.tag
            binding.phone.text = item.phone
            binding.distanceInfo.text =
                "${distanceBetweenTwoPoints(
                    location.first, item.latitude, location.second, item.longitude
                ).roundToInt()} метров"
            Picasso.get().load(item.imageUrl).into(binding.centerImage)

            binding.root.setOnClickListener { onClick(item.id, item.name) }
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
            location = location,
            onClick = onClick
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

    fun updateLocation(newLocation: Pair<Double, Double>) {
        location = newLocation
    }
}