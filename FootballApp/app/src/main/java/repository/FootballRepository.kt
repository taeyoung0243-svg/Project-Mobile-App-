package com.example.footballapp.data.repository

import android.util.Log
import com.example.footballapp.data.api.RetrofitInstance
import com.example.footballapp.data.model.Match
import java.text.SimpleDateFormat
import java.util.*

class FootballRepository {

    private val api = RetrofitInstance.api

    // 여기에 발급받은 API 키 입력!
    private val apiKey = "7ba94fce0260404db340597f421642bf"

    suspend fun getTodayMatches(): Result<List<Match>> {
        // 여러 주요 리그의 경기 가져오기
        return getMultipleLeaguesMatches()
    }

    suspend fun getMatchesByDate(date: String): Result<List<Match>> {
        return try {
            // 날짜 범위로 검색 (해당 날짜만)
            val response = api.getMatchesByDateRange(
                apiKey = apiKey,
                dateFrom = date,
                dateTo = date
            )

            if (response.isSuccessful && response.body() != null) {
                val matches = response.body()!!.matches
                Log.d("Repository", "$date: ${matches.size}개 경기")

                // 날짜 역순 정렬 (최신이 위로)
                val sorted = matches.sortedByDescending { it.date }
                Result.success(sorted)
            } else {
                Log.e("Repository", "API 호출 실패: ${response.code()}")
                Result.failure(Exception("API 호출 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("Repository", "오류: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 여러 주요 리그 경기 가져오기
    suspend fun getMultipleLeaguesMatches(): Result<List<Match>> {
        return try {
            Log.d("Repository", "주요 리그 경기 로딩 시작")

            val allMatches = mutableListOf<Match>()

            // 현재 날짜
            val now = Calendar.getInstance()
            val currentDate = now.time

            // 7일 전
            val sevenDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -7)
            }.time

            // 7일 후
            val sevenDaysLater = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 7)
            }.time

            // 주요 리그 코드 (Football-Data.org)
            val competitions = mapOf(
                "PL" to "프리미어리그",
                "PD" to "라리가",
                "BL1" to "분데스리그",
                "SA" to "세리에 A",
                "FL1" to "리그 1",
                "CL" to "챔피언스리그"
            )

            for ((code, name) in competitions) {
                try {
                    Log.d("Repository", "$name ($code) 로딩 중...")

                    val response = api.getCompetitionMatches(
                        apiKey = apiKey,
                        competitionId = code
                    )

                    if (response.isSuccessful && response.body() != null) {
                        val matches = response.body()!!.matches

                        // 날짜 필터링: 7일 전 ~ 7일 후 경기만
                        val filteredMatches = matches.filter { match ->
                            try {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                                val matchDate = inputFormat.parse(match.date)

                                matchDate != null &&
                                        matchDate.after(sevenDaysAgo) &&
                                        matchDate.before(sevenDaysLater)
                            } catch (e: Exception) {
                                false
                            }
                        }

                        allMatches.addAll(filteredMatches.take(10))

                        Log.d("Repository", "$name: 전체 ${matches.size}개 중 최근 ${filteredMatches.size}개")
                    } else {
                        Log.e("Repository", "$name 실패: ${response.code()}")
                    }

                    // API 제한 고려 (10 requests/min)
                    kotlinx.coroutines.delay(1000)

                } catch (e: Exception) {
                    Log.e("Repository", "$name 오류: ${e.message}")
                }
            }

            Log.d("Repository", "총 경기: ${allMatches.size}개")

            // 리그별 통계
            val leagueStats = allMatches.groupBy { it.competition.name }
            leagueStats.forEach { (league, matches) ->
                Log.d("Repository", "  $league: ${matches.size}개")
            }

            if (allMatches.isEmpty()) {
                Result.failure(Exception("최근 경기가 없습니다"))
            } else {
                // 날짜 역순 정렬 (최신이 위로)
                val sorted = allMatches.sortedByDescending { it.date }
                Result.success(sorted)
            }
        } catch (e: Exception) {
            Log.e("Repository", "전체 오류: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 특정 리그 경기 (최근 2주)
    suspend fun getLeagueMatches(leagueCode: String): Result<List<Match>> {
        return try {
            Log.d("Repository", "리그 $leagueCode 로딩")

            val response = api.getCompetitionMatches(
                apiKey = apiKey,
                competitionId = leagueCode
            )

            if (response.isSuccessful && response.body() != null) {
                val allMatches = response.body()!!.matches

                // 최근 2주 필터링
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")

                val now = Calendar.getInstance().time
                val twoWeeksAgo = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -7)
                }.time
                val twoWeeksLater = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 7)
                }.time

                val filtered = allMatches.filter { match ->
                    try {
                        val matchDate = dateFormat.parse(match.date)
                        matchDate != null &&
                                matchDate.after(twoWeeksAgo) &&
                                matchDate.before(twoWeeksLater)
                    } catch (e: Exception) {
                        false
                    }
                }

                Log.d("Repository", "리그 $leagueCode: ${filtered.size}개 경기")

                // 날짜 역순 정렬 (최신이 위로)
                val sorted = filtered.sortedByDescending { it.date }
                Result.success(sorted)
            } else {
                Log.e("Repository", "리그 $leagueCode 실패: ${response.code()}")
                Result.failure(Exception("API 호출 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("Repository", "리그 $leagueCode 오류: ${e.message}", e)
            Result.failure(e)
        }
    }
}