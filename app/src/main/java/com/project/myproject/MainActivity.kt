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

        initializeLogoutButton()
    }

    private fun parseName() {
        val extras = intent.extras
        val nameField = viewBinding.name

        if (extras != null) {
            var name = extras.getString(Constants.EMAIL_KEY)
            if (name != null) {
                name = name.substringBefore(Constants.CUT_CHAR)

                val splittedName = name.split(Constants.SPLIT_CHAR)
                    .map { it.replaceFirstChar { char -> char.uppercaseChar() } }

                if (splittedName.size >= 2) {
                    val nameText = getString(
                        R.string.name_placeholder,
                        "${splittedName[0]} ${splittedName[1]}"
                    )
                    nameField.text = nameText
                } else {
                    val nameText = getString(R.string.name_placeholder, splittedName[0])
                    nameField.text = nameText
                }
            }
        }
    }

    private fun initializeLogoutButton() {
        val logoutButton = viewBinding.logoutButton
        logoutButton.setOnClickListener {
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
