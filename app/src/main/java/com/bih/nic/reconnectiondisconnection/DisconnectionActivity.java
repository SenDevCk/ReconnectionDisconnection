package com.bih.nic.reconnectiondisconnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.asyncTask.DirectUploadDisconnectedConsumer;
import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Consumer;
import com.bih.nic.model.UserInfo2;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.Utiilties;

import static com.bih.nic.utilitties.Utiilties.BitMapToString;

public class DisconnectionActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout ll_take_img_pahchan;
    ImageView img_pahchan;
    EditText edit_reading,edit_remarks;
    Button dis_connect;
    Consumer consumer;
    private static final int CAMERA_PIC = 12;
    Bitmap bitmap;
    byte[] imageData1;
    String latitude, longitude, gps_time,readstatus;
    RadioGroup radio_gp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnection);
        consumer = (Consumer) getIntent().getSerializableExtra("object");
        setHeader();
        init();
    }

    private void setHeader() {
        View view=findViewById(R.id.lay_dc);
        UIUtility.SetHeader(DisconnectionActivity.this,view,"Consumer ID : "+consumer.getConId(),"N","");
    }

    private void init() {
        ll_take_img_pahchan = (LinearLayout) findViewById(R.id.ll_take_img_pahchan);
        img_pahchan = (ImageView) findViewById(R.id.img_pahchan);
        edit_reading = (EditText) findViewById(R.id.edit_reading);
        edit_remarks = (EditText) findViewById(R.id.edit_remarks);
        dis_connect = (Button) findViewById(R.id.dis_connect);
        radio_gp = (RadioGroup) findViewById(R.id.radio_gp);
        radio_gp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {

                int radioBtnID = rGroup.getCheckedRadioButtonId();

                RadioButton radioB = (RadioButton)rGroup.findViewById(radioBtnID);

                int position = radio_gp.indexOfChild(radioB);
                readstatus = (String) radioB.getText();
            }
        });
        dis_connect.setOnClickListener(this);
        ll_take_img_pahchan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dis_connect) {
            if (edit_reading.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Enter Meter Reading !", Toast.LENGTH_SHORT).show();
                edit_reading.setError("Enter Meter Reading !");
            } else if (readstatus==null) {
            Toast.makeText(this, "Select Read Status", Toast.LENGTH_SHORT).show();
            } else if (bitmap == null) {
                Toast.makeText(this, "Take Meter Image", Toast.LENGTH_SHORT).show();
            }else if (edit_remarks.getText().toString().trim().equals("") || edit_remarks.getText().toString().trim().length()<3){
                Toast.makeText(this, "Enter Remarks !", Toast.LENGTH_SHORT).show();
                edit_remarks.setError("Remarks to Short !");
            }else {
                consumer.setRead_status(readstatus);
                if (Utiilties.isOnline(DisconnectionActivity.this)){
                    UserInfo2 userInfo2 = CommonPref.getUserDetails(DisconnectionActivity.this);
                    new DirectUploadDisconnectedConsumer(DisconnectionActivity.this).execute(consumer.getSubDivId().trim(), consumer.getBookNo().trim(), consumer.getConId().trim(),
                            consumer.getTariffId().trim(), consumer.getPhase().trim(), consumer.getcLoad().trim(), edit_reading.getText().toString().trim(), consumer.getRead_status().trim(), consumer.getMobile().trim(),  edit_remarks.getText().toString().trim(), BitMapToString(bitmap), latitude, consumer.get_id().trim(), userInfo2.getUserId(), userInfo2.getImeiNumber(),longitude,consumer.getSectionId());
                }
                else {
                    long c = new DataBaseHelper(DisconnectionActivity.this).disconnect(consumer.getConId(), edit_reading.getText().toString().trim(), latitude, longitude, imageData1, consumer.getRead_status(), edit_remarks.getText().toString().trim());
                    if (c > 0) {
                        Toast.makeText(this, "Disconnected !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        //intent.putExtra("MESSAGE",message);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Not Disconnected !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (view.getId() == R.id.ll_take_img_pahchan) {
            Intent iCamera = new Intent(DisconnectionActivity.this, CameraActivity.class);
            iCamera.putExtra("KEY_PIC", "1");
            startActivityForResult(iCamera, CAMERA_PIC);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_PIC:
                if (resultCode == Activity.RESULT_OK) {
                    byte[] imgData = data.getByteArrayExtra("CapturedImage");
                    switch (data.getIntExtra("KEY_PIC", 0)) {
                        case 1:
                            bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                            img_pahchan.setScaleType(ImageView.ScaleType.FIT_XY);
                            img_pahchan.setImageBitmap(Utiilties.GenerateThumbnail(bitmap, 500, 500));
                            imageData1 = imgData;
                            latitude = data.getStringExtra("Lat");
                            longitude = data.getStringExtra("Lng");
                            gps_time = data.getStringExtra("GPSTime");
                            break;
                    }
                }
        }

    }


}
