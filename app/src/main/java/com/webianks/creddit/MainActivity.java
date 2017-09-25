package com.webianks.creddit;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Params;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView balance;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        Intent intent = getIntent();
        String user_doc_key = intent.getStringExtra("user_document_key");
        new CloudantClientAsync().execute(user_doc_key+"_account_");

    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        balance = (TextView) findViewById(R.id.main_balance);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.change_info)
            startActivity(new Intent(this,PersonalInfoActivity.class));

        return super.onOptionsItemSelected(item);
    }

    class CloudantClientAsync extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            CloudantClient client = ClientBuilder.account(getString(R.string.cloudantUsername1))
                    .username(getString(R.string.cloudantUsername1))
                    .password(getString(R.string.cloudantPassword1))
                    .build();


            Database account_db = client.database("account_info", false);
            try {
                AccountInfo accountInfo = account_db.find(AccountInfo.class, strings[0]);
                return accountInfo.getBalance();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            progressBar.setVisibility(View.GONE);

            if (value != null)
              balance.setText("$"+value);
        }
    }
}
