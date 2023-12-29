package com.bih.nic.databaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.bih.nic.model.Book;
import com.bih.nic.model.Category;
import com.bih.nic.model.Consumer;
import com.bih.nic.utilitties.Utiilties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by NIC2 on 1/6/2018.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    // The Android's default system path of your application database.
    private static String DB_PATH = "";
    private static String DB_NAME = "reconn_disconne.db";
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        if(Build.VERSION.SDK_INT  >= 29){
            DB_PATH= context.getDatabasePath(DB_NAME).getPath();
        }
        else if (Build.VERSION.SDK_INT  >= 21) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.myContext = context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }
    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            // do nothing - database already exist
            Log.d("DataBase", "exist");
        } else {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            Log.d("DataBase", "exist");
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        String myPath=null;
        try {
            if(Build.VERSION.SDK_INT  >= 29){
                myPath = DB_PATH ;
            }else {
                myPath = DB_PATH + DB_NAME;
            }
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS
                            | SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException e) {

            // database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    public boolean databaseExist() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName =null;
        if(Build.VERSION.SDK_INT  >= 29){
            outFileName = DB_PATH ;
        }else {
            outFileName = DB_PATH + DB_NAME;
        }

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        // Open the database
        String myPath = null;
        if(Build.VERSION.SDK_INT >= 29){
            myPath = DB_PATH ;
        }else {
            myPath = DB_PATH + DB_NAME;
        }
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion)
            return;
        ClearAllTable(db);
        onCreate(db);
        if (oldVersion == 1) {
            Log.d("New Version", "Data can be upgraded");
        }

        Log.d("Sample Data", "onUpgrade	: " + newVersion);
    }

    public void ClearAllTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS Book");
        db.execSQL("DROP TABLE IF EXISTS Categoriy");
        db.execSQL("DROP TABLE IF EXISTS CONSUMER");
    }

    public long saveBook(ArrayList<Book> mruEntities, String userid) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Book");
        try {
            for (Book bookNoEntity : mruEntities) {
                ContentValues values = new ContentValues();
                values.put("book_number", bookNoEntity.getBook_number().trim());
                values.put("uid", userid.trim());
                String[] whereArgs = new String[]{bookNoEntity.getBook_number().trim()};
                c = db.update("Book", values, "book_number=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("Book", null, values);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for BookNo");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;
    }

    public ArrayList<Book> getBooks(String userid, String enter_char) {
        ArrayList<Book> bookArrayList = new ArrayList<>();
        bookArrayList.clear();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = "select * from Book where uid='" + userid + "' and (book_number LIKE'" + enter_char + "%' ) ORDER BY book_number ASC";
            Cursor cursor = db.rawQuery(quary, null);
            while (cursor.moveToNext()) {
                Book bookNoEntity = new Book();
                bookNoEntity.set_id(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
                bookNoEntity.setBook_number(cursor.getString(cursor.getColumnIndex("book_number")));
                bookNoEntity.setUid(cursor.getString(cursor.getColumnIndex("uid")));
                bookArrayList.add(bookNoEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookArrayList;
    }

    public long getBooksCount(String userID) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = "select _id from Book where uid=?";
            Cursor cursor = db.rawQuery(quary, new String[]{userID});
            c = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }


    public long saveCategories(ArrayList<Category> categories, String userId) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Categoriy");
        try {
            for (Category categories1 : categories) {
                ContentValues values = new ContentValues();
                values.put("tariffId", categories1.getTariffId().trim());
                String[] whereArgs = new String[]{categories1.getTariffId().trim()};
                c = db.update("Categoriy", values, "tariffId=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("Categoriy", null, values);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for Categoriy");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;
    }

    public ArrayList<Category> getCategories() {
        ArrayList<Category> categoriesArrayList = new ArrayList<>();
        categoriesArrayList.clear();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = "select * from Categoriy";
            Cursor cursor = db.rawQuery(quary, null);
            while (cursor.moveToNext()) {
                Category categories = new Category();
                categories.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
                categories.setTariffId(cursor.getString(cursor.getColumnIndex("tariffId")));
                categoriesArrayList.add(categories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoriesArrayList;
    }

    public long getCotegoryCount() {
        long c = -1;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = "select * from Categoriy";
            cursor = db.rawQuery(quary, null);
            c = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public long saveConsumers(ArrayList<Consumer> consumers, String userId) {
        long c = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("delete from CONSUMER");
        try {
            for (Consumer consumer : consumers) {
                ContentValues values = new ContentValues();
                values.put("subDivId", consumer.getSubDivId().trim());
                values.put("sectionId", consumer.getSectionId().trim());
                values.put("bookNo", consumer.getBookNo().trim());
                values.put("conId", consumer.getConId().trim());
                values.put("actNo", consumer.getActNo().trim());
                values.put("cName", consumer.getcName().trim());
                values.put("cAddress", consumer.getcAddress().trim());
                values.put("tariffId", consumer.getTariffId().trim());
                values.put("phase", consumer.getPhase().trim());
                values.put("cLoad", consumer.getcLoad().trim());
                values.put("fAmount", consumer.getfAmount());
                values.put("mobile", consumer.getMobile().trim());
                values.put("USER_ID", userId);
                String[] whereArgs = new String[]{consumer.getConId().trim()};
                c = db.update("CONSUMER", values, "conId=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("CONSUMER", null, values);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for CONSUMER");
            // TODO: handle exception
        } finally {
            db.close();
            this.getWritableDatabase().close();
        }
        return c;
    }

    public ArrayList<Consumer> getConsumers(String userID, String con_id, String book) {
        ArrayList<Consumer> consumerArrayList = new ArrayList<>();
        consumerArrayList.clear();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = null;
            if (!con_id.trim().equals("")) {
                quary = "select * from CONSUMER where USER_ID='" + userID + "' and bookNo ='" + book + "' and conId LIKE'%" + con_id + "%'";
            } else {
                quary = "select * from CONSUMER where USER_ID='" + userID + "'and bookNo ='" + book + "'";
            }
            //Cursor cursor = db.rawQuery(quary, new String[]{userID,con_id});
            Cursor cursor = db.rawQuery(quary, null);
            while (cursor.moveToNext()) {
                Consumer consumer = new Consumer();
                consumer.set_id(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
                consumer.setSubDivId(cursor.getString(cursor.getColumnIndex("subDivId")));
                consumer.setSectionId(cursor.getString(cursor.getColumnIndex("sectionId")));
                consumer.setBookNo(cursor.getString(cursor.getColumnIndex("bookNo")));
                consumer.setConId(cursor.getString(cursor.getColumnIndex("conId")));
                consumer.setActNo(cursor.getString(cursor.getColumnIndex("actNo")));
                consumer.setcName(cursor.getString(cursor.getColumnIndex("cName")));
                consumer.setcAddress(cursor.getString(cursor.getColumnIndex("cAddress")));
                consumer.setTariffId(cursor.getString(cursor.getColumnIndex("tariffId")));
                consumer.setPhase(cursor.getString(cursor.getColumnIndex("phase")));
                consumer.setcLoad(cursor.getString(cursor.getColumnIndex("cLoad")));
                consumer.setfAmount(cursor.getDouble(cursor.getColumnIndex("fAmount")));
                consumer.setDis_conn(cursor.getString(cursor.getColumnIndex("dis_conn")));
                consumer.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                //consumer.setRead_status(cursor.getString(cursor.getColumnIndex("dis_conn")));
                consumerArrayList.add(consumer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return consumerArrayList;
    }

    public long getConsumerscount(String userID, String book) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = "select * from CONSUMER where USER_ID=? and bookNo=?";
            cursor = db.rawQuery(quary, new String[]{userID, book});

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return cursor.getCount();
    }

    public long disconnect(String conid, String reading, String lat, String lang, byte[] bytes, String read_status, String remarks) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("reading", reading);
            contentValues.put("longitude", lang);
            contentValues.put("latitude", lat);
            contentValues.put("image", bytes);
            contentValues.put("read_status", read_status);
            contentValues.put("remarks", remarks);
            contentValues.put("dis_conn", "Y");
            c = db.update("CONSUMER", contentValues, "conId=?", new String[]{conid});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public ArrayList<Consumer> getAllDisConsumers(String userID) {
        ArrayList<Consumer> consumerArrayList = new ArrayList<>();
        consumerArrayList.clear();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = "select * from CONSUMER where USER_ID='" + userID + "'and dis_conn='Y'";
            //Cursor cursor = db.rawQuery(quary, new String[]{userID,con_id});
            Cursor cursor = db.rawQuery(quary, null);
            while (cursor.moveToNext()) {
                Consumer consumer = new Consumer();
                consumer.set_id(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
                consumer.setSubDivId(cursor.getString(cursor.getColumnIndex("subDivId")));
                consumer.setSectionId(cursor.getString(cursor.getColumnIndex("sectionId")));
                consumer.setBookNo(cursor.getString(cursor.getColumnIndex("bookNo")));
                consumer.setConId(cursor.getString(cursor.getColumnIndex("conId")));
                consumer.setActNo(cursor.getString(cursor.getColumnIndex("actNo")));
                consumer.setcName(cursor.getString(cursor.getColumnIndex("cName")));
                consumer.setcAddress(cursor.getString(cursor.getColumnIndex("cAddress")));
                consumer.setTariffId(cursor.getString(cursor.getColumnIndex("tariffId")));
                consumer.setPhase(cursor.getString(cursor.getColumnIndex("phase")));
                consumer.setcLoad(cursor.getString(cursor.getColumnIndex("cLoad")));
                consumer.setfAmount(cursor.getDouble(cursor.getColumnIndex("fAmount")));
                consumer.setImage(Utiilties.StringToBitMap(Utiilties.BitArrayToString(cursor.getBlob(cursor.getColumnIndex("image")))));
                consumer.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
                consumer.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
                consumer.setReading(cursor.getString(cursor.getColumnIndex("reading")));
                consumer.setRead_status(cursor.getString(cursor.getColumnIndex("read_status")));
                consumer.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
                consumer.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                //consumer.setU(cursor.getString(cursor.getColumnIndex("USER_ID")));
                consumerArrayList.add(consumer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return consumerArrayList;
    }

    public long getAllDisConsumersCount(String userID) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = "select * from CONSUMER where USER_ID='" + userID + "'and dis_conn='Y'";
            cursor = db.rawQuery(quary, null);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return cursor.getCount();
    }

    public void deletePendingUpload(long pid, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM CONSUMER WHERE USER_ID='" + userId + "' and _id='" + pid + "'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    public long connect(Consumer consumer) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("dis_conn", "N");
            c = db.update("CONSUMER", contentValues, "conId=?", new String[]{consumer.getConId().trim()});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public ArrayList<Consumer> getConsumersByCategory(String userId, Category category, String amount, String book, String con_id) {

        ArrayList<Consumer> consumerArrayList = new ArrayList<>();
        consumerArrayList.clear();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = null;
            if (category != null && amount != null) {
                if (con_id.equals(""))
                    quary = "select * from CONSUMER where USER_ID='" + userId + "' and bookNo ='" + book + "'and fAmount >='" + amount + "' and tariffId ='" + category.getTariffId().trim() + "'";
                else
                    quary = "select * from CONSUMER where USER_ID='" + userId + "' and bookNo ='" + book + "'and fAmount >='" + amount + "' and tariffId ='" + category.getTariffId().trim() + "'and conId LIKE'%" + con_id + "%'";
            } else if (category != null) {
                if (con_id.equals(""))
                    quary = "select * from CONSUMER where USER_ID='" + userId + "'and bookNo ='" + book + "'and tariffId ='" + category.getTariffId().trim() + "'";
                else
                    quary = "select * from CONSUMER where USER_ID='" + userId + "'and bookNo ='" + book + "'and tariffId ='" + category.getTariffId().trim() + "'and conId LIKE'%" + con_id + "%'";
            } else if (amount != null) {
                if (con_id.equals(""))
                    quary = "select * from CONSUMER where USER_ID='" + userId + "'and bookNo ='" + book + "'and fAmount >='" + amount + "'";
                else
                    quary = "select * from CONSUMER where USER_ID='" + userId + "'and bookNo ='" + book + "'and fAmount >='" + amount + "'and conId LIKE'%" + con_id + "%'";
            } else {
                if (con_id.equals(""))
                    quary = "select * from CONSUMER where USER_ID='" + userId + "'and bookNo ='" + book + "'";
                else
                    quary = "select * from CONSUMER where USER_ID='" + userId + "'and bookNo ='" + book + "'and conId LIKE'%" + con_id + "%'";
            }
            //Cursor cursor = db.rawQuery(quary, new String[]{userID,con_id});
            Cursor cursor = db.rawQuery(quary, null);
            while (cursor.moveToNext()) {
                Consumer consumer = new Consumer();
                consumer.set_id(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
                consumer.setSubDivId(cursor.getString(cursor.getColumnIndex("subDivId")));
                consumer.setSectionId(cursor.getString(cursor.getColumnIndex("sectionId")));
                consumer.setBookNo(cursor.getString(cursor.getColumnIndex("bookNo")));
                consumer.setConId(cursor.getString(cursor.getColumnIndex("conId")));
                consumer.setActNo(cursor.getString(cursor.getColumnIndex("actNo")));
                consumer.setcName(cursor.getString(cursor.getColumnIndex("cName")));
                consumer.setcAddress(cursor.getString(cursor.getColumnIndex("cAddress")));
                consumer.setTariffId(cursor.getString(cursor.getColumnIndex("tariffId")));
                consumer.setPhase(cursor.getString(cursor.getColumnIndex("phase")));
                consumer.setcLoad(cursor.getString(cursor.getColumnIndex("cLoad")));
                consumer.setfAmount(cursor.getDouble(cursor.getColumnIndex("fAmount")));
                consumer.setDis_conn(cursor.getString(cursor.getColumnIndex("dis_conn")));
                consumer.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                //consumer.setRead_status(cursor.getString(cursor.getColumnIndex("dis_conn")));
                consumerArrayList.add(consumer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return consumerArrayList;
    }

    public ArrayList<Consumer> getAllConsumers(String mjson){
        ArrayList<Consumer> consumers = new ArrayList<>();
        try{
            JSONArray jsonArray=new JSONArray(mjson);
            for (int i=0;i<jsonArray.length();i++){
                Consumer consumer=new Consumer();
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                consumer.setSubDivId(jsonObject.getString("subDivId"));
                consumer.setBookNo(jsonObject.getString("mruName"));
                consumer.setConId(jsonObject.getString("conId"));
                consumer.setTariffId(jsonObject.getString("tariffId"));
                consumer.setPhase(jsonObject.getString("phase"));
                consumer.setcLoad(jsonObject.getString("load"));
                consumer.setReading(jsonObject.getString("reading"));
                consumer.setRead_status(jsonObject.getString("readStatus"));
                consumer.setMobile(jsonObject.getString("mobile"));
                consumer.setRemarks(jsonObject.getString("remarks"));
                consumer.setMeterImage(jsonObject.getString("meterImage"));
                consumer.setRefNo(jsonObject.getString("refNo"));
                consumer.setDate_time(jsonObject.getString("dateTime"));
                consumers.add(consumer);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return consumers;
    }
}