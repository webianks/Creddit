package com.webianks.creddit;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Params;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView balance;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private String doc_key;
    private String previousRev;
    private List<SingleTransaction> transactionList = new ArrayList<>();
    private String currentBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        Intent intent = getIntent();
        doc_key = intent.getStringExtra("user_document_key");

        new CloudantClientAsyncRead().execute(doc_key + "_account_");
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        balance = (TextView) findViewById(R.id.main_balance);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        fab = (FloatingActionButton) findViewById(R.id.add);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputSheet();
            }
        });
    }


    private void showInputSheet() {
        BottomSheetDialogFragment bottomSheetDialogFragment = new CredditBottomSheetDialogFragment();
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.change_info) {
            Intent intent = new Intent(this, PersonalInfoActivity.class);
            intent.putExtra("document_key", doc_key);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    class CloudantClientAsyncWrite extends AsyncTask<SingleTransaction, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SingleTransaction... singleTransactions) {

            CloudantClient client = ClientBuilder.account(getString(R.string.cloudantUsername1))
                    .username(getString(R.string.cloudantUsername1))
                    .password(getString(R.string.cloudantPassword1))
                    .build();

            Database account_db = client.database("account_info", false);

            try {

                //SingleTransaction singleTransaction = new SingleTransaction();
                transactionList.add(singleTransactions[0]);
                int spent = Integer.valueOf(singleTransactions[0].getAmount());
                int current_amout = Integer.valueOf(currentBalance);
                int new_amount = current_amout - spent;

                TransactionData transactionData = new TransactionData(doc_key + "_account_", previousRev, String.valueOf(new_amount), transactionList);
                account_db.update(transactionData);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean value) {
            super.onPostExecute(value);

            progressBar.setVisibility(View.GONE);

            if (value)
                new CloudantClientAsyncRead().execute(doc_key + "_account_");
        }
    }

    //For TransactionHistory
    class CloudantClientAsyncRead extends AsyncTask<String, Void, TransactionData> {

        @Override
        protected TransactionData doInBackground(String... strings) {

            CloudantClient client = ClientBuilder.account(getString(R.string.cloudantUsername1))
                    .username(getString(R.string.cloudantUsername1))
                    .password(getString(R.string.cloudantPassword1))
                    .build();


            Database account_db = client.database("account_info", false);
            try {
                TransactionData transactionData = account_db.find(TransactionData.class, strings[0]);
                return transactionData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(TransactionData value) {
            super.onPostExecute(value);

            progressBar.setVisibility(View.GONE);

            if (value != null) {
                currentBalance = value.getBalance();
                previousRev = value.get_rev();
                balance.setText("$" + value.getBalance());
                showTransactionData(value);
            }
        }
    }

    private void showTransactionData(TransactionData transactionData) {
        List<SingleTransaction> list = transactionData.getTransactions();
        if (list != null) {
            transactionList.clear();
            transactionList.addAll(list);
            MainRecyclerViewAdapter adapter = new MainRecyclerViewAdapter(this, list);
            recyclerView.setAdapter(adapter);
        }
    }

    public static class CredditBottomSheetDialogFragment extends BottomSheetDialogFragment {

        private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };

        @Override
        public void setupDialog(Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);
            dialog.setContentView(contentView);


            final EditText amoutET = (EditText) contentView.findViewById(R.id.amount);
            final EditText paidToET = (EditText) contentView.findViewById(R.id.paid_to);

            contentView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String amount = amoutET.getText().toString();
                    String paid_to = paidToET.getText().toString();

                    if (amount.length() > 0 && paid_to.length() > 0) {

                        int previousAmount = Integer.parseInt(((MainActivity)getActivity()).currentBalance);
                        int amount_spent = Integer.parseInt(amount);

                        if (previousAmount - amount_spent >= 0){
                            ((MainActivity) getActivity()).writeTransaction(amount, paid_to);
                            dismiss();
                        }else
                            Toast.makeText(getActivity(), "You can't spent this much amount.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "Please provide all inputs.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();

            if (behavior != null && behavior instanceof BottomSheetBehavior) {
                ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            }
        }
    }

    private void writeTransaction(String amount, String paid_to) {
        progressBar.setVisibility(View.VISIBLE);
        String transaction_id = Util.Companion.randomString(UUID.randomUUID().toString(), 5);
        SingleTransaction singleTransaction = new SingleTransaction(amount, paid_to, transaction_id);
        new CloudantClientAsyncWrite().execute(singleTransaction);
    }
}
