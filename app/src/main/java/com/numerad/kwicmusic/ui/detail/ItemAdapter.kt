package com.numerad.kwicmusic.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.model.ItemUiModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.playlist_item.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class ItemAdapter(
    var values: List<ItemUiModel>,
    private val listener: DetailFragment.OnItemInteractionListener?
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(), KoinComponent {

    private val onClickListener: View.OnClickListener
    private val picasso: Picasso by inject()

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as ItemUiModel
            listener?.onItemInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        picasso.load(item.thumbnailUrl).into(holder.thumbnailView)
        holder.titleView.text = item.title
        holder.authorView.text = item.author
        holder.durationView.text = item.duration.toString()

        with(holder.view) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailView: ImageView = view.item_thumbnail
        val titleView: TextView = view.item_title
        val authorView: TextView = view.item_author
        val durationView: TextView = view.item_duration

        override fun toString(): String {
            return super.toString() + " '" +
                    titleView.text + ", " +
                    authorView.text + ", " +
                    durationView.text + "'"
        }
    }
}
