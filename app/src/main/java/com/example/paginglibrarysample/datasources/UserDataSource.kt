package com.example.paginglibrarysample.datasources

import android.content.Context
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.paginglibrarysample.api.ApiClient
import com.example.paginglibrarysample.models.User
import com.example.paginglibrarysample.models.UserResponse
import com.example.paginglibrarysample.utils.Utility
import com.example.paginglibrarysample.utils.Utility.isInternetAvailable
import com.example.paginglibrarysample.utils.Utility.showProgressBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDataSource(private val context: Context) : PageKeyedDataSource<Int, User>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, User>) {
        if (context.isInternetAvailable()) {
            getUsers(callback)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        if (context.isInternetAvailable()) {
            val nextPageNo = params.key + 1
            getMoreUsers(params.key, nextPageNo, callback)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        if (context.isInternetAvailable()) {
            val previousPageNo = if (params.key > 1) params.key - 1 else 0
            getMoreUsers(params.key, previousPageNo, callback)
        }
    }

    private fun getUsers(callback: LoadInitialCallback<Int, User>) {

        context.showProgressBar()

        ApiClient.apiService.getUsers(1).enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Utility.hideProgressBar()
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                Utility.hideProgressBar()
                val userResponse = response.body()
                val listUsers = userResponse?.listUsers
                listUsers?.let { callback.onResult(it, null, 2) }
            }

        })

    }

    private fun getMoreUsers(pageNo: Int, previousOrNextPageNo: Int, callback: LoadCallback<Int, User>) {

        ApiClient.apiService.getUsers(pageNo).enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val userResponse = response.body()
                val listUsers = userResponse?.listUsers
                listUsers?.let { callback.onResult(it, previousOrNextPageNo) }
            }

        })

    }

}