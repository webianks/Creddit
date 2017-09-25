package com.webianks.creddit

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.cloudant.client.api.ClientBuilder

/**
 * Created by ramankit on 25/9/17.
 */
class PersonalInfoActivity : AppCompatActivity(){

    private lateinit var nameValue: EditText
    private lateinit var addressValue: EditText
    private lateinit var emailValue: EditText
    private lateinit var submit: Button
    private lateinit var progress_bar: ProgressBar
    private var previous_rev: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_info)

        initViews()
    }

    private var document_key: String? = null

    private fun initViews() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if(supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progress_bar = findViewById(R.id.progress_bar) as ProgressBar
        nameValue = findViewById(R.id.name_value) as EditText
        addressValue = findViewById(R.id.address_value) as EditText
        emailValue = findViewById(R.id.email_value) as EditText

        submit = findViewById(R.id.submit) as Button

        submit.setOnClickListener{

            val name = nameValue.text.toString()
            val address = addressValue.text.toString()
            val email = emailValue.text.toString()

            if (name.isNotEmpty()){
                if (address.isNotEmpty() && email.isNotEmpty()){
                        updateInfo(name,address,email)
                    }else
                        showError("Please provide all inputs")
            }else
                showError("Name can not be empty")
        }

        document_key = intent.getStringExtra("document_key")
        loadInfo()

    }

    private fun loadInfo() {
        progress_bar.visibility = View.VISIBLE
        CloudantClientAsyncRead().execute(document_key)
    }

    private fun updateInfo(name: String, address: String, email: String) {

        progress_bar.visibility = View.VISIBLE
        CloudantClientAsyncWrite().execute(document_key,name,email,address)
    }

    internal inner class CloudantClientAsyncWrite : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg strings: String): Boolean {

            val name: String = strings[1]
            val email: String = strings[2]
            val address: String = strings[3]

            val doc_key: String = strings[0]+"_personal_"

            val personalDataNew = PersonalData(_id = doc_key,name = name,email = email,address = address,_rev = previous_rev)

            val client = ClientBuilder.account(getString(R.string.cloudantUsername1))
                    .username(getString(R.string.cloudantUsername1))
                    .password(getString(R.string.cloudantPassword1))
                    .build()


            val personal_db = client.database("personal_info", false)
            try {
                personal_db.update(personalDataNew)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

        }

        override fun onPostExecute(value: Boolean) {
            super.onPostExecute(value)

            progress_bar.visibility = View.GONE
            if (value){
                showError("Updated")
            }
        }
    }


    internal inner class CloudantClientAsyncRead : AsyncTask<String, Void, PersonalData>() {

        override fun doInBackground(vararg strings: String): PersonalData? {

            val doucument_key: String = strings[0]+"_personal_"

            val client = ClientBuilder.account(getString(R.string.cloudantUsername1))
                    .username(getString(R.string.cloudantUsername1))
                    .password(getString(R.string.cloudantPassword1))
                    .build()


            val personal_db = client.database("personal_info", false)
            try {
                val personal_data: PersonalData = personal_db.find(PersonalData::class.java, doucument_key)
                return personal_data
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(value: PersonalData?) {
            super.onPostExecute(value)

            progress_bar.visibility = View.GONE

            if (value != null){
                nameValue.setText(value.name)
                addressValue.setText(value.address)
                emailValue.setText(value.email)
                previous_rev = value._rev
            }
        }
    }

    private fun showError(s: String) {
        Toast.makeText(this@PersonalInfoActivity,s,Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}