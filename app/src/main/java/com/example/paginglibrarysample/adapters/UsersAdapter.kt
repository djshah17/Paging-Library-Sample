package com.example.paginglibrarysample.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.paginglibrarysample.R
import com.example.paginglibrarysample.databinding.UserRowBinding
import com.example.paginglibrarysample.models.User

class UsersAdapter(private val context: Context) : PagedListAdapter<User,UsersAdapter.MyViewHolder>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding: UserRowBinding = DataBindingUtil.inflate(inflater, R.layout.user_row,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemBinding.user = getItem(position)
    }

    class MyViewHolder(val itemBinding: UserRowBinding) : RecyclerView.ViewHolder(itemBinding.root){

        private var binding : UserRowBinding? = null

        init {
            this.binding = itemBinding
        }

    }
    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                newItem == oldItem
        }
    }

}