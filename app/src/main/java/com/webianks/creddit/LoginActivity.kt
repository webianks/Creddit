package com.webianks.creddit

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.cloudant.client.api.ClientBuilder
import com.cloudant.client.api.model.Params

/**
 * Created by R Ankit on 24-09-2017.
 */

class LoginActivity : AppCompatActivity(){

    lateinit var mPinET: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_layout)

        initViews()

    }

    private fun initViews() {

        mPinET = findViewById(R.id.mPinEditText) as TextInputEditText;

        findViewById(R.id.signup).setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        mPinET.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s?.length == 6){
                    performLogin(s)
                }
            }
        })
    }


    private fun performLogin(s: CharSequence) {
        val mpin: String = s.toString()

        CloudantClientAsync().execute(mpin)
    }

    internal inner class CloudantClientAsync : AsyncTask<String, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg strings: String): Boolean? {

            val sent_pin = strings[0]

            val client = ClientBuilder.account(getString(R.string.cloudantUsername1))
                    .username(getString(R.string.cloudantUsername1))
                    .password(getString(R.string.cloudantPassword1))
                    .build()

            val creddit_db = client.database("creddit_db", false)

            val params = Params()
            params.addParam("m_pin",sent_pin)
            val user = creddit_db.find(User::class.java, "7c9271065ccd4911cc197d923a691b45", params)

            return user.m_pin == sent_pin
        }

        override fun onPostExecute(value: Boolean?) {
            super.onPostExecute(value)

            hideDialog()

            if (value?.equals(true)!!){
                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this@LoginActivity,"Wrong mPIN",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var progressDialog: ProgressDialog? = null

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage(getString(R.string.please_wait))
        progressDialog?.setCancelable(false)
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog?.isIndeterminate = true
        progressDialog?.show()
    }

    private fun hideDialog(){
        progressDialog?.dismiss()
    }

}