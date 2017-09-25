package com.webianks.creddit

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.cloudant.client.api.ClientBuilder

/**
 * Created by R Ankit on 24-09-2017.
 */

class LoginActivity : AppCompatActivity(){

    lateinit var mPinET: TextInputEditText
    lateinit var phoneNumberET: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_layout)

        initViews()

    }

    private fun initViews() {

        mPinET = findViewById(R.id.mPinEditText) as TextInputEditText;
        phoneNumberET  = findViewById(R.id.phoneEditText) as TextInputEditText

        findViewById(R.id.signup).setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        findViewById(R.id.submit).setOnClickListener {

            if(mPinET.text.length == 6 && phoneNumberET.text.length == 10)
                   performLogin(mPinET.text)
        }

        mPinET.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s?.length == 6){
                     if(phoneNumberET.text.length == 10)
                        performLogin(s)
                     else
                         Toast.makeText(this@LoginActivity,"Please enter 10 digit phone number",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun performLogin(s: CharSequence) {
        val mpin: String = s.toString()
        val phone_number = phoneNumberET.text.toString()

        CloudantClientAsync().execute(phone_number,mpin)
    }


    internal inner class CloudantClientAsync : AsyncTask<String, Void, User>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg strings: String): User? {

            val sent_number = strings[0]
            val sent_pin = strings[1]

            val client = ClientBuilder.account(getString(R.string.cloudantUsername1))
                    .username(getString(R.string.cloudantUsername1))
                    .password(getString(R.string.cloudantPassword1))
                    .build()

            val users_db = client.database("users", false)
            try {
                val user = users_db.find(User::class.java, sent_number + "@#" + sent_pin)
                return user
            }catch (e: Exception){
                return null
            }
        }

        override fun onPostExecute(value: User?) {
            super.onPostExecute(value)

            hideDialog()

            if (value != null){
                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                intent.putExtra("user_document_key",value.phone_number + "@#" + value.m_pin)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this@LoginActivity,"Can't login",Toast.LENGTH_SHORT).show()
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