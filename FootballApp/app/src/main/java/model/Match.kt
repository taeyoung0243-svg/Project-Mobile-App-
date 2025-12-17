package com.example.footballapp.data.model

import com.google.gson.annotations.SerializedName

// Football-Data.org API 응답 구조
data class MatchResponse(
    @SerializedName("matches")
    val matches: List<Match>
)

data class Match(
    @SerializedName("id")
    val id: Int,

    @SerializedName("utcDate")
    val date: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("homeTeam")
    val homeTeam: Team,

    @SerializedName("awayTeam")
    val awayTeam: Team,

    @SerializedName("score")
    val score: Score,

    @SerializedName("competition")
    val competition: Competition
)

data class Team(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("shortName")
    val shortName: String?,

    @SerializedName("crest")
    val crest: String?
)

data class Score(
    @SerializedName("fullTime")
    val fullTime: ScoreDetail?
)

data class ScoreDetail(
    @SerializedName("home")
    val home: Int?,

    @SerializedName("away")
    val away: Int?
)

data class Competition(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("emblem")
    val emblem: String?
)