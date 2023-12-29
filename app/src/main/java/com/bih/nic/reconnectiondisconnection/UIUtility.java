package com.bih.nic.reconnectiondisconnection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bih.nic.adapters.ConsumerListAdapter;
import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Consumer;

public class UIUtility {

    public static void SetHeader(final Activity activity, View view, String header_text, String filter, final String flag_main_page){
        Typeface face = Typeface.createFromAsset(activity.getAssets(), "fonts/header_font.ttf");
        TextView textView=(TextView)view.findViewById(R.id.text_header);
        textView.setText(""+header_text);
        textView.setTypeface(face);
        ImageView imageView=(ImageView)view.findViewById(R.id.back_button);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag_main_page.equals("1")){
                    AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                    ab.setTitle("Exit");
                    ab.setMessage("Would you like to exit app ?");
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
                            dialog.dismiss();
                           activity.finish();
                        }
                    });

                    ab.create().getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                    ab.show();
                }else {
                    activity.finish();
                }
            }
        });
        ImageView button_filter=(ImageView)view.findViewById(R.id.button_filter);
        if (filter.trim().equalsIgnoreCase("Y")){
            button_filter.setVisibility(View.VISIBLE);
            button_filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(activity,LocalFilterActivity.class);
                    activity.startActivityForResult(intent,10);
                }
            });
        }
    }
}
