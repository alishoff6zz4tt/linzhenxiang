package com.keyboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.key.KeyboardRelativeLayout;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    EditText editText;
    KeyboardRelativeLayout mKeyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.main_edit);
        mKeyLayout = (KeyboardRelativeLayout) findViewById(R.id.mian_key_layout);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.addTextChangedListener(this);
        mKeyLayout.setUpWithEditText(editText);
        mKeyLayout.setActivity(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
     mKeyLayout.afterTextChanged(s);
    }
}
