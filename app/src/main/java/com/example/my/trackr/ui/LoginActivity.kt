package com.example.my.trackr.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.data.*
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    @Inject lateinit var sessionManager: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (application as MainApplication).component.inject(this)

        loginButton.setOnClickListener {
            doAsync {
                val username = usernameBox.text.toString()
                val password = passwordBox.text.toString()

                var message: String
                try {
                    sessionManager.attemptLogin(username, password)
                    message = "You logged in as ${sessionManager.username}!"
                } catch (e: RuntimeException) {
                    message = e.message!!
                }

                uiThread {
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()

                    if (sessionManager.hasActiveSession){
                        finish()
                    }
                }
            }
        }
    }
}
