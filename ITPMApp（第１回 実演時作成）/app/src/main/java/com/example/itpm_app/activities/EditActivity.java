package com.example.itpm_app.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.itpm_app.databases.ITPMDataOpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.itpm_app.R;

public class EditActivity extends AppCompatActivity {

    private static final String KEY_ID = "key_id";
    private static final String KEY_TITLE = "key_title";

    private EditText mEditText;
    private int mSelectId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSelectId = getIntent().getIntExtra(KEY_ID, -1);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (mSelectId == -1) {
                // 新規
                actionBar.setTitle(R.string.a_new);
            } else {
                // 編集
                actionBar.setTitle(R.string.edit);
            }
        }
        mEditText = findViewById(R.id.title_edit_text);

        String title = getIntent().getStringExtra(KEY_TITLE);
        if (title != null) {
            mEditText.setText(title);
        }

        FloatingActionButton fab = findViewById(R.id.save_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = mEditText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(EditActivity.this, R.string.error_no_title, Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ITPMDataOpenHelper.COLUMN_TITLE, title);
                    SQLiteDatabase db = new ITPMDataOpenHelper(EditActivity.this).getWritableDatabase();
                    if (mSelectId == -1) {
                        db.insert(ITPMDataOpenHelper.TABLE_NAME, null, contentValues);
                    } else {
                        contentValues.put(ITPMDataOpenHelper.COLUMN_ID, mSelectId);
                        db.update(ITPMDataOpenHelper.TABLE_NAME, contentValues, ITPMDataOpenHelper.COLUMN_ID + "=" + mSelectId, null);
                    }
                    db.close();
                    finish();
                }
            }
        });
    }

    public static Intent createIntent(Context context, int id, String title) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_TITLE, title);
        return intent;
    }

}
