package com.bih.nic.asyncTask;

import android.os.AsyncTask;

import com.bih.nic.model.Versioninfo;

public class LoadVersion extends AsyncTask<String,Void, Versioninfo> {
    @Override
    protected Versioninfo doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(Versioninfo versioninfo) {
        super.onPostExecute(versioninfo);

    }
}
