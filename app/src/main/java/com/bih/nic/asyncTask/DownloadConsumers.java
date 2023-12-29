package com.bih.nic.asyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Book;
import com.bih.nic.model.Consumer;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.Urls_this_pro;
import com.bih.nic.utilitties.WebHandler;
import com.bih.nic.webHelper.WebServiceHelper;

import org.json.JSONObject;

import java.util.ArrayList;

public class DownloadConsumers extends AsyncTask<String,Void, ArrayList<Consumer>> {
    Activity activity;
    private ProgressDialog dialog1 ;
    private AlertDialog alertDialog;
    String book;
    public DownloadConsumers(Activity activity){
        this.activity=activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Loading Consumer...");
        this.dialog1.show();
    }

    @Override
    protected ArrayList<Consumer> doInBackground(String... strings) {
        book=strings[1];
        ArrayList<Consumer> consumerArrayList = null;
        try {
            String res = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                JSONObject jsonObject=new JSONObject();
                jsonObject.accumulate("param1", strings[0]);//sub_id
                jsonObject.accumulate("param2", strings[1]);//book_num
                jsonObject.accumulate("param3", strings[2]);//tariff_id
                if (strings.length>3) {
                    jsonObject.accumulate("param4", Integer.parseInt(strings[3]));//amount
                }
                res = WebHandler.callByPost(jsonObject.toString(), Urls_this_pro.GET_CONSUMERS );
            }else {
                alertDialog.setMessage("Your device must have atleast Kitkat or Above Version");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            consumerArrayList = WebServiceHelper.consumerParser(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return consumerArrayList;
    }


    @Override
    protected void onPostExecute(ArrayList<Consumer> consumers) {
        super.onPostExecute(consumers);
        if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (consumers !=null) {
            long c=new DataBaseHelper(activity).saveConsumers(consumers, CommonPref.getUserDetails(activity).getUserId());
        }else{
            Toast.makeText(activity, "Server Problem !", Toast.LENGTH_SHORT).show();

        }
        consumers = new DataBaseHelper(activity).getConsumers(CommonPref.getUserDetails(activity).getUserId(),"",book);
        if (consumers.size() > 0) {
            Toast.makeText(activity, "Consumers Downloaded and Saved !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Consumers Not Found !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(activity, "Consumer Service Cancelled !", Toast.LENGTH_SHORT).show();
    }

}
