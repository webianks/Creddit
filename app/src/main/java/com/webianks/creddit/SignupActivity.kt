package com.webianks.creddit

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.cloudant.client.api.ClientBuilder
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by R Ankit on 24-09-2017.
 */
class SignupActivity : AppCompatActivity() {

    lateinit var mPinET: TextInputEditText
    lateinit var mPinAgainET: TextInputEditText
    lateinit var phoneET: TextInputEditText
    lateinit var documentKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        initViews()
    }

    private fun initViews() {

        mPinET = findViewById(R.id.mPinEditText) as TextInputEditText;
        mPinAgainET = findViewById(R.id.mPinAgainEditText) as TextInputEditText;
        phoneET = findViewById(R.id.phoneEditText) as TextInputEditText;

        findViewById(R.id.complete).setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        findViewById(R.id.complete).setOnClickListener{

            if(mPinET.text != null && mPinAgainET.text != null && phoneET.text != null){

                val mpin_value: String = mPinET.text.toString()
                val mpin_again_value: String = mPinAgainET.text.toString()
                val phone_number: String = phoneET.text.toString()

                if(mpin_value.length == 6 && mpin_again_value.length == 6 && phone_number.length == 10){

                    if(mpin_value == mpin_again_value){
                        doSignUp(mpin_value,phone_number)
                    }else
                        showErrorMessage("Both MPIN should be same.")

                }else
                    showErrorMessage("MPIN should be 6 digit length and phone should be 10 digit.")

            }else
                showErrorMessage("Please provide all inputs")
        }

    }

    private fun doSignUp(mpin: String, phone_number: String) {

        CloudantClientAsync().execute(phone_number,mpin)
    }


    internal inner class CloudantClientAsync : AsyncTask<String, Void, String?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg strings: String): String? {

            val phone_number = strings[0]
            val m_pin = strings[1]

            documentKey = phone_number+"@#"+m_pin

            val client = ClientBuilder.account(getString(R.string.cloudantUsername1))
                    .username(getString(R.string.cloudantUsername1))
                    .password(getString(R.string.cloudantPassword1))
                    .build()

            val users_db = client.database("users", false)
            val account_info_db = client.database("account_info", false)
            val personal_info_db = client.database("personal_info", false)

            val randomBalance  = ThreadLocalRandom.current().nextInt(1000, 10000 + 1)

            val newUser = User(phone_number+"@#"+m_pin,"",phone_number,m_pin)
            val newPersonalInfo = PersonalDataWithoutRev(phone_number+"@#"+m_pin+"_personal_","user_"+m_pin,"","")
            val newAccountData = TransactionDataWithoutRev(phone_number+"@#"+m_pin+"_account_",balance = randomBalance.toString(), transactions = null)

            val response = users_db.save(newUser)
            val response2 = personal_info_db.save(newPersonalInfo)
            val response3 = account_info_db.save(newAccountData)

            return response3.error
        }

        override fun onPostExecute(value: String?) {
            super.onPostExecute(value)

            hideDialog()

            if (value == null){
                val intent = Intent(this@SignupActivity,MainActivity::class.java)
                intent.putExtra("user_document_key",documentKey)
                startActivity(intent)
                finish()
            }else{
               showErrorMessage("Can't complete signup.")
            }
        }
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this@SignupActivity,message, Toast.LENGTH_SHORT).show()
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