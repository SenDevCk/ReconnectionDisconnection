package com.bih.nic.reconnectiondisconnection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.asyncTask.DownloadCategories;
import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Category;
import com.bih.nic.utilitties.Utiilties;

public class DownloadConsumerActivity extends AppCompatActivity implements View.OnClickListener {
    DownloadCategories downloadCategories;
    Button download_cotegoriy,download_con;
    TextView text_category;
    Category categories;
    EditText edit_amount;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_consumer);

        setHeader();
        setUI();
    }

    private void setUI() {
        download_cotegoriy=(Button)findViewById(R.id.download_cotegoriy);
        download_con=(Button)findViewById(R.id.download_con);
        edit_amount=(EditText) findViewById(R.id.edit_amount);
        text_category=(TextView)findViewById(R.id.text_category);
        download_cotegoriy.setOnClickListener(this);
        text_category.setOnClickListener(this);
        download_con.setOnClickListener(this);
    }

    private void setHeader() {
        View view=findViewById(R.id.lay_dc);
        UIUtility.SetHeader(DownloadConsumerActivity.this,view,getResources().getString(R.string.filter),"","");
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.download_cotegoriy){
            if (Utiilties.isOnline(DownloadConsumerActivity.this)){
                downloadCategories= (DownloadCategories) new DownloadCategories(DownloadConsumerActivity.this).execute();
            }else{
                Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }else if (view.getId()==R.id.text_category){
            if (new DataBaseHelper(DownloadConsumerActivity.this).getCotegoryCount()>0){
                Intent intent = new Intent(this, CategoryListActivity.class);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);

            }
        }else if (view.getId()==R.id.download_con){
           /* if (categories==null){
                Toast.makeText(this, "Select Category !", Toast.LENGTH_SHORT).show();
            }else if (edit_amount.getText().toString().trim().equals("")){
                Toast.makeText(this, "Enter Amount !", Toast.LENGTH_SHORT).show();
            }else{*/
                Intent intent = new Intent();
                if (categories!=null) intent.putExtra("category", categories);
                if (!edit_amount.getText().toString().trim().equals("")) intent.putExtra("amount", edit_amount.getText().toString().trim());
                setResult(RESULT_OK, intent);
                finish();
            //}
        }
    }

    @Override
    protected void onDestroy() {
        if (downloadCategories!=null){
            if (downloadCategories.getStatus()== AsyncTask.Status.RUNNING){
                downloadCategories.cancel(true);
            }
        }
        super.onDestroy();
    }
    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                // get String data from Intent
                categories = (Category) data.getSerializableExtra("category");
                // set text view with string
                text_category.setText(categories.getTariffId());
            }
        }
    }

    /*    @Override
    protected void onResume() {
        super.onResume();
        DownloadCategories.bindmListener(new CategoryListener() {
            @Override
            public void categoryDownloaded() {
                runOnUiThread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        if (new DataBaseHelper(DownloadConsumerActivity.this).getCotegoryCount()>0){

                        }
                 }
                });
            }
        });
    }*/
}
