package com.example.footballapp.data.api

import com.example.footballapp.data.model.MatchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FootballApiService {

    // 특정 대회의 경기 가져오기
    @GET("competitions/{competitionId}/matches")
    suspend fun getCompetitionMatches(
        @Header("X-Auth-Token") apiKey: String,
        @Path("competitionId") competitionId: String,
        @Query("status") status: String? = null
    ): Response<MatchResponse>

    // 날짜 범위로 경기 가져오기
    @GET("matches")
    suspend fun getMatchesByDateRange(
        @Header("X-Auth-Token") apiKey: String,
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String
    ): Response<MatchResponse>
}