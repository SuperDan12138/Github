package com.github.example.http.service

import com.github.example.entity.IssueReqParam
import com.github.example.entity.Repo
import com.github.example.entity.SearchResult
import com.github.example.entity.UserInfo
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    @GET("user")
    suspend fun fetchUserOwner(@Header("Authorization") authorization: String): Response<UserInfo>

//    @GET("events")
//    suspend fun getPublicEvents(): List<ReceivedEvent>


    @GET("users/{username}/repos?")
    suspend fun queryRepos(@Path("username") username: String,
                           @Query("page") pageIndex: Int,
                           @Query("per_page") perPage: Int,
                           @Query("sort") sort: String): List<Repo>

    @GET("search/repositories")
    suspend fun search(@Query("q") q: String,
                       @Query("page") pageIndex: Int,
                       @Query("per_page") perPage: Int,
                       @Query("sort") sort: String): SearchResult


    @POST("/repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Body body: IssueReqParam,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<Repo>
}
