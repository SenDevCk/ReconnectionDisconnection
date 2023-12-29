package com.bih.nic.asyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bih.nic.listeners.ConsumerDetailsListener;
import com.bih.nic.model.Consumer;
import com.bih.nic.reconnectiondisconnection.DisConnConsumerActivity;
import com.bih.nic.utilitties.Urls_this_pro;
import com.bih.nic.utilitties.WebHandler;

import org.json.JSONArray;
import org.json.JSONObject;

public class DownloadDisConsumer extends AsyncTask<String,Void,String> {
    private ProgressDialog dialog1 ;
    private AlertDialog alertDialog;
    private Activity activity;
    public DownloadDisConsumer(Activity activity){
        this.activity=activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }

    @Override
    protected void onPreExecute() {
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Loading Disconnected Consumer...");
        this.dialog1.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String res = null;
        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                JSONObject jsonObject=new JSONObject();
                jsonObject.accumulate("param1",strings[0]);
                jsonObject.accumulate("param2",strings[1]);
                jsonObject.accumulate("param3",strings[2]);
                res = WebHandler.callByPost(jsonObject.toString(), Urls_this_pro.GET_Dis_CONSUMER );
            }else {
                alertDialog.setMessage("Your device must have atleast Kitkat or Above Version");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (dialog1.isShowing())dialog1.dismiss();
        if (s!=null){
            try{
                JSONArray jsonArray=new JSONArray(s);
                if (jsonArray.length()>0){
                    Intent intent=new Intent(activity, DisConnConsumerActivity.class);
                    intent.putExtra("mjson",s);
                    activity.startActivity(intent);
                }else{
                    Toast.makeText(activity, "No Data Found !", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }else{
            Toast.makeText(activity, "Server Problem !", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCancelled() {
        Toast.makeText(activity, "Service Canceled !", Toast.LENGTH_SHORT).show();
        super.onCancelled();
    }


}
