package com.example.my.mynewapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.my.mynewapp.data.*
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*

class LoginActivity : AppCompatActivity() {
    private val webApi = WebApiService("https://limitless-dusk-74218.herokuapp.com/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
