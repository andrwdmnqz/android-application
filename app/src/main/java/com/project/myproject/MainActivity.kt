package com.project.myproject

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.project.myproject.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var settingPreference: SettingPreference
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        settingPreference = SettingPreference(this)

        parseName()

        initializeLogoutButtonListeners()
    }

    private fun parseName() {
        val extras = intent.extras
        val nameField = viewBinding.name

        if (extras != null) {
            var name = extras.getString(Constants.EMAIL_KEY)
            if (name != null) {
                name = name.substringBefore('@')

                val splittedName = name.split('.')
                    .map { it.replaceFirstChar { char -> char.uppercaseChar() } }

                val nameText: String

                if (splittedName.size > 1) {
                    nameText = "${splittedName[0]} ${splittedName[1]}"
                } else {
                    nameText = getString(R.string.name_placeholder, splittedName[0])
                }
                nameField.text = nameText
            }
        }
    }

    private fun initializeLogoutButtonListeners() {

        viewBinding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                settingPreference.clearData()

                val registerIntent = Intent(this@MainActivity, RegisterActivity::class.java)

                val profileActivityOptions =
                    ActivityOptions.makeSceneTransitionAnimation(this@MainActivity)
                startActivity(registerIntent, profileActivityOptions.toBundle())
                finish()
            }
        }
    }
}
