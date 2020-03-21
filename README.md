# Paging-Library-Sample
This is a sample app of jetpack paging library in kotlin.


## Add the below dependencies in your app level build.gradle file
```kotlin
implementation "android.arch.paging:runtime:1.0.1"
implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0'
annotationProcessor "android.arch.lifecycle:compiler:1.1.1"
```

## Create a Data Source class for page keyed content
```kotlin
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
```

## Create a DataSource.Factory class for DataSource
```kotlin
class UserDataSourceFactory(private val context: Context) : DataSource.Factory<Int, User>() {

    val mutableLiveData = MutableLiveData<UserDataSource>()

    override fun create(): DataSource<Int, User> {
        val userDataSource = UserDataSource(context)
        mutableLiveData.postValue(userDataSource)
        return userDataSource
    }

}
```

## Create a ViewModel class to handle data
```kotlin
class UserViewModel(private val context: Context) : ViewModel() {

    private var listUsers : LiveData<PagedList<User>> = MutableLiveData<PagedList<User>>()
    private var mutableLiveData = MutableLiveData<UserDataSource>()

    init {
        val factory : UserDataSourceFactory by lazy {
            UserDataSourceFactory(context)
        }
        mutableLiveData = factory.mutableLiveData

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(6)
            .build()

        listUsers = LivePagedListBuilder(factory, config)
            .build()

    }

    fun getData() : LiveData<PagedList<User>>{
        return listUsers
    }
    
}
```

## Create a ViewModelFactory class to pass custom arguments with ViewModel
```kotlin
class UserViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(context) as T
    }

}
```

## Create a PagedListAdapter class to display data from PagedList
```kotlin
class UsersAdapter(private val context: Context) : PagedListAdapter<User,UsersAdapter.MyViewHolder>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding: UserRowBinding = DataBindingUtil.inflate(inflater, R.layout.user_row,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemBinding.user = getItem(position)
    }

    class MyViewHolder(val itemBinding: UserRowBinding) : RecyclerView.ViewHolder(itemBinding.root){

        private var binding : UserRowBinding? = null

        init {
            this.binding = itemBinding
        }

    }
    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                newItem == oldItem
        }
    }

}
```

## Bind data with RecyclerView
```kotlin
val userViewModel = ViewModelProvider(this,UserViewModelFactory(this)).get(UserViewModel::class.java)
userViewModel.getData().observe(this, object : Observer<PagedList<User>>{
            override fun onChanged(t: PagedList<User>?) {
                adapter.submitList(t)
            }
        })
```        
