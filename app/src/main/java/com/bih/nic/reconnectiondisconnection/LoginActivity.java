package com.bih.nic.reconnectiondisconnection;
/**
 * Created by NIC2 on 10-01-2019.
 */
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.UserInfo2;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.GlobalVariables;
import com.bih.nic.utilitties.MarshmallowPermission;
import com.bih.nic.utilitties.Urls_this_pro;
import com.bih.nic.utilitties.Utiilties;
import com.bih.nic.utilitties.WebHandler;
import com.bih.nic.webHelper.WebServiceHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    EditText edit_name, edit_password;
    Button sign_in;
    TextView text_login_head;
    //------------WEBSERVICE  FOR THIS PROJECT---PPMelaWebService.cs in E:\SOftwar\TestService\App_code folder
    ConnectivityManager cm;
    public static String UserPhoto;
    String version, districtId = "";
    TelephonyManager tm;
    private static String imei = "";
    public String zonecode, maxid;
    int CALLING_ASYNC_TASK;


    String NoPacs, NoVMs, SelPacs, SelVMs;
    MarshmallowPermission MARSHMALLOW_PERMISSION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().hide();
        text_login_head=(TextView)findViewById(R.id.text_login_head);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/albas.ttf");
        text_login_head.setTypeface(face);
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
    }

    public void Login(View view) {
        if (!GlobalVariables.isOffline && !Utiilties.isOnline(LoginActivity.this)) {
            AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
            ab.setMessage("Internet Connection is not avaliable.Please Turn ON Network Connection OR Continue With Off-line Mode.");
            ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent I = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(I);
                }
            });
            ab.setNegativeButton("Continue Offline", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    GlobalVariables.isOffline = true;
                }

            });
            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
            ab.show();

        } else {

            edit_name = (EditText) findViewById(R.id.edit_name);
            edit_password = (EditText) findViewById(R.id.edit_password);
            String[] param = new String[3];
            param[0] = edit_name.getText().toString();
            param[1] = edit_password.getText().toString();
            param[2] = imei;

            if (!param[0].equals("") && !param[0].equals("null")) {
                if (!param[1].equals("") && !param[1].equals("null")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        new LoginTask().execute(param);
                    }
                    else{
                        Toast.makeText(this, "Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    edit_password.setError("Enter Password");
                }
            } else if (param[1].equals("") || param[1].equals("null")) {
                edit_name.setError("Enter UserID");
                edit_password.setError("Enter Password");
            } else {
                edit_name.setError("Enter UserId");
            }
        }
    }
    public void readPhoneState() {
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, android.Manifest.permission.READ_PHONE_STATE);
        if (MARSHMALLOW_PERMISSION.result == -1 || MARSHMALLOW_PERMISSION.result == 0) {
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null)
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }imei = tm.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) imei = tm.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv = (TextView) findViewById(R.id.text_app_version);
            tv.setText("एप्लिकेशन वेरीज़न : " + version + " ( " + imei + " )");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        readPhoneState();
        //checkOnline();
    }

    protected void checkOnline() {
        // TODO Auto-generated method stub
        super.onResume();

        if (Utiilties.isOnline(LoginActivity.this) == false) {

            AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
            ab.setMessage("Internet Connection is not avaliable.Please Turn ON Network Connection OR Continue With Off-line Mode.");
            ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    GlobalVariables.isOffline = false;
                    Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(I);
                }
            });

            ab.setNegativeButton("Continue Offline", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    GlobalVariables.isOffline = true;
                }
            });

            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
            ab.show();

        } else {

            GlobalVariables.isOffline = false;
            // new CheckUpdate().execute();
        }
    }
    

    private class LoginTask extends AsyncTask<String, Void, UserInfo2> {

        private final ProgressDialog dialog1 = new ProgressDialog(LoginActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();

        @Override
        protected void onPreExecute() {

            this.dialog1.setCanceledOnTouchOutside(false);
            this.dialog1.setMessage("Authenticating...");
            this.dialog1.show();
        }

        @Override
        protected UserInfo2 doInBackground(String... param) {
            UserInfo2 userInfo2=null;
            if (Utiilties.isOnline(LoginActivity.this)) {

                try {
                    JSONObject jsonObject= new JSONObject();;
                    jsonObject.accumulate("param1",param[0]);
                    jsonObject.accumulate("param2",param[1]);
                    jsonObject.accumulate("param3",param[2]);
                    String res=WebHandler.callByPost(jsonObject.toString(), Urls_this_pro.LOG_IN_URL);
                    userInfo2= WebServiceHelper.loginParser(res);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return userInfo2;

            } else {
                userInfo2 = CommonPref.getUserDetails(LoginActivity.this);
                if (userInfo2.getUserId().length() > 4) {
                    //userInfo2.setActiveFlag("A");
                    return userInfo2;
                } else {
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(UserInfo2 result) {
            if (this.dialog1.isShowing()) {
                this.dialog1.cancel();
                final EditText userPass = (EditText) findViewById(R.id.edit_password);
                final EditText userName = (EditText) findViewById(R.id.edit_name);
                //	new GetZonesList().execute();//-----------BY SHEK--------------
                if (result == null) {
                    alertDialog.setTitle("Failed");
                    alertDialog.setMessage("Authentication Failed. Please try again.");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            userName.setFocusable(true);
                        }
                    });
                    alertDialog.show();

                } else if (!result.getActiveFlag().equals("A")){
                    alertDialog.setTitle("Login Failed");
                    alertDialog.setMessage("UserID or Password wrong !");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            userName.setFocusable(true);
                        }
                    });
                    alertDialog.show();
                }else {
                    DataBaseHelper placeData = new DataBaseHelper(LoginActivity.this);
                    Intent cPannel = new Intent(getApplicationContext(), HomeActivity.class);
                    if (Utiilties.isOnline(LoginActivity.this) == true) {
                        if (result != null) {
                            if (imei.equalsIgnoreCase(result.getImeiNumber())) {
                                try {
                                    GlobalVariables.LoggedUser = result;
                                    result.setPassword(userPass.getText().toString());
                                    CommonPref.setUserDetails(getApplicationContext(), result);
                                    startActivity(cPannel);
                                    finish();

                                } catch (Exception ex) {
                                    Toast.makeText(LoginActivity.this, "Login failed due to Some Error !", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                alertDialog.setTitle("Device Not Registered");
                                alertDialog.setMessage("Sorry, your device is not registered!.\r\nPlease contact your Admin.");
                                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        userName.setFocusable(true);
                                    }
                                });
                                alertDialog.show();
                            }
                        }
                    }
                    else {
                        if (CommonPref.getUserDetails(LoginActivity.this) != null) {
                            GlobalVariables.LoggedUser = CommonPref.getUserDetails(LoginActivity.this);
                            if (!GlobalVariables.LoggedUser.getUserId().equalsIgnoreCase("") && !GlobalVariables.LoggedUser.getPassword().equalsIgnoreCase("")) {
                                startActivity(cPannel);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "User name and password not matched !", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Please enable internet connection for first time login.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }
}
