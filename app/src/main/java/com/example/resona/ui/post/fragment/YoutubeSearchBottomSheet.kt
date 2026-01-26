package com.example.resona.ui.post.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resona.R
import com.example.resona.YoutubeVideo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class YoutubeSearchBottomSheet(
    private val videos: List<YoutubeVideo>,
    private val onVideoSelected: (YoutubeVideo) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_youtube_search_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rv_search_results)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = SearchAdapter(videos) {
            onVideoSelected(it)
            dismiss()
        }
    }

    // 내부 어댑터 클래스
    inner class SearchAdapter(
        private val items: List<YoutubeVideo>,
        private val onClick: (YoutubeVideo) -> Unit
    ) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val ivThumb = v.findViewById<ImageView>(R.id.iv_search_item_thumb)
            val tvTitle = v.findViewById<TextView>(R.id.tv_search_item_title)
            val tvChannel = v.findViewById<TextView>(R.id.tv_search_item_channel)
            init { v.setOnClickListener { onClick(items[adapterPosition]) } }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_youtube_search, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvTitle.text = item.title
            holder.tvChannel.text = item.channelTitle
            Glide.with(holder.ivThumb).load(item.thumbnailUrl).into(holder.ivThumb)
        }

        override fun getItemCount() = items.size
    }
}