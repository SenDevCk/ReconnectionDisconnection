package com.bih.nic.reconnectiondisconnection;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.adapters.DisConConsumerAdapter;
import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisConnConsumerActivity extends AppCompatActivity {
    ArrayList<Consumer> consumers;
    ListView list_consumer;
    String mjson;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dis_conn_consumer);
        setHeader();
        mjson=getIntent().getStringExtra("mjson");
        consumers=new DataBaseHelper(DisConnConsumerActivity.this).getAllConsumers(mjson);
        list_consumer = (ListView) findViewById(R.id.list_consumer);
        list_consumer.setAdapter(new DisConConsumerAdapter(DisConnConsumerActivity.this, consumers));
    }

    private void setHeader() {
        View view=findViewById(R.id.header_con_list);
        UIUtility.SetHeader(DisConnConsumerActivity.this,view,getResources().getString(R.string.header_dis_con),"","");
    }
}
