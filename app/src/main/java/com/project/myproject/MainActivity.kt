package com.project.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameField = findViewById<TextView>(R.id.name)

        val extras = intent.extras
        if (extras != null) {
            var name = extras.getString("email")
            if (name != null) {
                name = name.substring(0, name.indexOf('@'))

                val splittedName = name.split('.')
                    .map { it.replaceFirstChar { char -> char.uppercaseChar() } }

                val nameText = getString(R.string.name_placeholder, "${splittedName[0]} ${splittedName[1]}")
                nameField.text = nameText
            }
        }
    }
}
