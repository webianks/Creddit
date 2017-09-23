package com.webianks.creddit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DatastoreNotCreatedException;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;

public class MainActivity extends AppCompatActivity {


    private java.net.URI cloudantUri;
    private Datastore ds;
    private DatastoreManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create a DatastoreManager using application internal storage path
        java.io.File path = getApplicationContext().getDir("datastores", android.content.Context.MODE_PRIVATE);
        manager = DatastoreManager.getInstance(path);

        try {
            cloudantUri = new java.net.URI(getApplicationContext().getResources().getString(R.string.cloudantUrl1) + "/your_db_name");
            // Create the Datastore object you'll use in all of your Cloudant operations:
            ds = manager.openDatastore("creddit_datastore");

            // At this point, you may wish to create pull and push replicators for bi-directional sync.  It is up
            // to you, the developer, to program the interaction between the Cloudant database and the mobile application.
            // A simple example follows.  The example does not include a countdown latch, does not show how to
            // subscribe to sync events, and ignores sync errors.

            // Create and run the pull replicator
            //com.cloudant.sync.replication.Replicator pullReplicator = com.cloudant.sync.replication.ReplicatorBuilder.pull().from(cloudantUri).to(ds).build();
            //pullReplicator.start();
            // Create and run the push replicator
            //com.cloudant.sync.replication.Replicator pushReplicator = com.cloudant.sync.replication.ReplicatorBuilder.push().to(cloudantUri).from(ds).build();
            //pushReplicator.start();

        } catch (java.net.URISyntaxException e) {
            android.util.Log.e("TAG", e.getMessage(), e);
        } catch (DatastoreNotCreatedException e) {
            android.util.Log.e("TAG", e.getMessage(), e);
        }

    }
}
