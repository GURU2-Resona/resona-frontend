package com.example.resona.ui.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.data.dto.PostResponseDto
import com.example.resona.databinding.ItemPostCardBinding
import java.util.regex.Pattern

class PostAdapter(
    private var items: List<PostResponseDto>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // 클릭 리스너 콜백 추가
    var onItemClick: ((PostResponseDto) -> Unit)? = null

    class PostViewHolder(val binding: ItemPostCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvTitle.text = item.title
            tvSubhead.text = item.songTitle

            // 프로필 이미지 (플레이스홀더 추가)
            Glide.with(ivProfile.context)
                .load(item.writerProfileImage)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .circleCrop()
                .into(ivProfile)

            // 유튜브 썸네일
            val videoId = extractVideoId(item.songUrl)
            val imageUrl = if (videoId != null) "https://img.youtube.com/vi/$videoId/maxresdefault.jpg" else item.songUrl

            Glide.with(ivThumbnail.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_thumnail_placeholder)
                .centerCrop()
                .into(ivThumbnail)

            // 아이템 클릭 시 콜백 호출
            root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PostResponseDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun extractVideoId(url: String?): String? {
        if (url.isNullOrEmpty()) return null
        val pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%202F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*"
        val matcher = Pattern.compile(pattern).matcher(url)
        return if (matcher.find()) matcher.group() else null
    }
}