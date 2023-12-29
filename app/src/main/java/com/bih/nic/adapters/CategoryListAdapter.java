package com.bih.nic.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bih.nic.databaseHandler.DataBaseHelper;
import com.bih.nic.model.Category;
import com.bih.nic.reconnectiondisconnection.R;

import java.util.ArrayList;

public class CategoryListAdapter extends BaseAdapter {
    Activity activity;
    private ArrayList<Category> categories;
    LayoutInflater layoutInflater;
    public CategoryListAdapter(Activity activity, String enter_char){
        this.activity=activity;
        categories=new DataBaseHelper(activity).getCategories();
        layoutInflater=activity.getLayoutInflater();
    }
    @Override
    public int getCount() {
        return categories.size();
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
        textView.setText(""+categories.get(i).getTariffId());
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
        intent.putExtra("category", categories.get(pos));
        activity.setResult(activity.RESULT_OK, intent);
        activity.finish();
    }
}
