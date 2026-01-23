package com.example.resona.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.resona.databinding.ItemPostCardBinding

data class PostModel(
    val title: String,
    val subhead: String,
    val category: String,
    val situation: String
)

class PostAdapter(private var items: List<PostModel>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(val binding: ItemPostCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvSubhead.text = item.subhead
        // 필요시 이미지 로딩 라이브러리(Glide 등) 사용 위치
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PostModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}