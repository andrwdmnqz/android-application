package com.project.myproject

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)

        email = sharedPreferences.getString("email_key", null)

        val nameField = findViewById<TextView>(R.id.name)
        val logoutButton = findViewById<Button>(R.id.logout_button)

        if (email != null) {
            val name = email!!.substringBefore('@')

            val splittedName = name.split('.')
                .map { it.replaceFirstChar { char -> char.uppercaseChar() } }

            val nameText = getString(R.string.name_placeholder,
                "${splittedName[0]} ${splittedName[1]}")

            nameField.text = nameText
        }

        logoutButton.setOnClickListener {
            val editor = sharedPreferences.edit()

            editor.clear()
            editor.apply()

            val registerIntent = Intent(this, RegisterActivity::class.java)

            val profileActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(registerIntent, profileActivityOptions.toBundle())
        }

//        val extras = intent.extras
//
//        if (extras != null) {
//            var name = extras.getString("email")
//            if (name != null) {
//                name = name.substring(0, name.indexOf('@'))
//
//                val splittedName = name.split('.')
//                    .map { it.replaceFirstChar { char -> char.uppercaseChar() } }
//
//                val nameText = getString(R.string.name_placeholder, "${splittedName[0]} ${splittedName[1]}")
//                nameField.text = nameText
//            }
//        }
    }
}
