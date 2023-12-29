package com.bih.nic.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bih.nic.asyncTask.DownloadDisConsumer;
import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Category;
import com.bih.nic.model.Consumer;
import com.bih.nic.model.ReportEntity;
import com.bih.nic.model.UserInfo2;
import com.bih.nic.reconnectiondisconnection.ConsumerDetailsActivity;
import com.bih.nic.reconnectiondisconnection.R;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.Utiilties;

import java.util.ArrayList;

public class ReportAdapter  extends BaseAdapter {
    Activity activity;
    private ArrayList<ReportEntity> reportEntities;
    LayoutInflater layoutInflater;
    public ReportAdapter(Activity activity, ArrayList<ReportEntity> reportEntities){
        this.activity=activity;
        this.reportEntities=reportEntities;
        //consumers=new DataBaseHelper(activity).getConsumers(CommonPref.getUserDetails(activity).getUserId(),con_id,book);
        layoutInflater=activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return reportEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return reportEntities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(reportEntities.get(i).getSubdiv());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        convertView=layoutInflater.inflate(R.layout.report_item, null, false);
        TextView text_date=(TextView)convertView.findViewById(R.id.text_date);
        TextView report_recept_no=(TextView)convertView.findViewById(R.id.report_recept_no);
        TextView report_amount=(TextView)convertView.findViewById(R.id.report_amount);
        LinearLayout ll_st_head=(LinearLayout)convertView.findViewById(R.id.ll_st_head);
        text_date.setText(""+ reportEntities.get(position).getDate().substring(0,10));
        report_recept_no.setText(""+ reportEntities.get(position).getCount());
        report_amount.setText(""+ reportEntities.get(position).getSubdiv());
        //ll_st_head.setBackgroundColor(activity.getResources().getColor(R.color.holo_red_light));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utiilties.isOnline(activity)){
                    UserInfo2 userInfo2=CommonPref.getUserDetails(activity);
                    String[] token=reportEntities.get(position).getDate().split(" ");
                    String[] token2=token[0].split("-");
                    new DownloadDisConsumer(activity).execute(token2[2]+"-"+token2[1]+"-"+token2[0],userInfo2.getUserId(),userInfo2.getImeiNumber());
                }else{
                    Toast.makeText(activity, "No Internet Avalable !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }


}
