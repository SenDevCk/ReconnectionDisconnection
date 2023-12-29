package com.bih.nic.reconnectiondisconnection;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.adapters.ReportAdapter;
import com.bih.nic.model.ReportEntity;
import com.bih.nic.webHelper.WebServiceHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportListActivity extends AppCompatActivity {

    ListView list_report;
    TextView text_grand_total, text_report_not_found;
    LinearLayout ll_tag, ll_total;
    ArrayList<ReportEntity> reportEntities;
    private int grand_total=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        setHeader();
        ll_tag = (LinearLayout) findViewById(R.id.ll_tag);
        ll_total = (LinearLayout) findViewById(R.id.ll_total);

        text_grand_total = (TextView) findViewById(R.id.text_grand_total);
        text_report_not_found = (TextView) findViewById(R.id.text_report_not_found);
        list_report = (ListView) findViewById(R.id.list_report);
        String mjson=getIntent().getStringExtra("json");

        reportEntities= WebServiceHelper.reportParser(mjson);

        if (reportEntities.size() >0) {
            ll_tag.setVisibility(View.VISIBLE);
            ll_total.setVisibility(View.VISIBLE);
            text_report_not_found.setVisibility(View.GONE);
            list_report.setAdapter(new ReportAdapter(ReportListActivity.this,reportEntities));
        }else{
            ll_tag.setVisibility(View.GONE);
            ll_total.setVisibility(View.GONE);
            text_report_not_found.append("\n Please Syncronise data");
            text_report_not_found.setVisibility(View.VISIBLE);
            list_report.setVisibility(View.GONE);
        }
        for (ReportEntity reportEntity : reportEntities) {
            grand_total = grand_total + Integer.parseInt(reportEntity.getCount().trim());
            text_grand_total.setText("" + grand_total);
        }
    }

    private void setHeader() {
        View view=findViewById(R.id.lay_rep);
        UIUtility.SetHeader(ReportListActivity.this,view,getResources().getString(R.string.header_report),"","");
    }
}
