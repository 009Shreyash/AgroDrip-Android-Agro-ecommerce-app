package com.agrodrip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.adapter.agroNewsAdapter.AgroNewsMainAdapter
import com.agrodrip.model.NewsData
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.activity_watch_demo.*
import kotlinx.android.synthetic.main.video_gallary_layout.view.*


class VideoGalleryAdapter(
    val context: Context,
    var alist: ArrayList<String>,
    var titleList: ArrayList<String>,
    val itemClick: OnItemClick
) : RecyclerView.Adapter<VideoGalleryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoGalleryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_gallary_layout, parent, false)
        return ViewHolder(view)
    }



    interface OnItemClick {
        fun onItemClick(youtubeId : String)
    }

    override fun getItemCount(): Int {
        return alist.size
    }

    override fun onBindViewHolder(holder: VideoGalleryAdapter.ViewHolder, position: Int) {
        holder.binding()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun binding() {

            itemView.txtVideoTitle.text = titleList[adapterPosition]


            itemView.webview.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url: String?
                ): Boolean {
                    return false
                }
            }
            val webSettings: WebSettings = itemView.webview.settings
            webSettings.javaScriptEnabled = true
            webSettings.useWideViewPort = true
            webSettings.loadWithOverviewMode = true
            webSettings.javaScriptCanOpenWindowsAutomatically = true

            val videoUrl = alist[adapterPosition]

            itemView.webview.loadUrl("http://www.youtube.com/embed/$videoUrl?autoplay=1&vq=small")


            itemView.fullscreen_button.setOnClickListener{
                itemClick.onItemClick(alist[adapterPosition])
            }


        }

    }

}