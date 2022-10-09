package com.droid.kat

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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
    lateinit var btnRetry: Button
    lateinit var tvErrorMessage: TextView

    @Inject
    lateinit var catListView: CatListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        fetchData()
        observeData()
    }

    override fun onItemClick(catData: CatData) {
        info { "clicked: $catData" }
    }

    override fun loadNextPage() {
        fetchData()
    }

    override fun loadData(catList: List<CatData>) {
        catListView.loadData(catList)
    }

    private fun initUI() {
        recyclerView = findViewById(R.id.rvCats)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)
        btnRetry = findViewById(R.id.btnRetry)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        val catViewConfig = CatListView.CatViewConfig(
            recyclerView = recyclerView,
            callbacks = this,
            paginationOffset = 10
        )

        catListView.init(catViewConfig)

        btnRetry.setOnClickListener {
            fetchData()
        }
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
                                showProgressBar()
                                clearLoadingState()
                            }
                            it.error != null -> {
                                error("Activity UI State") { it.error }
                                showErrorLayout(it.error)
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
        errorLayout.gone()
        recyclerView.visible()

        loadData(catList)
    }

    private fun showProgressBar() {
        progressBar.visible()
    }

    private fun showErrorLayout(message: String) {
        if (recyclerView.isVisible()) {
            toast(message)
        } else {
            progressBar.gone()
            errorLayout.visible()
            tvErrorMessage.text = message
        }
    }
}