package com.bih.nic.asyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.listeners.CategoryListener;
import com.bih.nic.model.Category;
import com.bih.nic.reconnectiondisconnection.R;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.Urls_this_pro;
import com.bih.nic.utilitties.WebHandler;
import com.bih.nic.webHelper.WebServiceHelper;

import java.util.ArrayList;

public class DownloadCategories extends AsyncTask<Void,Void,ArrayList<Category>> {
    Activity activity;
    private ProgressDialog dialog1 ;
    private AlertDialog alertDialog;
    static CategoryListener categoryListener;
    public DownloadCategories(Activity activity){
        this.activity=activity;
        this.activity=activity;
        dialog1 = new ProgressDialog(this.activity);
        alertDialog = new AlertDialog.Builder(this.activity).create();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog1.setCanceledOnTouchOutside(false);
        this.dialog1.setMessage("Loading Category...");
        this.dialog1.show();
    }

    @Override
    protected ArrayList<Category> doInBackground(Void... voids) {
        ArrayList<Category> categoriesArrayList = null;
        try {
            String res = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                res = WebHandler.callByGet( Urls_this_pro.GET_CATEGORIES );
            }else {
                alertDialog.setMessage(activity.getResources().getString(R.string.ver_msg));
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            categoriesArrayList = WebServiceHelper.categoriesParser(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return categoriesArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Category> categories) {
        super.onPostExecute(categories);

        if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (categories !=null) {
            long c=new DataBaseHelper(activity).saveCategories(categories, CommonPref.getUserDetails(activity).getUserId());
        }else{
            Toast.makeText(activity, "Server Problem !", Toast.LENGTH_SHORT).show();

        }
        categories = new DataBaseHelper(activity).getCategories();
        if (categories.size() > 0) {
            Toast.makeText(activity, "Category Downloaded and Saved !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Category Not Found !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(activity, "Syncronise Service Cancelled !", Toast.LENGTH_SHORT).show();
    }
    public static void bindmListener(CategoryListener listener) {
        categoryListener = listener;
    }

}
