package com.droid.kat

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

    data class CatViewConfig(
        val recyclerView: RecyclerView,
        val callbacks: Callbacks,
        val paginationOffset: Int = 5
    )

    fun init(catViewConfig: CatViewConfig) {
        catAdapter = CatAdapter(catViewConfig.callbacks, catViewConfig.paginationOffset)

        catViewConfig.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = catAdapter
        }
    }

    fun loadData(catList: List<CatData>) {
        val finalList = catAdapter.currentList.toMutableList()
        finalList.addAll(catList)

        catAdapter.submitList(finalList)

        info { "total items: ${finalList.size}" }
    }

    class CatAdapter(private val callbacks: Callbacks, private val paginationOffset: Int) :
        ListAdapter<CatData, CatAdapter.ViewHolder>(CatResponseDiff()) {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private var tvBreedName: TextView
            private var ivCatImage: ImageView
            private var rootView: CardView

            init {
                tvBreedName = view.findViewById(R.id.tvBreedName)
                ivCatImage = view.findViewById(R.id.ivCatImage)
                rootView = view.findViewById(R.id.rootView)

                rootView.setOnClickListener {
                    callbacks.onItemClick(getItem(adapterPosition))
                }
            }

            fun bindItem(catData: CatData) {
                tvBreedName.text = catData.getBreedName()

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
            if (position == (itemCount - paginationOffset)) {
                callbacks.loadNextPage()
            }

            holder.bindItem(getItem(position))
        }
    }

    class CatResponseDiff : DiffUtil.ItemCallback<CatData>() {
        override fun areItemsTheSame(oldItem: CatData, newItem: CatData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CatData, newItem: CatData): Boolean {
            return oldItem == newItem
        }
    }
}