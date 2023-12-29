package com.bih.nic.asyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.reconnectiondisconnection.R;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.GlobalVariables;
import com.bih.nic.utilitties.Urls_this_pro;
import com.bih.nic.utilitties.WebHandler;

import org.json.JSONObject;

public class DirectUploadDisconnectedConsumer extends AsyncTask<String, Void, String> {
    Activity activity;
    private ProgressDialog dialog1;
    private AlertDialog alertDialog;
    long pid;

    public DirectUploadDisconnectedConsumer(Activity activity) {
        this.activity = activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Uploading Consumer...");
        this.dialog1.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        pid = Long.parseLong(strings[12]);
        String res = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("subDivId", strings[0]);
                jsonObject.accumulate("mruName", strings[1]);
                jsonObject.accumulate("conId", strings[2]);
                jsonObject.accumulate("tariffId", strings[3]);
                jsonObject.accumulate("phase", strings[4]);
                jsonObject.accumulate("load", strings[5]);
                jsonObject.accumulate("reading", strings[6]);
                jsonObject.accumulate("readStatus", strings[7]);
                jsonObject.accumulate("mobile", strings[8]);
                jsonObject.accumulate("remarks", strings[9]);
                jsonObject.accumulate("meterImage", strings[10]);
                jsonObject.accumulate("latitude", strings[11]);
                jsonObject.accumulate("user", strings[13]);
                jsonObject.accumulate("imei", strings[14]);
                jsonObject.accumulate("longitude", strings[15]);
                jsonObject.accumulate("sectionId", strings[16]);
                Log.d("log", jsonObject.toString());
                res = WebHandler.callByPost(jsonObject.toString(), Urls_this_pro.UPLOAD_CONSUMERS);
            } else {
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
    protected void onPostExecute(String res) {
        super.onPostExecute(res);
        if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (res != null) {
            try {
                if (res.trim().equals("\"1\"")) {
                    DataBaseHelper placeData = new DataBaseHelper(activity);
                    placeData.deletePendingUpload(pid, CommonPref.getUserDetails(activity).getUserId());
                    Toast.makeText(activity,  "Disconnecion Successful !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    //intent.putExtra("MESSAGE",message);
                    activity.setResult(activity.RESULT_OK, intent);
                    activity.finish();
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "Server Problem !", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(activity, "Upload Service Cancelled !", Toast.LENGTH_SHORT).show();
    }

}
