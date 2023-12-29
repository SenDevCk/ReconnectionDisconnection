package com.bih.nic.asyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;


import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Book;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.Urls_this_pro;
import com.bih.nic.utilitties.WebHandler;
import com.bih.nic.webHelper.WebServiceHelper;
import org.json.JSONObject;

import java.util.ArrayList;


public class DownloadBook extends AsyncTask<String, Void, ArrayList<Book>> {
    
    private ProgressDialog dialog1 ;
    private AlertDialog alertDialog;
    private Activity activity;
    public DownloadBook(Activity activity){
        this.activity=activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }
    @Override
    protected void onPreExecute() {
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Loading Book...");
        this.dialog1.show();
    }

    @Override
    protected ArrayList<Book> doInBackground(String... strings) {
        ArrayList<Book> bookArrayList = null;
        try {
            String res = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                JSONObject jsonObject=new JSONObject();
                jsonObject.accumulate("param1",strings[0]);
                res = WebHandler.callByPost(jsonObject.toString(), Urls_this_pro.GET_BOOK );
            }else {
                alertDialog.setMessage("Your device must have atleast Kitkat or Above Version");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            bookArrayList = WebServiceHelper.bookParser(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bookArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Book> statementMS) {
        super.onPostExecute(statementMS);
        if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (statementMS !=null) {
            long c=new DataBaseHelper(activity).saveBook(statementMS, CommonPref.getUserDetails(activity).getUserId());
        }else{
            Toast.makeText(activity, "Server Problem !", Toast.LENGTH_SHORT).show();

        }
        statementMS = new DataBaseHelper(activity).getBooks(CommonPref.getUserDetails(activity).getUserId(),"");
        if (statementMS.size() > 0) {
            Toast.makeText(activity, "Book Downloaded and Saved !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Book Not Found !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(activity, "Syncronise Service Cancelled !", Toast.LENGTH_SHORT).show();
    }

   /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        return encString;
    }*/
}