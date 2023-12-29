package com.bih.nic.reconnectiondisconnection;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.adapters.ConsumerListAdapter;
import com.bih.nic.model.Book;
import com.bih.nic.model.Category;
import com.bih.nic.utilitties.GlobalVariables;

public class ConsumerListActivity extends AppCompatActivity {
    ListView list_consumer;
    EditText editText;
    Book book;
    String char_seq="";
    String amount;
    RelativeLayout ll_left;
    Category category;
    TextView text_no_consumer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_list);
        book = (Book) getIntent().getSerializableExtra("book");
        setHeader();
        text_no_consumer = (TextView) findViewById(R.id.text_no_consumer);
        text_no_consumer.setVisibility(View.GONE);
        ll_left = (RelativeLayout) findViewById(R.id.ll_left);
        list_consumer = (ListView) findViewById(R.id.list_consumer);
        editText = (EditText) findViewById(R.id.et_accountno);
        ll_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.flag=true;
                category=null;
                amount=null;
                Intent intent = new Intent(ConsumerListActivity.this, LocalFilterActivity.class);
                startActivityForResult(intent, 10);
            }
        });
    }

    private void setHeader() {
        View view = findViewById(R.id.header_con_list);
        UIUtility.SetHeader(ConsumerListActivity.this, view, book.getBook_number().trim(), "N","");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!GlobalVariables.flag) {
            GlobalVariables.flag=false;
            list_consumer.setAdapter(new ConsumerListAdapter(ConsumerListActivity.this, "", book.getBook_number().trim(),text_no_consumer));
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    list_consumer.invalidate();
                    list_consumer.setAdapter(new ConsumerListAdapter(ConsumerListActivity.this, charSequence.toString(), book.getBook_number().trim(),text_no_consumer));

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("category")) category = (Category) data.getSerializableExtra("category");
                if (data.hasExtra("amount")) amount = data.getStringExtra("amount");
                list_consumer.invalidate();
                list_consumer.setAdapter(new ConsumerListAdapter(ConsumerListActivity.this, category, amount,book.getBook_number(),char_seq,text_no_consumer));
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        char_seq=charSequence.toString();
                        list_consumer.invalidate();
                        list_consumer.setAdapter(new ConsumerListAdapter(ConsumerListActivity.this, category, amount,book.getBook_number(),char_seq,text_no_consumer));

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        GlobalVariables.flag=false;
        super.onDestroy();
    }
}
