package com.bih.nic.reconnectiondisconnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.asyncTask.DownloadCategories;
import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Category;
import com.bih.nic.utilitties.GlobalVariables;
import com.bih.nic.utilitties.Utiilties;

public class LocalFilterActivity extends AppCompatActivity implements View.OnClickListener {
    DownloadCategories downloadCategories;
    Button download_con;
    TextView text_category;
    Category categorie;
    EditText edit_amount;
    ImageButton download_cotegoriy;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_filter);

        setHeader();
        setUI();
    }

    private void setUI() {
        download_cotegoriy=(ImageButton)findViewById(R.id.download_cotegoriy);
        download_con=(Button)findViewById(R.id.download_con);
        edit_amount=(EditText) findViewById(R.id.edit_amount);
        edit_amount.setText("");
        categorie=null;
        text_category=(TextView)findViewById(R.id.text_category);
        download_cotegoriy.setOnClickListener(this);
        text_category.setOnClickListener(this);
        download_con.setOnClickListener(this);
    }

    private void setHeader() {
        View view=findViewById(R.id.lay_dc);
        UIUtility.SetHeader(LocalFilterActivity.this,view,getResources().getString(R.string.filter),"","");
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.download_cotegoriy){
            if (Utiilties.isOnline(LocalFilterActivity.this)){
                downloadCategories= (DownloadCategories) new DownloadCategories(LocalFilterActivity.this).execute();
            }else{
                Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }else if (view.getId()==R.id.text_category){
            if (new DataBaseHelper(LocalFilterActivity.this).getCotegoryCount()>0){
                Intent intent = new Intent(this, CategoryListActivity.class);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);

            }
        }else if (view.getId()==R.id.download_con){
            Intent intent = new Intent();
            if (categorie!=null) intent.putExtra("category", categorie);
            if (!edit_amount.getText().toString().trim().equals("")) intent.putExtra("amount", edit_amount.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                categorie = (Category) data.getSerializableExtra("category");
                text_category.setText(categorie.getTariffId());
            }
        }
    }

    @Override
    public void onBackPressed() {
        GlobalVariables.flag=false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        //GlobalVariables.flag=false;
        super.onDestroy();
    }
}
