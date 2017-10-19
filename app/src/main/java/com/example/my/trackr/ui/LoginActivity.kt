package com.example.my.trackr.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.data.*
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    @Inject lateinit var webApi: WebApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (application as MainApplication).component.inject(this)

        loginButton.setOnClickListener {
            // TODO: Actually save the user's credentials and do things with them
            doAsync {
                val username = usernameBox.text.toString()
                val password = passwordBox.text.toString()

                val credentials = AuthCredentials(username, password)

                val authorization = webApi.authenticate(credentials)

                val message = when (authorization.access_token) {
                    null -> authorization.description!! // Show error message when authorization fails
                    else -> {
                        val who = webApi.whoAmI(authorization)
                        "You logged in as '${who.username}'!"
                    }
                }

                uiThread {
                    startActivity<DisplayMessageActivity>(EXTRA_MESSAGE to message)
                }
            }
        }
    }
}
