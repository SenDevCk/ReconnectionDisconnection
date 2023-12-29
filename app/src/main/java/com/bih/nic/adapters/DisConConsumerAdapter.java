package com.bih.nic.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Category;
import com.bih.nic.model.Consumer;
import com.bih.nic.reconnectiondisconnection.ConsumerDetailsActivity;
import com.bih.nic.reconnectiondisconnection.R;
import com.bih.nic.utilitties.CommonPref;

import java.util.ArrayList;

public class DisConConsumerAdapter extends BaseAdapter {
    Activity activity;
    private ArrayList<Consumer> consumers;
    LayoutInflater layoutInflater;
    String book;
    public DisConConsumerAdapter(Activity activity,ArrayList<Consumer> consumers){
        this.activity=activity;
        this.consumers=consumers;
        layoutInflater=activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return consumers.size();
    }

    @Override
    public Object getItem(int i) {
        return consumers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view=layoutInflater.inflate(R.layout.consumer_item,null,false);
        TextView con_name=(TextView)view.findViewById(R.id.con_name);
        TextView con_id=(TextView)view.findViewById(R.id.con_id);
        TextView book_no=(TextView)view.findViewById(R.id.book_no);
        TextView text_amt=(TextView)view.findViewById(R.id.text_amt);
        TextView text_address=(TextView)view.findViewById(R.id.text_address);
        ImageView text_connect=(ImageView)view.findViewById(R.id.text_connect);
        text_connect.setVisibility(View.GONE);
        Typeface face = Typeface.createFromAsset(activity.getAssets(), "fonts/date_time.ttf");
        Typeface face2 = Typeface.createFromAsset(activity.getAssets(), "fonts/remarks_font.ttf");
        con_name.setText(""+consumers.get(i).getDate_time());
        con_name.setTypeface(face);
        con_name.setTextSize(14);
        con_name.setTypeface(null, Typeface.NORMAL);

        con_id.setText(""+consumers.get(i).getConId());
        book_no.setText(""+consumers.get(i).getBookNo());
        text_amt.setText(""+consumers.get(i).getfAmount());
        text_address.setText(""+consumers.get(i).getRemarks());
        text_address.setTypeface(face2);
        return view;
    }

}
