package com.droid.kat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import javax.inject.Inject

class CatListView @Inject constructor() {

    lateinit var catAdapter: CatAdapter

    interface Callbacks {
        fun onItemClick(catData: CatData)
        fun loadData(catList: List<CatData>)
        fun loadNextPage()
    }

    fun init(recyclerView: RecyclerView, callbacks: Callbacks) {
        catAdapter = CatAdapter(callbacks)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context)
            adapter = catAdapter
        }
    }

    fun loadData(catList: List<CatData>) {
        catAdapter.submitList(catList.toMutableList())
    }

    class CatAdapter(val callbacks: Callbacks): ListAdapter<CatData, CatAdapter.ViewHolder>(CatResponseDiff()) {
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            var tvBreedName: TextView
            var ivCatImage: ImageView
            var rootView: CardView

            init {
                tvBreedName = view.findViewById(R.id.tvBreedName)
                ivCatImage = view.findViewById(R.id.ivCatImage)
                rootView = view.findViewById(R.id.rootView)

                rootView.setOnClickListener {
                    callbacks.onItemClick(getItem(adapterPosition))
                }
            }

            fun bindItem(catData: CatData) {
                tvBreedName.text = catData.breeds?.firstOrNull()?.name ?: "Unknown breed"

                catData.url?.let { url ->
                    ivCatImage.load(url) {
                        crossfade(true)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cat, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItem(getItem(position))
        }
    }

    class CatResponseDiff: DiffUtil.ItemCallback<CatData>() {
        override fun areItemsTheSame(oldItem: CatData, newItem: CatData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CatData, newItem: CatData): Boolean {
            return oldItem == newItem
        }
    }
}