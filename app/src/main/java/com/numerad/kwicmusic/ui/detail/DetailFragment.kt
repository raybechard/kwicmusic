package com.numerad.kwicmusic.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.models.ui.PlaylistItemUiModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class DetailFragment(val title: String, val id: String, private val thumbnailUrl: String) :
    Fragment(), KoinComponent {

    private lateinit var viewModel: DetailViewModel
    private lateinit var adapter: ItemAdapter
    private lateinit var detailView: View
    private var listener: OnItemInteractionListener? = null
    private val picasso: Picasso by inject()

    interface OnItemInteractionListener {
        fun onItemInteraction(playlistItem: PlaylistItemUiModel?)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailView = inflater.inflate(R.layout.fragment_detail, container, false)
        return detailView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ItemAdapter(listOf(), listOf(), listOf(), listener)
        detail_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        detail_list.adapter = adapter
        detail_title.text = title
        picasso.load(thumbnailUrl).into(detail_thumbnail)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        viewModel.setPlaylistId(id)

        val titleObserver = Observer<String> { detail_number.text = it }
        viewModel.titleLiveData.observe(viewLifecycleOwner, titleObserver)

        val numberObserver = Observer<String> {
            detail_number.text = if (!it.isNullOrEmpty())
                context?.getString(R.string.num_of_items, it)
            else ""
        }
        viewModel.numberLiveData.observe(viewLifecycleOwner, numberObserver)

        val durationObserver = Observer<List<String>> {
            adapter.durations = it
            adapter.notifyDataSetChanged()
        }
        viewModel.durationLiveData.observe(viewLifecycleOwner, durationObserver)

        val authorsObserver = Observer<List<String>> {
            adapter.authors = it
            adapter.notifyDataSetChanged()
        }
        viewModel.authorsLiveData.observe(viewLifecycleOwner, authorsObserver)

        val itemsObserver = Observer<List<PlaylistItemUiModel>> { items ->
            adapter.values = items
            adapter.notifyDataSetChanged()
        }
        viewModel.getItems().observe(viewLifecycleOwner, itemsObserver)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnItemInteractionListener)
            listener = context
        else
            throw RuntimeException("$context must implement OnItemInteractionListener")
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        fun newInstance(title: String, id: String, thumbnailUrl: String) =
            DetailFragment(title, id, thumbnailUrl)
    }
}