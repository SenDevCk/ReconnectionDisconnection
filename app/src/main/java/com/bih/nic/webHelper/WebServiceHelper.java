package com.bih.nic.webHelper;


import com.bih.nic.model.Book;
import com.bih.nic.model.Category;
import com.bih.nic.model.Consumer;
import com.bih.nic.model.ReportEntity;
import com.bih.nic.model.UserInfo2;
import com.bih.nic.model.Versioninfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chandan Singh on 08-01-2019.
 */
public class WebServiceHelper {

    public static Versioninfo CheckVersion(String res) {
        Versioninfo versioninfo = null;
        try {
            JSONArray jsonArray=new JSONArray(res);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            versioninfo = new Versioninfo();
            versioninfo.setAdminMsg(jsonObject.getString("adminMsg"));
            versioninfo.setAdminTitle(jsonObject.getString("adminTitle"));
            versioninfo.setUpdateMsg(jsonObject.getString("updateMsg"));
            versioninfo.setUpdateTile(jsonObject.getString("updateTitle"));
            versioninfo.setAppUrl(jsonObject.getString("appUrl"));
            versioninfo.setRole(jsonObject.getString("role"));
            versioninfo.setAppversion(jsonObject.getString("version"));
            versioninfo.setPriority(jsonObject.getString("priority"));
            //versioninfo.setVerUpdated(jsonObject.getBoolean("isUpdated"));
            //versioninfo.setValidDevice(jsonObject.getBoolean("isValidDev"));
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return versioninfo;
    }

    public static UserInfo2 loginParser(String res) {
        UserInfo2 userInfo2 = null;
        try {
            JSONArray jsonArray=new JSONArray(res);
            if (jsonArray.length()>0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                userInfo2 = new UserInfo2();
                if (jsonObject.has("userId")) userInfo2.setUserId(jsonObject.getString("userId"));
                if (jsonObject.has("password"))
                    userInfo2.setPassword(jsonObject.getString("password"));
                if (jsonObject.has("userRole"))
                    userInfo2.setUserRole(jsonObject.getString("userRole"));
                if (jsonObject.has("staffId"))
                    userInfo2.setStaffId(jsonObject.getString("staffId"));
                if (jsonObject.has("staffName"))
                    userInfo2.setStaffName(jsonObject.getString("staffName"));
                if (jsonObject.has("designId"))
                    userInfo2.setDesignId(jsonObject.getString("designId"));
                if (jsonObject.has("fromDate"))
                    userInfo2.setFromDate(jsonObject.getString("fromDate"));
                if (jsonObject.has("subDivId"))
                    userInfo2.setSubDivId(jsonObject.getString("subDivId"));
                if (jsonObject.has("subDivName"))
                    userInfo2.setSubDivName(jsonObject.getString("subDivName"));
                if (jsonObject.has("locType"))
                    userInfo2.setLocType(jsonObject.getString("locType"));
                if (jsonObject.has("activeFlag"))
                    userInfo2.setActiveFlag(jsonObject.getString("activeFlag"));
                if (jsonObject.has("contactNo"))
                    userInfo2.setContactNo(jsonObject.getString("contactNo"));
                if (jsonObject.has("dateTime"))
                    userInfo2.setDateTime(jsonObject.getString("dateTime"));
                if (jsonObject.has("enterBy"))
                    userInfo2.setEnterBy(jsonObject.getString("enterBy"));
                if (jsonObject.has("imeiNumber"))
                    userInfo2.setImeiNumber(jsonObject.getString("imeiNumber"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return userInfo2;
    }


    public static ArrayList<Book> bookParser(String res) {
        ArrayList<Book> bookArrayList=new ArrayList<>();
         try{
             JSONArray jsonArray=new JSONArray(res);
             if (jsonArray.length()>0){
                 for (int i=0;i<jsonArray.length();i++) {
                     JSONObject jsonObject = jsonArray.getJSONObject(i);
                     Book book=new Book();
                     book.setBook_number(jsonObject.getString("bookName"));
                     bookArrayList.add(book);
                 }
             }
         }catch (Exception e){
             e.printStackTrace();
         }
        return bookArrayList;
    }

    public static ArrayList<Consumer> consumerParser(String res) {
        ArrayList<Consumer> consumerArrayList=new ArrayList<>();
        try{
            JSONArray jsonArray=new JSONArray(res);
            if (jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Consumer consumer=new Consumer();
                    if (jsonObject.has("subDivId")) consumer.setSubDivId(jsonObject.getString("subDivId"));
                    if (jsonObject.has("sectionId")) consumer.setSectionId(jsonObject.getString("sectionId"));
                    if (jsonObject.has("bookNo")) consumer.setBookNo(jsonObject.getString("bookNo"));
                    if (jsonObject.has("conId")) consumer.setConId(jsonObject.getString("conId"));
                    if (jsonObject.has("actNo")) consumer.setActNo(jsonObject.getString("actNo"));
                    if (jsonObject.has("cName")) consumer.setcName(jsonObject.getString("cName"));
                    if (jsonObject.has("cAddress")) consumer.setcAddress(jsonObject.getString("cAddress"));
                    if (jsonObject.has("tariffId")) consumer.setTariffId(jsonObject.getString("tariffId"));
                    if (jsonObject.has("phase")) consumer.setPhase(jsonObject.getString("phase"));
                    if (jsonObject.has("cLoad")) consumer.setcLoad(jsonObject.getString("cLoad"));
                    if (jsonObject.has("fAmount")) consumer.setfAmount(Double.parseDouble(jsonObject.getString("fAmount")));
                    if (jsonObject.has("telefoneNo")) consumer.setMobile(jsonObject.getString("telefoneNo"));
                    else consumer.setMobile("NA");
                    consumerArrayList.add(consumer);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return consumerArrayList;
    }

    public static ArrayList<Category> categoriesParser(String res) {
        ArrayList<Category> categoriesArrayList=new ArrayList<>();
        try{
            JSONArray jsonArray=new JSONArray(res);
            if (jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Category categories=new Category();
                    if (jsonObject.has("tariffId"))categories.setTariffId(jsonObject.getString("tariffId"));
                    categoriesArrayList.add(categories);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return categoriesArrayList;
    }

    public static ArrayList<ReportEntity> reportParser(String res) {
        ArrayList<ReportEntity> reportEntities=new ArrayList<>();
        try{
            JSONArray jsonArray=new JSONArray(res);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                ReportEntity reportEntity=new ReportEntity();
                if (jsonObject.has("param5")) reportEntity.setDate(jsonObject.getString("param5"));
                if (jsonObject.has("param3")) reportEntity.setSubdiv(jsonObject.getString("param3"));
                if (jsonObject.has("param4")) reportEntity.setCount(jsonObject.getString("param4"));
                reportEntities.add(reportEntity);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return reportEntities;
    }
}
