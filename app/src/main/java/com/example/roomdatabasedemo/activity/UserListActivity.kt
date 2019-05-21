package com.example.roomdatabasedemo.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.example.roomdatabasedemo.*
import com.example.roomdatabasedemo.database.User
import com.example.roomdatabasedemo.database.UserViewModel
import com.example.roomdatabasedemo.databinding.ItemUserBinding
import com.example.roomdatabasedemo.utils.*
import com.github.nitrico.lastadapter.LastAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_user_list.*
import kotlinx.android.synthetic.main.common_toolbar.*
import java.util.*


/**
 * show all users
 * can search user by name
 * */

class UserListActivity : AppCompatActivity() {
    lateinit var userViewModel: UserViewModel
    val arrUser = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        init()
        setListener()
        getUsers()
    }

    private fun init() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        rvUser.layoutManager = LinearLayoutManager(this)
        ivBack.hide()
        tvToolbarTitle.text = "Users"
    }


    private fun setListener() {

        /**
         * To search user from list
         * */
        etSearchText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchKeyword = etSearchText.text.toString().trim()
                if (!searchKeyword.equals("", ignoreCase = true)) {
                    if (searchKeyword.isEmpty()) {
                        toastNow("Please enter user name")
                    } else {
                        searchUserByName(searchKeyword)
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })


        ivSearch.setOnClickListener {
            toolbar.hide()
            containerSearch.show()
            ivSearch.hide()
            etSearchText.requestFocus()
            showSoftKeyboard()
        }

        ivClose.setOnClickListener {
            toolbar.show()
            containerSearch.hide()
            ivSearch.show()

            hideSoftKeyboard()

            getUsers()
        }

        fab.setOnClickListener {
            val intent = Intent(this@UserListActivity, AddUpdateUserActivity::class.java)
            intent.putExtra(AppConstants.EXTRA_IS_FROM_NEW_CLICK, true)
            startActivity(intent)
        }
    }

    /**
     * get all users from local database
     */
    private fun getUsers() {
        userViewModel.getAllUser()?.observe(this, Observer {
            arrUser.clear()
            arrUser.addAll(it.orEmpty())

            Log.d("userList", arrUser.toString())

            setDataInRecyclerView(arrUser)
        })
    }

    /**
     * set data in recycler view
     * @param arrUser list of users
     */
    private fun setDataInRecyclerView(arrUser: ArrayList<User>) {
        LastAdapter(arrUser, BR.user)
            .map<User, ItemUserBinding>(R.layout.item_user) {
                onBind {
                    val binding = it.binding
                    val currentItem = binding.user

                    binding.ivDelete.setOnClickListener {
                        val alertDialog = AlertDialog.Builder(this@UserListActivity)
                        alertDialog.setTitle(getString(R.string.app_name))
                        alertDialog.setMessage("Are you sure you want to delete user?")
                        alertDialog.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                            val thread = object : Thread() {
                                override fun run() {
                                    try {
                                        userViewModel.deleteUserById(currentItem?.userId!!)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            thread.start()
                        })
                        alertDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

                        })

                        alertDialog.setCancelable(false)
                        alertDialog.create().show()

                    }

                    binding.containerMain.setOnClickListener {
                        val userDetails = Gson().toJson(currentItem)

                        val intent = Intent(this@UserListActivity, AddUpdateUserActivity::class.java)
                        intent.putExtra(AppConstants.EXTRA_IS_FROM_NEW_CLICK, false)
                        intent.putExtra(AppConstants.EXTRA_USER_DETAILS, userDetails)
                        startActivity(intent)
                    }
                }
            }
            .into(rvUser)
    }


    /**
     * search user by name
     * @param searchText text to be search
     */
    private fun searchUserByName(searchText: String) {
        val userByName = userViewModel.getUserByName(searchText)

        userByName?.observe(this, Observer {
            arrUser.clear()
            arrUser.addAll(it.orEmpty())
            Log.d("userList", arrUser.toString())
            rvUser.adapter!!.notifyDataSetChanged()
        })

        //todo uncomment below code to understand " WHERE email IN (:emails) " query
//        val userByEmails = userViewModel.getUsersFromEmail(listOf("vidhi@inheritx.com", "kajal@inheritx.com"))
//
//        userByEmails?.observe(this, Observer {
//            arrUser.clear()
//            arrUser.addAll(it.orEmpty())
//            Log.d("userList", arrUser.toString())
//            rvUser.adapter!!.notifyDataSetChanged()
//        })

        etSearchText.setText("")
    }
}
