package com.webianks.creddit;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        new CloudantClientAsync().execute();

    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        balance = (TextView) findViewById(R.id.main_balance);

    }

    class CloudantClientAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            CloudantClient client = ClientBuilder.account(getString(R.string.cloudantUsername1))
                    .username(getString(R.string.cloudantUsername1))
                    .password(getString(R.string.cloudantPassword1))
                    .build();

            List<String> databases = client.getAllDbs();
            Log.d("webi","All my databases : ");
            for ( String db : databases ) {
                Log.d("webi",db);
            }

            Database users_db = client.database("users", false);
            User user = users_db.find(User.class,"ramankit_1214353AB");
            Log.d("webi",user.getName());

            return null;
        }

    }
}
