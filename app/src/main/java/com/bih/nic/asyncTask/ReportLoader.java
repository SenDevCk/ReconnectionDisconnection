package com.bih.nic.asyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bih.nic.reconnectiondisconnection.ReportListActivity;
import com.bih.nic.utilitties.Urls_this_pro;
import com.bih.nic.utilitties.Utiilties;
import com.bih.nic.utilitties.WebHandler;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReportLoader extends AsyncTask<String, Void, String> {
    Activity activity;
    private ProgressDialog dialog1;
    private AlertDialog alertDialog;
    public ReportLoader(Activity activity) {
        this.activity=activity;
        dialog1= new ProgressDialog(this.activity);
        alertDialog=new AlertDialog.Builder(this.activity).create();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Loading Report...");
        this.dialog1.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String res = null;
        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                JSONObject jsonObject=new JSONObject();
                jsonObject.accumulate("param3",strings[0]);
                //jsonObject.accumulate("param3","admin");
                 jsonObject.accumulate("param5",strings[1]);
                //jsonObject.accumulate("param5","359243095268168");
                jsonObject.accumulate("param1",strings[2]);
                jsonObject.accumulate("param2",strings[3]);
                res = WebHandler.callByPost2(jsonObject.toString(), Urls_this_pro.GET_REPORT);
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
        if (dialog1.isShowing())dialog1.cancel();
        if (s!=null){
           try{
               JSONArray jsonArray=new JSONArray(s);
               if (jsonArray.length()>0){
                   Intent intent=new Intent(activity, ReportListActivity.class);
                   intent.putExtra("json",s);
                   activity.startActivity(intent);
               }else {
                   Toast.makeText(activity, "NO DATA FOUND !", Toast.LENGTH_SHORT).show();
               }
           }catch (Exception e){e.printStackTrace();}
        }else {
            Toast.makeText(activity, "NO DATA FOUND ! DUE TO SERVER PROBLEM !", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (dialog1.isShowing())dialog1.cancel();
        Toast.makeText(activity, "NEFT Service Canceled !", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        if (dialog1.isShowing())dialog1.cancel();
        Toast.makeText(activity, ""+s, Toast.LENGTH_SHORT).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP );//.getEncoder().encodeToString(chipperdata);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        return encString;
    }
}
