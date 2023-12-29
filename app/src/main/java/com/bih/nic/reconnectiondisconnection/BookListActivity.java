package com.bih.nic.reconnectiondisconnection;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.adapters.BookListAdapter;

public class BookListActivity extends AppCompatActivity {
ListView list_book;
EditText et_accountno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        setHeader();
        et_accountno=(EditText) findViewById(R.id.et_accountno);
        list_book=(ListView)findViewById(R.id.list_book);
        list_book.setAdapter(new BookListAdapter(BookListActivity.this,""));
        et_accountno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                   list_book.invalidate();
                   list_book.setAdapter(new BookListAdapter(BookListActivity.this,charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void setHeader() {
        View view=findViewById(R.id.header_booklist);
        UIUtility.SetHeader(BookListActivity.this,view,getResources().getString(R.string.sel_book),"","");
    }
}
