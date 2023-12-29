package com.bih.nic.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Book;
import com.bih.nic.reconnectiondisconnection.R;
import com.bih.nic.utilitties.CommonPref;

import java.util.ArrayList;

public class BookListAdapter extends BaseAdapter {
    Activity activity;
    private ArrayList<Book> books;
    LayoutInflater layoutInflater;
    public BookListAdapter(Activity activity,String enter_char){
        this.activity=activity;
        books=new DataBaseHelper(activity).getBooks(CommonPref.getUserDetails(activity).getUserId(),enter_char);
        layoutInflater=activity.getLayoutInflater();
    }
    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view=layoutInflater.inflate(R.layout.book_item,null,false);
        TextView textView=(TextView)view.findViewById(R.id.text_book_item);
        textView.setText(""+books.get(i).getBook_number());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItem(i);
            }
        });
        return view;
    }

    private void onClickItem(int pos){
        Intent intent = new Intent();
        intent.putExtra("book", books.get(pos));
        activity.setResult(activity.RESULT_OK, intent);
        activity.finish();
    }
}
