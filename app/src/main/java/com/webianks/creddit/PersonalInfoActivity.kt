package com.webianks.creddit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

/**
 * Created by ramankit on 25/9/17.
 */
class PersonalInfoActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_info)

        initViews()
    }

    private fun initViews() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if(supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}