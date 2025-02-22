package ru.sicampus.bootcamp2025.ui.mainscreen.allusers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.sicampus.bootcamp2025.databinding.FreeVolunteerItemBinding
import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity

class UserListAdapter(private val onClick: (profileId: Int) -> Unit) :
    PagingDataAdapter<ProfileEntity, UserListAdapter.ViewHolder>(CenterDiff) {

    class ViewHolder(
        private val binding: FreeVolunteerItemBinding,
        private val onClick: (profileId: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProfileEntity) {
            binding.name.text = "${item.name} ${item.lastname}"
            binding.email.text = item.email
            Picasso.get().load(item.photoUrl).into(binding.image)
            binding.root.setOnClickListener { onClick(item.id) }
        }
    }

    object CenterDiff : DiffUtil.ItemCallback<ProfileEntity>() {
        override fun areContentsTheSame(oldItem: ProfileEntity, newItem: ProfileEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: ProfileEntity, newItem: ProfileEntity): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FreeVolunteerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClick = onClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            getItem(position) ?: ProfileEntity(
                id = 10,
                centerId = 10,
                name = "name",
                lastname = "lastname",
                photoUrl = null,
                phoneNumber = "Loading ...",
                email = "Loading ..."
            )
        )
    }

}