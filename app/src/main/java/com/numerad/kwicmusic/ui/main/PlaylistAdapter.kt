package com.numerad.kwicmusic.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.models.ui.PlaylistUiModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.playlist.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class PlaylistAdapter(
    val context: Context,
    var values: List<PlaylistUiModel>,
    private val listener: MainFragment.OnListInteractionListener?
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>(), KoinComponent {

    private val onClickListener: View.OnClickListener
    private val picasso: Picasso by inject()

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as PlaylistUiModel
            listener?.onListInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = values[position]

        picasso.load(playlist.thumbnailUrl).into(holder.thumbnailView)
        holder.titleView.text = playlist.title
        holder.numberView.text = context.getString(R.string.num_of_items, playlist.number.toString())

        with(holder.view) {
            tag = playlist
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailView: ImageView = view.playlist_thumbnail
        val titleView: TextView = view.playlist_title
        val numberView: TextView = view.playlist_number

        override fun toString(): String {
            return super.toString() + " '" +
                    titleView.text + ", " +
                    numberView.text + "'"
        }
    }
}
