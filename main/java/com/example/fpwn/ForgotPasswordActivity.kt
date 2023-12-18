package com.example.fpwn

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var userDao: UserDao
    private lateinit var forgotPasswordUsernameEditText: EditText
    private lateinit var forgotPasswordEditText: EditText
    private lateinit var resetPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_forgot_password)


        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "user-database"
        ).build()

        userDao = db.userDao()


        forgotPasswordUsernameEditText = findViewById(R.id.forgotPasswordUsernameEditText)
        forgotPasswordEditText = findViewById(R.id.forgotPasswordEditText)
        resetPasswordButton = findViewById(R.id.forgotPasswordButton)


        resetPasswordButton.setOnClickListener {
            val username = forgotPasswordUsernameEditText.text.toString()
            val newPassword = forgotPasswordEditText.text.toString()

            if (username.isNotEmpty() && newPassword.isNotEmpty()) {

                updatePassword(username, newPassword)


                runOnUiThread {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Password updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }


                finish()
            } else {

                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Username and new password cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updatePassword(username: String, newPassword: String) {

        CoroutineScope(Dispatchers.IO).launch {
            userDao.updatePassword(username, newPassword)
        }
    }
}