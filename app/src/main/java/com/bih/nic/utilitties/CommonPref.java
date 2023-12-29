package com.bih.nic.utilitties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bih.nic.model.UserInfo2;


/**
 * Created by CKS on 15/06/2018.
 */
public class CommonPref {

	static Context context;

	CommonPref() {

	}

	CommonPref(Context context) {
		CommonPref.context = context;
	}

	public static void setUserDetails(Context context, UserInfo2 UserInfo2) {

		String key = "_USER_DETAILS";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("userId", UserInfo2.getUserId());
		editor.putString("password", UserInfo2.getPassword());
		editor.putString("userRole", UserInfo2.getPassword());
		editor.putString("staffId", UserInfo2.getStaffId());
		editor.putString("staffName", UserInfo2.getStaffName());
		editor.putString("designId", UserInfo2.getDesignId());
		editor.putString("fromDate", UserInfo2.getFromDate());
		editor.putString("subDivId", UserInfo2.getSubDivId());
		editor.putString("subDivName", UserInfo2.getSubDivName());
		editor.putString("locType", UserInfo2.getLocType());
		editor.putString("activeFlag", UserInfo2.getActiveFlag());
		editor.putString("contactNo", UserInfo2.getContactNo());
		editor.putString("dateTime", UserInfo2.getDateTime());
		editor.putString("enterBy", UserInfo2.getEnterBy());
		editor.putString("imeiNumber", UserInfo2.getImeiNumber());
		editor.commit();
	}

	public static UserInfo2 getUserDetails(Context context) {
		String key = "_USER_DETAILS";
		UserInfo2 UserInfo2 = new UserInfo2();
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		UserInfo2.setUserId(prefs.getString("userId", ""));
		UserInfo2.setPassword(prefs.getString("password", ""));
		UserInfo2.setUserRole(prefs.getString("userRole", ""));
		UserInfo2.setStaffId(prefs.getString("staffId", ""));
		UserInfo2.setStaffName(prefs.getString("staffName", ""));
		UserInfo2.setDesignId(prefs.getString("designId", ""));
		UserInfo2.setFromDate(prefs.getString("fromDate", ""));
		UserInfo2.setSubDivId(prefs.getString("subDivId", ""));
		UserInfo2.setSubDivName(prefs.getString("subDivName", ""));
		UserInfo2.setLocType(prefs.getString("locType", ""));
		UserInfo2.setActiveFlag(prefs.getString("activeFlag", ""));
		UserInfo2.setContactNo(prefs.getString("contactNo", ""));
		UserInfo2.setDateTime(prefs.getString("dateTime", ""));
		UserInfo2.setEnterBy(prefs.getString("enterBy", ""));
		UserInfo2.setImeiNumber(prefs.getString("imeiNumber", ""));
		return UserInfo2;
	}

	public static void setCheckUpdate(Context context, long dateTime) {

		String key = "_CheckUpdate";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		Editor editor = prefs.edit();

		dateTime = dateTime + 1 * 3600000;
		editor.putLong("LastVisitedDate", dateTime);

		editor.commit();

	}
	public static void setPrinterMacAddress(Context context, String address) {

		String key = "_MAC_ADDRESS";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		Editor editor = prefs.edit();

		editor.putString("MacAddress", address);
		editor.commit();

	}

	public static String getPrinterMacAddress(Context context) {

		String key = "_MAC_ADDRESS";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		String macAddress = prefs.getString("MacAddress", "");
		return macAddress;
	}

	public static void setPrinterType(Context context, String address) {

		String key = "P_Type";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		Editor editor = prefs.edit();

		editor.putString("PType", address);
		editor.commit();

	}

	public static String getPrinterType(Context context) {

		String key = "P_Type";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		String Ptype = prefs.getString("PType", "");
		return Ptype;
	}
}
