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
import android.widget.Toast;

import com.bih.nic.asyncTask.UploadDisconnectedConsumer;
import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Book;
import com.bih.nic.model.Category;
import com.bih.nic.model.Consumer;
import com.bih.nic.reconnectiondisconnection.ConsumerDetailsActivity;
import com.bih.nic.reconnectiondisconnection.HomeActivity;
import com.bih.nic.reconnectiondisconnection.R;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.GlobalVariables;

import java.util.ArrayList;

import static com.bih.nic.utilitties.Utiilties.BitMapToString;

public class ConsumerListAdapter extends BaseAdapter {
    Activity activity;
    private ArrayList<Consumer> consumers;
    LayoutInflater layoutInflater;
    String book;
    TextView worning;
    public ConsumerListAdapter(Activity activity,String con_id,String book,TextView worning){
        this.activity=activity;
        this.worning=worning;
        this.book=book;
        consumers=new DataBaseHelper(activity).getConsumers(CommonPref.getUserDetails(activity).getUserId(),con_id,book);
        if (consumers.size()<=0){
            this.worning.setVisibility(View.VISIBLE);
        }else{
            this.worning.setVisibility(View.GONE);
        }
        layoutInflater=activity.getLayoutInflater();
    }

    public ConsumerListAdapter(Activity activity, Category category, String amount,String book,String con_id,TextView worning){
        this.activity=activity;
        this.worning=worning;
        consumers=new DataBaseHelper(activity).getConsumersByCategory(CommonPref.getUserDetails(activity).getUserId(),category,amount,book,con_id);
        if (consumers.size()<=0){
            this.worning.setVisibility(View.VISIBLE);
        }else{
            this.worning.setVisibility(View.GONE);
        }
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
        return Long.parseLong(consumers.get(i).get_id());
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
        Typeface face = Typeface.createFromAsset(activity.getAssets(), "fonts/name_font2.ttf");
        con_name.setText(""+consumers.get(i).getcName());
        con_name.setTypeface(face);
        con_id.setText(""+consumers.get(i).getConId());
        book_no.setText(""+consumers.get(i).getBookNo());
        text_amt.setText(""+consumers.get(i).getfAmount());
        text_address.setText(""+consumers.get(i).getcAddress());
       if (consumers.get(i).getDis_conn().equals("N")) {
           view.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   onClickItem(i);
               }
           });
       }else{
           text_connect.setVisibility(View.VISIBLE);
           text_connect.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                   ab.setTitle(activity.getResources().getString(R.string.reconnect_title));
                   ab.setMessage(activity.getResources().getString(R.string.reconnect_msg));
                   ab.setPositiveButton(activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int whichButton) {

                           dialog.dismiss();

                       }
                   });

                   ab.setNegativeButton(activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                       @TargetApi(Build.VERSION_CODES.CUPCAKE)
                       @Override
                       public void onClick(DialogInterface dialog, int whichButton) {
                           //code for uploading data
                           long c=new DataBaseHelper(activity).connect(consumers.get(i));
                           if (c>0) {
                               Consumer consumer = consumers.get(i);
                               consumer.setDis_conn("N");
                               consumers.set(i, consumer);
                               ConsumerListAdapter.this.notifyDataSetInvalidated();
                           }
                       }
                   });

                   ab.create().getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                   ab.show();


               }
           });
       }
        return view;
    }

    private void onClickItem(int pos){
       Intent intent=new Intent(activity, ConsumerDetailsActivity.class);
       intent.putExtra("object",consumers.get(pos));
       activity.startActivity(intent);

    }
}
