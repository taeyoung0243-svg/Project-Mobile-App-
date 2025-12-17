package com.example.footballapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.footballapp.R
import com.example.footballapp.data.model.Match
import java.text.SimpleDateFormat
import java.util.*

class MatchAdapter : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    private var matches = listOf<Match>()

    // 데이터 업데이트 함수
    fun submitList(newMatches: List<Match>) {
        matches = newMatches
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(matches[position])
    }

    override fun getItemCount(): Int = matches.size

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvLeagueName: TextView = itemView.findViewById(R.id.tvLeagueName)
        private val tvMatchDate: TextView = itemView.findViewById(R.id.tvMatchDate)
        private val ivHomeTeamLogo: ImageView = itemView.findViewById(R.id.ivHomeTeamLogo)
        private val tvHomeTeamName: TextView = itemView.findViewById(R.id.tvHomeTeamName)
        private val tvHomeScore: TextView = itemView.findViewById(R.id.tvHomeScore)
        private val ivAwayTeamLogo: ImageView = itemView.findViewById(R.id.ivAwayTeamLogo)
        private val tvAwayTeamName: TextView = itemView.findViewById(R.id.tvAwayTeamName)
        private val tvAwayScore: TextView = itemView.findViewById(R.id.tvAwayScore)
        private val tvMatchStatus: TextView = itemView.findViewById(R.id.tvMatchStatus)

        fun bind(match: Match) {
            // 리그 이름
            tvLeagueName.text = match.competition.name

            // 날짜 포맷팅
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date = inputFormat.parse(match.date)
                tvMatchDate.text = date?.let { outputFormat.format(it) } ?: match.date
            } catch (e: Exception) {
                tvMatchDate.text = match.date
            }

            // 팀 이름
            tvHomeTeamName.text = match.homeTeam.name
            tvAwayTeamName.text = match.awayTeam.name

            // 팀 로고
            if (!match.homeTeam.crest.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(match.homeTeam.crest)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ivHomeTeamLogo)
            } else {
                ivHomeTeamLogo.setImageResource(R.mipmap.ic_launcher)
            }

            if (!match.awayTeam.crest.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(match.awayTeam.crest)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ivAwayTeamLogo)
            } else {
                ivAwayTeamLogo.setImageResource(R.mipmap.ic_launcher)
            }

            // 스코어
            val homeScore = match.score.fullTime?.home
            val awayScore = match.score.fullTime?.away

            if (homeScore != null && awayScore != null) {
                tvHomeScore.text = homeScore.toString()
                tvAwayScore.text = awayScore.toString()
            } else {
                tvHomeScore.text = "-"
                tvAwayScore.text = "-"
            }

            // 경기 상태
            tvMatchStatus.text = when (match.status) {
                "SCHEDULED" -> "예정"
                "TIMED" -> "시작 전"
                "IN_PLAY" -> "진행중"
                "PAUSED" -> "하프타임"
                "FINISHED" -> "경기종료"
                "POSTPONED" -> "연기"
                "CANCELLED" -> "취소"
                "SUSPENDED" -> "중단"
                else -> match.status
            }
        }
    }
}