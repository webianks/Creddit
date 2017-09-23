package com.webianks.creddit;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cloudant.sync.documentstore.AttachmentException;
import com.cloudant.sync.documentstore.ConflictException;
import com.cloudant.sync.documentstore.DocumentBodyFactory;
import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain storage path on Android
        File path = getApplicationContext().getDir("documentstores", Context.MODE_PRIVATE);

        try {
            // Obtain reference to DocumentStore instance, creating it if doesn't exist
            DocumentStore ds = DocumentStore.getInstance(new File(path, "creddit_document_store"));

            // Create a document
            DocumentRevision revision = new DocumentRevision();
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("animal", "cat");
            revision.setBody(DocumentBodyFactory.create(body));
            DocumentRevision saved = ds.database().create(revision);

            DocumentRevision updated = ds.database().update(saved);

            // Read a document
            DocumentRevision aRevision = ds.database().read(updated.getId());
            aRevision.getAttachments();

        } catch (DocumentStoreException dse) {
            System.err.println("Problem opening or accessing DocumentStore: "+dse);
        } catch (DocumentNotFoundException e) {
            e.printStackTrace();
        } catch (AttachmentException e) {
            e.printStackTrace();
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }
}
