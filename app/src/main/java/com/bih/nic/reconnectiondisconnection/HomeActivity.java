package com.bih.nic.reconnectiondisconnection;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.asyncTask.DownloadBook;
import com.bih.nic.asyncTask.DownloadConsumers;
import com.bih.nic.asyncTask.ReportLoader;
import com.bih.nic.asyncTask.UploadDisconnectedConsumer;
import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Book;
import com.bih.nic.model.Category;
import com.bih.nic.model.Consumer;
import com.bih.nic.model.UserInfo2;
import com.bih.nic.utilitties.CommonPref;
import com.bih.nic.utilitties.GlobalVariables;
import com.bih.nic.utilitties.Utiilties;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.bih.nic.utilitties.Utiilties.BitMapToString;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private static final int FIRST_ACTIVITY_REQUEST_CODE = 1;
    TextView textView, text_count;
    LinearLayout download_book, ll_download_consumer, ll_con_list, ll_upload, download_report;
    DownloadBook downloadBook;
    DownloadConsumers downloadConsumer;
    Book book;
    Category category;
    private String amount = null;
    TextView text_fromdate, text_todate;
    String date_clik = "";
    UploadDisconnectedConsumer uploadDisconnectedConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setHeader();
        textView = (TextView) findViewById(R.id.text_select_book);
        text_count = (TextView) findViewById(R.id.text_count);
        textView.setOnClickListener(this);

        download_book = (LinearLayout) findViewById(R.id.download_book);
        ll_download_consumer = (LinearLayout) findViewById(R.id.ll_download_consumer);
        ll_con_list = (LinearLayout) findViewById(R.id.ll_con_list);
        ll_upload = (LinearLayout) findViewById(R.id.ll_upload);
        download_report = (LinearLayout) findViewById(R.id.download_report);
        download_report.bringToFront();
        download_book.setOnClickListener(this);
        ll_download_consumer.setOnClickListener(this);
        ll_con_list.setOnClickListener(this);
        ll_upload.setOnClickListener(this);
        download_report.setOnClickListener(this);
    }

    private void setHeader() {
        View view = findViewById(R.id.in_header);
        UIUtility.SetHeader(HomeActivity.this, view, "RC-DC", "","1");
    }

    @Override
    protected void onResume() {
        UserInfo2 userInfo2 = CommonPref.getUserDetails(HomeActivity.this);
        long count = new DataBaseHelper(HomeActivity.this).getAllDisConsumersCount(userInfo2.getUserId());
        if (count > 0) {
            text_count.setText("" + count);
        } else {
            text_count.setText("0");
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.text_select_book) {
            UserInfo2 userInfo2 = CommonPref.getUserDetails(HomeActivity.this);
            if (new DataBaseHelper(HomeActivity.this).getBooksCount(userInfo2.getUserId()) > 0) {
                Intent intent = new Intent(this, BookListActivity.class);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            } else {
                Toast.makeText(this, getResources().getString(R.string.don_book), Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.download_book) {
            if (Utiilties.isOnline(HomeActivity.this)) {
                downloadBook = (DownloadBook) new DownloadBook(HomeActivity.this).execute(CommonPref.getUserDetails(HomeActivity.this).getSubDivId());
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.ll_download_consumer) {
            if (book != null) {
               /* category=null;
                amount=null;
            Intent intent = new Intent(this, DownloadConsumerActivity.class);
            startActivityForResult(intent,FIRST_ACTIVITY_REQUEST_CODE);*/
                if (Utiilties.isOnline(HomeActivity.this)) {
                    downloadConsumer = (DownloadConsumers) new DownloadConsumers(HomeActivity.this).execute(CommonPref.getUserDetails(HomeActivity.this).getSubDivId(),
                            book.getBook_number(), "ALL");
                } else {
                    Toast.makeText(this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.sel_book), Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.ll_con_list) {
            UserInfo2 userInfo2 = CommonPref.getUserDetails(HomeActivity.this);
            if (book == null) {
                Toast.makeText(this, getResources().getString(R.string.sel_book), Toast.LENGTH_SHORT).show();
            } else if (new DataBaseHelper(HomeActivity.this).getConsumerscount(userInfo2.getUserId(), book.getBook_number().trim()) <= 0) {
                Toast.makeText(this, getResources().getString(R.string.don_consumer), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, ConsumerListActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        } else if (view.getId() == R.id.ll_upload) {
            uploadData();
            // Toast.makeText(this, "Coming Soon..", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.download_report) {
            dialogReport();
        }
    }

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (date_clik.equals("F")) {
            text_fromdate.setText(sdf.format(myCalendar.getTime()));
        } else {
            text_todate.setText(sdf.format(myCalendar.getTime()));
        }
    }

    private void dialogReport() {
        final Dialog setup_dialog = new Dialog(HomeActivity.this);
        // Include dialog.xml file
        setup_dialog.setContentView(R.layout.neft_dialog);
        // Set dialog title
        setup_dialog.setTitle("");
        setup_dialog.setCancelable(false);
        // set values for custom dialog components - text, image and button
        text_fromdate = (TextView) setup_dialog.findViewById(R.id.text_fromdate);
        text_todate = (TextView) setup_dialog.findViewById(R.id.text_todate);
        text_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_clik = "F";
                new DatePickerDialog(HomeActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        text_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_clik = "T";
                new DatePickerDialog(HomeActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        ImageView close_setup = (ImageView) setup_dialog.findViewById(R.id.img_close);
        close_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup_dialog.dismiss();
            }
        });
        Button search_for_mru = (Button) setup_dialog.findViewById(R.id.search_for_mru);
        search_for_mru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                if (text_fromdate.getText().toString().trim().equals("--Select From Date--")) {
                    Toast.makeText(HomeActivity.this, "Select From Date", Toast.LENGTH_SHORT).show();
                }
                else if (text_todate.getText().toString().trim().equals("--Select To Date--")) {
                    Toast.makeText(HomeActivity.this, "Select To Date", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        int com = formatter.parse(text_todate.getText().toString().trim()).compareTo(formatter.parse(text_fromdate.getText().toString().trim()));
                        if (com <0) {
                            Toast.makeText(HomeActivity.this, "Selected-To-Date is not correct", Toast.LENGTH_SHORT).show();
                        } else if (Utiilties.monthsBetweenDates(formatter.parse(text_fromdate.getText().toString().trim()), formatter.parse(text_todate.getText().toString().trim())) > 3) {
                            Toast.makeText(HomeActivity.this, "Date difference must be 3 months !", Toast.LENGTH_SHORT).show();
                        } else {
                            setup_dialog.dismiss();
                            UserInfo2 userInfo2 = CommonPref.getUserDetails(HomeActivity.this);
                            new ReportLoader(HomeActivity.this).execute(userInfo2.getUserId(), userInfo2.getImeiNumber(), text_fromdate.getText().toString().trim(), text_todate.getText().toString().trim());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        setup_dialog.show();
    }

    private void uploadData() {
        long countPending = new DataBaseHelper(HomeActivity.this).getAllDisConsumersCount(CommonPref.getUserDetails(HomeActivity.this).getUserId());
        if (countPending > 0) {
            AlertDialog.Builder ab = new AlertDialog.Builder(
                    HomeActivity.this);
            ab.setTitle(getResources().getString(R.string.upload_title));
            ab.setMessage(getResources().getString(R.string.upload_msg));
            ab.setPositiveButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {

                    dialog.dismiss();

                }
            });

            ab.setNegativeButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                @TargetApi(Build.VERSION_CODES.CUPCAKE)
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    //code for uploading data
                    DataBaseHelper dbHelper = new DataBaseHelper(HomeActivity.this);
                    ArrayList<Consumer> dataProgress = dbHelper.getAllDisConsumers(CommonPref.getUserDetails(getApplicationContext()).getUserId());
                    if (dataProgress.size() > 0) {
                        for (Consumer data : dataProgress) {
                            UserInfo2 userInfo2 = CommonPref.getUserDetails(HomeActivity.this);
                            uploadDisconnectedConsumer = new UploadDisconnectedConsumer(HomeActivity.this, text_count);
                            uploadDisconnectedConsumer.execute(data.getSubDivId().trim(), data.getBookNo().trim(), data.getConId().trim(),
                                    data.getTariffId().trim(), data.getPhase().trim(), data.getcLoad().trim(), data.getReading().trim(), data.getRead_status().trim(), data.getMobile().trim(), data.getRemarks().trim(), BitMapToString(data.getImage()),data.getLatitude() ,
                                    data.get_id().trim(), userInfo2.getUserId(), userInfo2.getImeiNumber(),data.getLongitude(),data.getSectionId());
                        }
                        GlobalVariables.listSize = dataProgress.size();
                    }
                }
            });

            ab.create().getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            ab.show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_data),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                // get String data from Intent
                book = (Book) data.getSerializableExtra("book");
                // set text view with string
                textView.setText(book.getBook_number());
            }
        } else if (requestCode == FIRST_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("category"))
                    category = (Category) data.getSerializableExtra("category");
                if (data.hasExtra("amount")) amount = data.getStringExtra("amount");
                if (Utiilties.isOnline(HomeActivity.this)) {
                    if (!data.hasExtra("category") && !data.hasExtra("amount")) {
                        downloadConsumer = (DownloadConsumers) new DownloadConsumers(HomeActivity.this).execute(CommonPref.getUserDetails(HomeActivity.this).getSubDivId(),
                                book.getBook_number(), "ALL");
                    } else if (data.hasExtra("category") && data.hasExtra("amount")) {
                        downloadConsumer = (DownloadConsumers) new DownloadConsumers(HomeActivity.this).execute(CommonPref.getUserDetails(HomeActivity.this).getSubDivId(),
                                book.getBook_number(), category.getTariffId(), amount);
                    } else if (data.hasExtra("amount")) {
                        downloadConsumer = (DownloadConsumers) new DownloadConsumers(HomeActivity.this).execute(CommonPref.getUserDetails(HomeActivity.this).getSubDivId(),
                                book.getBook_number(), "ALL", amount);
                    } else if (data.hasExtra("category")) {
                        downloadConsumer = (DownloadConsumers) new DownloadConsumers(HomeActivity.this).execute(CommonPref.getUserDetails(HomeActivity.this).getSubDivId(),
                                book.getBook_number(), category.getTariffId());
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(HomeActivity.this);
        ab.setTitle("Exit");
        ab.setMessage("Would you like to exit app ?");
        ab.setPositiveButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.dismiss();

            }
        });

        ab.setNegativeButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //code for uploading data
                dialog.dismiss();
                HomeActivity.super.onBackPressed();
            }
        });

        ab.create().getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        ab.show();
       //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (downloadBook != null) {
            if (downloadBook.getStatus() != AsyncTask.Status.RUNNING && !downloadBook.isCancelled()) {
                downloadBook.cancel(true);
            }
        }
        if (downloadConsumer != null) {
            if (downloadConsumer.getStatus() != AsyncTask.Status.RUNNING && !downloadConsumer.isCancelled()) {
                downloadConsumer.cancel(true);
            }
        }
        super.onDestroy();
    }
}
