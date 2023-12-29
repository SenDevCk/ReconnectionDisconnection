package com.bih.nic.reconnectiondisconnection;
/**
 * Created by NIC2 on 12-01-2019.
 */
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Versioninfo;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.MarshmallowPermission;
import com.bih.nic.utilitties.Urls_this_pro;
import com.bih.nic.utilitties.Utiilties;
import com.bih.nic.utilitties.WebHandler;
import com.bih.nic.webHelper.WebServiceHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import pl.tajchert.waitingdots.DotsTextView;

public class SplashActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    String version;
    TelephonyManager tm;
    private static String imei;
  MarshmallowPermission MARSHMALLOW_PERMISSION;
    DotsTextView dotsTextView;
    CheckUpdate checkUpdate;
    TextView rc_dc_tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // getSupportActionBar().hide();
        dotsTextView=(DotsTextView)findViewById(R.id.dots);
        rc_dc_tag=(TextView)findViewById(R.id.rc_dc_tag);
        dotsTextView.setText("Please wait ...");
        dotsTextView.setTextSize(15);
        dotsTextView.show();
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/albas.ttf");
        rc_dc_tag.setTypeface(face);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Call some material design APIs here
            if (checkAndRequestPermissions()) {
                init2();
            }

        } else {
            // Implement this feature without material design
            init2();
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Call some material design APIs here
            if (checkAndRequestPermissions()) {
                init();
            }

        } else {
            // Implement this feature without material design
            init();
        }
    }
    private void init2(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        },1000);

    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @SuppressLint("MissingPermission")
    private void init() {
        DataBaseHelper db = new DataBaseHelper(this);
        try {
            db.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            db.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;

        }

        try {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null)
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            imei = tm.getDeviceId();
            //imei = "861878037876718";
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.e("App Version : ", "" + version + " ( " + imei + " )");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        checkOnline();
    }



    private boolean checkAndRequestPermissions() {

        int read_phone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int read_external = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int read_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (read_external != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (read_phone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (read_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //init();
                    init();
                } else {
                    Toast.makeText(this, "Please enable all permissions !", Toast.LENGTH_SHORT).show();
                    if (version!=null) init();
                }
                break;
        }
    }

    @SuppressLint("NewApi")
    private class CheckUpdate extends AsyncTask<String, Void, Versioninfo> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Versioninfo doInBackground(String... Params) {
            MARSHMALLOW_PERMISSION = new MarshmallowPermission(SplashActivity.this, android.Manifest.permission.READ_PHONE_STATE);
            if (MARSHMALLOW_PERMISSION.result == -1 || MARSHMALLOW_PERMISSION.result == 0) {
                try {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    imei = tm.getDeviceId();
                    version = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    imei = tm.getDeviceId();
                    version = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String res=null;

            try{
                JSONObject jsonObject= new JSONObject();;
                jsonObject.accumulate("param1","RCDC");
                res= WebHandler.callByPost(jsonObject.toString(),Urls_this_pro.OUIRY_VERSION);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            Versioninfo versioninfo = WebServiceHelper.CheckVersion(res);
            return versioninfo;
        }

        @Override
        protected void onPostExecute(final Versioninfo versioninfo) {

            final AlertDialog.Builder ab = new AlertDialog.Builder(SplashActivity.this);
            ab.setCancelable(false);
            if (versioninfo != null) {
                CommonPref.setCheckUpdate(getApplicationContext(), System.currentTimeMillis());
                if (versioninfo.isVerUpdated() == false) {
                    ab.setTitle(versioninfo.getAdminTitle());
                    ab.setMessage(Html.fromHtml(versioninfo.getAdminMsg()));
                    ab.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.dismiss();
                                    showDailog(ab, versioninfo);

                                }
                            });
                    ab.setNegativeButton("IGNORE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            start();
                        }
                    });

                    ab.show();
                } else if (versioninfo.isValidDevice() == false) {
                    showDailog(ab, versioninfo);
                } else {
                    showDailog(ab, versioninfo);
                }
            }

        }
    }

    protected void checkOnline() {
        // TODO Auto-generated method stub
        super.onResume();

        if (Utiilties.isOnline(SplashActivity.this)) {
          checkUpdate=new CheckUpdate();
            //if (checkUpdate==null && checkUpdate.getStatus()==AsyncTask.Status.PENDING) {
                checkUpdate.execute();
            //}
        } else {

            AlertDialog.Builder ab = new AlertDialog.Builder(SplashActivity.this);
            ab.setMessage("Either enable internet connection or go offline");
            ab.setPositiveButton("ENABLE INTERNET", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(I);
                    //new CheckUpdate().execute();
                }
            });

            ab.setNegativeButton("GO OFFLINE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {

                    start();
                }
            });

            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
            ab.show();
        }
    }
    private void start(){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        SplashActivity.this.finish();
    }

    private void showDailog(AlertDialog.Builder ab,
                            final Versioninfo versioninfo) {

        if (!versioninfo.isVerUpdated()) {

            if (versioninfo.getPriority().equals("0")) {

                start();
            } else if (versioninfo.getPriority().endsWith("1")) {

                ab.setTitle(versioninfo.getUpdateTile());
                ab.setMessage(versioninfo.getUpdateMsg());
                ab.setPositiveButton("UPDATE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                                myWebLink.setData(Uri.parse(versioninfo.getAppUrl()));
                                startActivity(myWebLink);
                                dialog.dismiss();
                            }
                        });
                ab.setNegativeButton("IGNORE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                dialog.dismiss();

                                start();
                            }

                        });

                ab.show();

            } else if (versioninfo.getPriority().equals("2")) {
                ab.setTitle(versioninfo.getUpdateTile());
                ab.setMessage(versioninfo.getUpdateMsg());
                ab.setPositiveButton("UPDATE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                                myWebLink.setData(Uri.parse(versioninfo.getAppUrl()));
                                startActivity(myWebLink);
                                dialog.dismiss();

                            }
                        });

                ab.show();
            }
        } else {
            start();
        }
    }
    @Override
    protected void onDestroy() {
        dotsTextView.hideAndStop();
        super.onDestroy();

    }


}

