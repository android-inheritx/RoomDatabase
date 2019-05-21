package com.example.roomdatabasedemo.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.roomdatabasedemo.R
import com.example.roomdatabasedemo.database.User
import com.example.roomdatabasedemo.database.UserViewModel
import com.example.roomdatabasedemo.utils.AppConstants
import com.example.roomdatabasedemo.utils.hideSoftKeyboard
import com.example.roomdatabasedemo.utils.isEmail
import com.example.roomdatabasedemo.utils.toastNow
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_update_user.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.Dispatcher
import kotlin.coroutines.CoroutineContext

/**
 * Add user if user come by clicking add new user fab
 * Update user if user come by clicking on user details in listing
 */
class AddUpdateUserActivity : AppCompatActivity() {
    var name = ""
    var email = ""
    var phoneNumber = ""
    var mAge = 0
    var isNewUser = false  // if true then consider the add feature, else edit feature need to implement
    lateinit var userDetails: User
    lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_user)

        init()
        setListener()
        setIntentData()
    }

    private fun init() {
        tvToolbarTitle.text = "Add User"
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    /**
     * set data come from previous screen
     */
    private fun setIntentData() {
        val extras = intent.extras
        if (extras != null) {
            isNewUser = extras.getBoolean(AppConstants.EXTRA_IS_FROM_NEW_CLICK)

            if (!isNewUser) {//update call
                //set old details in editText
                userDetails =
                    Gson().fromJson<User>(extras.getString(AppConstants.EXTRA_USER_DETAILS), User::class.java)

                etName.setText(userDetails.name)
                etEmail.setText(userDetails.email)
                etPhoneNumber.setText(userDetails.phoneNumber)
                etAge.setText(userDetails.age.toString())
            }
        }
    }

    /**
     * set all listener
     */
    private fun setListener() {
        ivBack.setOnClickListener {
            hideSoftKeyboard()
            finish()
        }

        btnSubmit.setOnClickListener {
            if (isValid()) {
                if (!isNewUser) {
                    val user = User(
                        userId = userDetails.userId,
                        name = name,
                        email = email,
                        phoneNumber = phoneNumber,
                        createdAt = userDetails.createdAt,
                        age = mAge
                    )

                    insertOrUpdateUser(user)

                } else {
                    val user = User(
                        name = name,
                        email = email,
                        phoneNumber = phoneNumber,
                        createdAt = System.currentTimeMillis(),
                        age = mAge
                    )
                    insertOrUpdateUser(user)
                }


            }
        }
    }

    /**
     * insert or update user
     */
    private fun insertOrUpdateUser(user: User) {

        val thread = object : Thread() {
            override fun run() {
                try {
                    if (isNewUser) {//new user
                        userViewModel.insertUser(user)//add user
                    } else {//old user
                        userViewModel.updateUser(user)//update user
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        thread.start()

        hideSoftKeyboard()
        finish()
    }

    /**
     * check if all inputs are valid or not
     * @return true if all inputs are valid else false
     */
    private fun isValid(): Boolean {
        name = etName.text.toString().trim()
        email = etEmail.text.toString().trim()
        phoneNumber = etPhoneNumber.text.toString().trim()
        val age = etAge.text.toString().trim()

        if (name.isEmpty()) {
            toastNow(getString(R.string.msg_empty_name))
            return false
        }

        if (email.isEmpty()) {
            toastNow(getString(R.string.msg_empty_email))
            return false
        }

        if (!email.isEmail()) {
            toastNow(getString(R.string.msg_invalid_email))
            return false
        }

        if (phoneNumber.isEmpty()) {
            toastNow(getString(R.string.msg_empty_phone))
            return false
        }

        if (age.isEmpty()) {
            toastNow(getString(R.string.msg_empty_age))
            return false
        } else if (age.toInt() == 0) {
            toastNow(getString(R.string.msg_invalid_age))
            return false
        } else {
            mAge = etAge.text.toString().trim().toInt()
        }

        return true
    }
}
