package com.project.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class MyContacts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_contacts_activity)

//        val toolbar = findViewById<Toolbar>(R.id.my_contacts_toolbar)
//        toolbar.setNavigationIcon(R.drawable.arrow_back_24px)
    }
}