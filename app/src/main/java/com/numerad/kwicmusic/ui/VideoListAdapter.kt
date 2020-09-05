package com.numerad.kwicmusic.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.model.Video
import com.numerad.kwicmusic.data.model.VideoUiModel
import com.numerad.numeriq.ui.main.MainFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.video.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject


class VideoListAdapter(
    var values: List<VideoUiModel>,
    private val listener: MainFragment.OnListInteractionListener?
) : RecyclerView.Adapter<VideoListAdapter.ViewHolder>(), KoinComponent {

    private val onClickListener: View.OnClickListener
    private val picasso: Picasso by inject()

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Video
            listener?.onListInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = values[position]

        holder.sourceView.text = video.title
        picasso.load(video.thumbnailUrl).into(holder.imageView)
        holder.titleView.text = video.title
        holder.descriptionView.text = video.number.toString()

        with(holder.view) {
            tag = video
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val sourceView: TextView = view.source
        val imageView: ImageView = view.image
        val titleView: TextView = view.title
        val descriptionView: TextView = view.description

        override fun toString(): String {
            return super.toString() + " '" +
                    sourceView.text + ", " +
                    titleView.text + ", " +
                    descriptionView.text + "'"
        }
    }
}
