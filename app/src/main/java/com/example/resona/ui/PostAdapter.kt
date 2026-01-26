package com.example.resona.ui

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

            // 1. 좌측 프로필 이미지 로드
            Glide.with(ivProfile.context)
                .load(item.writerProfileImage)
                .placeholder(R.drawable.ic_placeholder) // 기본 프로필 이미지 리소스 필요
                .error(R.drawable.ic_placeholder)
                .circleCrop()
                .into(ivProfile)

            // 2. 우측 유튜브 썸네일 추출 및 로드
            val videoId = extractVideoId(item.albumImage)
            val imageUrl = if (videoId != null) {
                // 유튜브 링크일 경우 고화질 썸네일 주소
                "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
            } else {
                // 일반 이미지 주소일 경우 그대로 로드
                item.albumImage
            }

            Glide.with(ivThumbnail.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_thumnail_placeholder)
                .centerCrop()
                .into(ivThumbnail)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PostResponseDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * URL이 null이거나 비어있을 경우를 대비한 안전한 ID 추출 함수
     */
    private fun extractVideoId(url: String?): String? {
        if (url.isNullOrEmpty()) return null
        val pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%202F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(url)
        return if (matcher.find()) matcher.group() else null
    }
}