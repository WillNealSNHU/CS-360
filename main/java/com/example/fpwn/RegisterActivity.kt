package com.example.fpwn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerUsernameEditText: EditText
    private lateinit var registerPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButtonFromRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_register)
        registerUsernameEditText = findViewById(R.id.registerUsernameEditText)
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginButtonFromRegister = findViewById(R.id.loginButtonFromRegister)
        registerButton.setOnClickListener {
            val username = registerUsernameEditText.text.toString()
            val password = registerPasswordEditText.text.toString()

            GlobalScope.launch(Dispatchers.IO) {
                val userDao = AppDatabase.getDatabase(applicationContext).userDao()
                userDao.insertUser(User(username = username, password = password))
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
        loginButtonFromRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }


    }
}
