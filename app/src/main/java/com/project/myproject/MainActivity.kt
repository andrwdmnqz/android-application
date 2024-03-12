package com.project.myproject

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var settingPreference: SettingPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameField = findViewById<TextView>(R.id.name)
        val logoutButton = findViewById<Button>(R.id.logout_button)

        settingPreference = SettingPreference(this)

        val extras = intent.extras

        if (extras != null) {
            var name = extras.getString("email")
            if (name != null) {
                name = name.substringBefore('@')

                val splittedName = name.split('.')
                    .map { it.replaceFirstChar { char -> char.uppercaseChar() } }

                if (splittedName.size >= 2) {
                    val nameText = getString(R.string.name_placeholder,
                        "${splittedName[0]} ${splittedName[1]}")
                    nameField.text = nameText
                } else {
                    val nameText = getString(R.string.name_placeholder, "${splittedName[0]}")
                    nameField.text = nameText
                }
            }
        }

        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                settingPreference.clearData()

                val registerIntent = Intent(this@MainActivity, RegisterActivity::class.java)

                val profileActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity)
                startActivity(registerIntent, profileActivityOptions.toBundle())
                finish()
            }
        }
    }
}
