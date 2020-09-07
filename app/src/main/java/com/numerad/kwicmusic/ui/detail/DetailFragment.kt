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
import com.numerad.kwicmusic.data.model.ItemUiModel
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_main.detail_title
import kotlinx.android.synthetic.main.fragment_main.item_list

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var adapter: ItemAdapter
    private lateinit var detailView: View
    private var listener: OnItemInteractionListener? = null

    companion object {
        fun newInstance() = DetailFragment()
    }

    interface OnItemInteractionListener {
        fun onItemInteraction(item: ItemUiModel?)
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

        item_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        adapter = ItemAdapter(listOf(), listener)
        item_list.adapter = adapter
        detail_title.text = "Playlist items" // todo fetch name, combine with resource
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        val titleObserver = Observer<String> { detail_number.text = it }
        viewModel.titleLiveData.observe(viewLifecycleOwner, titleObserver)

        val numberObserver = Observer<String> { detail_number.text = "$it videos" }
        viewModel.numberLiveData.observe(viewLifecycleOwner, numberObserver)

        val itemsObserver = Observer<List<ItemUiModel>> { items ->
            adapter.values = items
            adapter.notifyDataSetChanged()
        }

        viewModel.getItems().observe(viewLifecycleOwner, itemsObserver)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnItemInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnItemInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}