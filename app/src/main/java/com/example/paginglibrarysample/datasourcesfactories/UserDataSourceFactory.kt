package com.example.paginglibrarysample.datasourcesfactories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.example.paginglibrarysample.datasources.UserDataSource
import com.example.paginglibrarysample.models.User

class UserDataSourceFactory(private val context: Context) : DataSource.Factory<Int, User>() {

    val mutableLiveData = MutableLiveData<UserDataSource>()

    override fun create(): DataSource<Int, User> {
        val userDataSource = UserDataSource(context)
        mutableLiveData.postValue(userDataSource)
        return userDataSource
    }

}