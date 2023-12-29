package com.bih.nic.reconnectiondisconnection;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.adapters.CategoryListAdapter;

public class CategoryListActivity extends AppCompatActivity {
ListView list_cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        setHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUI();
    }

    private void initUI() {
        list_cat=(ListView)findViewById(R.id.list_cat);
        list_cat.setAdapter(new CategoryListAdapter(CategoryListActivity.this,""));
    }

    private void setHeader() {
        View view=findViewById(R.id.header_catList);
        UIUtility.SetHeader(CategoryListActivity.this,view,getResources().getString(R.string.sel_cat),"N","");
    }
}
