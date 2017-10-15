package com.example.my.mynewapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.my.mynewapp.data.AuthCredentials
import com.example.my.mynewapp.data.AuthResponse
import com.example.my.mynewapp.data.WhoAmIResponse
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import java.io.BufferedInputStream
import java.io.DataOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = "Login"

        loginButton.setOnClickListener {
            // TODO: Pull this functionality out into appropriate classes and functions
            doAsync {
                val authUrl = URL("https://limitless-dusk-74218.herokuapp.com/auth")
                val whoUrl = URL("https://limitless-dusk-74218.herokuapp.com/api/whoami")

                val gson = Gson()

                val username = usernameBox.text.toString()
                val password = passwordBox.text.toString()

                val credentials = AuthCredentials(username, password)
                val credentialsJson = gson.toJson(credentials)

                val authClient = authUrl.openConnection() as HttpsURLConnection
                val tokenJson : String;
                try {
                    authClient.apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json");
                        setRequestProperty("Accept","*/*");
                        setDoOutput(true);
                        setDoInput(true);
                    }

                    Log.i("JSON", credentialsJson)

                    val outputStream = DataOutputStream(authClient.outputStream)
                    outputStream.writeBytes(credentialsJson)
                    outputStream.flush()
                    outputStream.close()

                    val responseCode = authClient.responseCode
                    Log.i("STATUS", responseCode.toString());
                    Log.i("MSG" , authClient.getResponseMessage());

                    val builder = StringBuilder()

                    val inputStream = when(responseCode) {
                        200 -> BufferedInputStream(authClient.inputStream)
                        else -> BufferedInputStream(authClient.errorStream)
                    }

                    inputStream.bufferedReader()
                            .readLines()
                            .forEach { builder.appendln(it) }
                    tokenJson = builder.toString()

                    Log.i("TOK", tokenJson)
                } finally {
                    authClient.disconnect();
                }

                val authToken = gson.fromJson<AuthResponse>(tokenJson, AuthResponse::class.java)

                val whoClient = whoUrl.openConnection() as HttpsURLConnection
                val whoResponse : String
                try {
                    whoClient.apply {
                        setRequestProperty("Authorization", "JWT ${authToken.access_token}")
                    }
                    val inputStream = BufferedInputStream(whoClient.inputStream)
                    val builder = StringBuilder()
                    inputStream.bufferedReader()
                            .readLines()
                            .forEach { builder.appendln(it) }
                    whoResponse = builder.toString()
                } finally {
                    whoClient.disconnect()
                }

                val who = gson.fromJson<WhoAmIResponse>(whoResponse, WhoAmIResponse::class.java)
                val res = "You logged in as ${who.username}!"

                uiThread {
                    startActivity<DisplayMessageActivity>(EXTRA_MESSAGE to res)
                }
            }
        }
    }
}
