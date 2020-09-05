package com.numerad.numeriq.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.model.Video
import com.numerad.kwicmusic.data.model.VideoUiModel
import com.numerad.kwicmusic.ui.VideoListAdapter
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: VideoListAdapter
    private lateinit var mainView: View
    private var listener: OnListInteractionListener? = null

    companion object {
        fun newInstance() = MainFragment()
    }

    interface OnListInteractionListener {
        fun onListInteraction(article: Video?)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainView = inflater.inflate(R.layout.fragment_main, container, false)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        video_list.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        adapter = VideoListAdapter(listOf(), listener)
        video_list.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val videoObserver = Observer<List<VideoUiModel>> { newVideos ->
            adapter.values = newVideos
            adapter.notifyDataSetChanged()
        }

        viewModel.getVideos()
            .observe(viewLifecycleOwner, videoObserver)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnListInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
