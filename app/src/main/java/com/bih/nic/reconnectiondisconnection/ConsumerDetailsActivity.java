package com.bih.nic.reconnectiondisconnection;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.asyncTask.DownloadSingleConsumer;
import com.bih.nic.listeners.ConsumerDetailsListener;
import com.bih.nic.model.Consumer;
import com.bih.nic.utilitties.Utiilties;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConsumerDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    Button button_pay;
    TextView text_deleer_name, text_acc_no, text_con_id, text_old_con_id, text_meter_no, text_book_no;
    TextView text_address, text_payable_amount;
    TextView text_mobile2;
    CheckBox check_term;
    Consumer consumer;
    DownloadSingleConsumer downloadSingleConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_details);
        setHeader();
        init();
    }

    private void setHeader() {
        View view = findViewById(R.id.head_cdetails);
        UIUtility.SetHeader(ConsumerDetailsActivity.this, view, "Consumer Details", "","");
    }

    private void init() {
        if (getIntent().hasExtra("object")) {
            consumer = (Consumer) getIntent().getSerializableExtra("object");

            text_deleer_name = (TextView) findViewById(R.id.text_deleer_name);
            text_acc_no = (TextView) findViewById(R.id.text_acc_no);
            text_con_id = (TextView) findViewById(R.id.text_con_id);
            text_old_con_id = (TextView) findViewById(R.id.text_old_con_id);
            text_mobile2 = (TextView) findViewById(R.id.text_mobile2);
            text_meter_no = (TextView) findViewById(R.id.text_meter_no);
            text_book_no = (TextView) findViewById(R.id.text_book_no);
            text_address = (TextView) findViewById(R.id.text_address);
            text_payable_amount = (TextView) findViewById(R.id.text_payable_amount);
            check_term = (CheckBox) findViewById(R.id.check_term);

            setData();

        }
        button_pay = (Button) findViewById(R.id.button_pay);
        button_pay.setOnClickListener(this);
        button_pay.setEnabled(false);
        button_pay.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));


    }

    @Override
    protected void onResume() {
        DownloadSingleConsumer.bindmListener(new ConsumerDetailsListener() {
            @Override
            public void downloadConsumerDetails(String json) {
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    //consumer=new Consumer();
                    consumer.setSubDivId(jsonObject.getString("subDivId"));
                    consumer.setSectionId(jsonObject.getString("sectionId"));
                    consumer.setBookNo(jsonObject.getString("bookNo"));
                    consumer.setConId(jsonObject.getString("conId"));
                    consumer.setActNo(jsonObject.getString("actNo"));
                    consumer.setcName(jsonObject.getString("cName"));
                    consumer.setcAddress(jsonObject.getString("cAddress"));
                    consumer.setTariffId(jsonObject.getString("tariffId"));
                    consumer.setPhase(jsonObject.getString("phase"));
                    consumer.setcLoad(jsonObject.getString("cLoad"));
                    if (jsonObject.has("telefoneNo"))
                        consumer.setMobile(jsonObject.getString("telefoneNo"));
                    if (jsonObject.has("fAmount"))
                        consumer.setfAmount(Double.parseDouble(jsonObject.getString("fAmount")));
                    button_pay.setEnabled(true);
                    button_pay.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    setData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        super.onResume();
        if (Utiilties.isOnline(ConsumerDetailsActivity.this)) {
            downloadSingleConsumer = (DownloadSingleConsumer) new DownloadSingleConsumer(ConsumerDetailsActivity.this).execute(consumer.getSubDivId(), consumer.getBookNo(), consumer.getConId());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_pay) {
            if (check_term.isChecked()) {
                Intent intent = new Intent(ConsumerDetailsActivity.this, DisconnectionActivity.class);
                intent.putExtra("object", consumer);
                startActivityForResult(intent, 2);
            } else {
                Toast.makeText(this, "Check Terms and Conditions !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            this.finish();

        }
    }

    @Override
    protected void onDestroy() {
        if (downloadSingleConsumer != null) {
            if (downloadSingleConsumer.getStatus() == AsyncTask.Status.RUNNING) {
                downloadSingleConsumer.cancel(true);
            }
        }
        super.onDestroy();
    }

    private void setData() {
        text_deleer_name.setText(consumer.getcName());
        text_acc_no.setText("A/C - " + consumer.getActNo());
        text_con_id.setText(consumer.getConId());
        text_old_con_id.setText(consumer.getMobile());
        text_mobile2.setText(consumer.getPhase() + " / " + consumer.getcLoad());
        text_meter_no.setText(consumer.getTariffId());
        text_book_no.setText(consumer.getBookNo());
        text_address.setText(consumer.getcAddress());
        text_payable_amount.setText("" + consumer.getfAmount());
    }
}
