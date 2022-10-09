package com.droid.kat

import android.os.Bundle
import android.os.Message
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CatListView.Callbacks {

    private val katViewModel: KatViewModel by viewModels()

    lateinit var recyclerView: RecyclerView
    lateinit var errorLayout: ConstraintLayout
    lateinit var progressBar: ProgressBar

    @Inject
    lateinit var catListView: CatListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        fetchData()
        observeData()
    }

    private fun initUI() {
        recyclerView = findViewById(R.id.rvCats)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)

        catListView.init(recyclerView, this)
    }

    private fun fetchData() {
        katViewModel.getCatData()
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                with(katViewModel) {
                    homeUiState.collect {
                        when {
                            it.loading -> {
                                info("Activity UI State") { "loading" }
                                showProgressBar(true)
                                clearLoadingState()
                            }
                            it.error != null -> {
                                error("Activity UI State") { it.error }
                                showErrorLayout(it.error, true)
                                clearErrorState()
                            }
                            it.catList?.isNotEmpty() == true -> {
                                info("Activity UI State") { "got cat list: ${it.catList.size}" }
                                showCatListView(it.catList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showCatListView(catList: List<CatData>) {
        progressBar.gone()
        recyclerView.visible()
        loadData(catList)
    }

    fun showProgressBar(show: Boolean) {
        if (show) {
            progressBar.visible()
        } else {
            progressBar.gone()
        }
    }

    fun showErrorLayout(message: String, show: Boolean) {
        if (show) {
            errorLayout.visible()
        } else {
            errorLayout.gone()
        }
    }

    override fun onItemClick(catData: CatData) {

    }

    override fun loadNextPage() {

    }

    override fun loadData(catList: List<CatData>) {
        catListView.loadData(catList)
    }
}