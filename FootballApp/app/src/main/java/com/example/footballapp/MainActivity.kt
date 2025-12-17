package com.example.footballapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapp.ui.adapter.MatchAdapter
import com.example.footballapp.ui.viewmodel.MatchViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MatchViewModel
    private lateinit var adapter: MatchAdapter

    private lateinit var rvMatches: RecyclerView
    private lateinit var btnRefresh: Button
    private lateinit var btnAllLeagues: Button
    private lateinit var btnPremierLeague: Button
    private lateinit var btnLaLiga: Button
    private lateinit var btnBundesliga: Button
    private lateinit var btnSerieA: Button
    private lateinit var btnLigue1: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvNoMatches: TextView
    private lateinit var tvCurrentDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MatchViewModel::class.java]
        initViews()
        setupRecyclerView()
        displayCurrentDate()
        observeViewModel()
        setupListeners()

        // 앱 시작 시 전체 리그 로드
        viewModel.loadTodayMatches()
    }

    private fun initViews() {
        rvMatches = findViewById(R.id.rvMatches)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnAllLeagues = findViewById(R.id.btnAllLeagues)
        btnPremierLeague = findViewById(R.id.btnPremierLeague)
        btnLaLiga = findViewById(R.id.btnLaLiga)
        btnBundesliga = findViewById(R.id.btnBundesliga)
        btnSerieA = findViewById(R.id.btnSerieA)
        btnLigue1 = findViewById(R.id.btnLigue1)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        tvNoMatches = findViewById(R.id.tvNoMatches)
        tvCurrentDate = findViewById(R.id.tvCurrentDate)
    }

    private fun setupRecyclerView() {
        adapter = MatchAdapter()
        rvMatches.adapter = adapter
        rvMatches.layoutManager = LinearLayoutManager(this)
    }

    private fun displayCurrentDate() {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREAN)
        tvCurrentDate.text = dateFormat.format(Date())
    }

    private fun setupListeners() {
        // 새로고침
        btnRefresh.setOnClickListener {
            viewModel.loadTodayMatches()
        }

        // 전체 리그
        btnAllLeagues.setOnClickListener {
            viewModel.loadTodayMatches()
        }

        // 프리미어리그 - 코드로 전달!
        btnPremierLeague.setOnClickListener {
            viewModel.loadLeagueMatches("PL")
        }

        // 라리가 - 코드로 전달!
        btnLaLiga.setOnClickListener {
            viewModel.loadLeagueMatches("PD")
        }

        // 분데스리가 - 코드로 전달!
        btnBundesliga.setOnClickListener {
            viewModel.loadLeagueMatches("BL1")
        }

        // 세리에 A - 코드로 전달!
        btnSerieA.setOnClickListener {
            viewModel.loadLeagueMatches("SA")
        }

        // 리그 1 - 코드로 전달!
        btnLigue1.setOnClickListener {
            viewModel.loadLeagueMatches("FL1")
        }
    }

    private fun observeViewModel() {
        viewModel.matches.observe(this) { matches ->
            if (matches.isEmpty()) {
                rvMatches.visibility = View.GONE
                tvNoMatches.visibility = View.VISIBLE
            } else {
                rvMatches.visibility = View.VISIBLE
                tvNoMatches.visibility = View.GONE
                adapter.submitList(matches)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnRefresh.isEnabled = !isLoading
            btnAllLeagues.isEnabled = !isLoading
            btnPremierLeague.isEnabled = !isLoading
            btnLaLiga.isEnabled = !isLoading
            btnBundesliga.isEnabled = !isLoading
            btnSerieA.isEnabled = !isLoading
            btnLigue1.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                tvError.visibility = View.VISIBLE
                tvError.text = error
            } else {
                tvError.visibility = View.GONE
            }
        }
    }
}