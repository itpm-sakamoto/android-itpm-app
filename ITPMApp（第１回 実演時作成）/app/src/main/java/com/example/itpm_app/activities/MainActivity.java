package com.example.itpm_app.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.itpm_app.R;
import com.example.itpm_app.databases.ITPMDataOpenHelper;
import com.example.itpm_app.pojo.TitleDataItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainListAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.insert_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        });

        mListView = findViewById(R.id.main_list);
        // Adapter作成
        mAdapter = new MainListAdapter(this, new ArrayList<TitleDataItem>());

        // Adapterセット
        mListView.setAdapter(mAdapter);

        // ListViewクリック処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TitleDataItem item = (TitleDataItem) adapterView.getItemAtPosition(position);
                Intent intent = EditActivity.createIntent(MainActivity.this, item.getId(), item.getTitle());
                startActivity(intent);
            }
        });

        // ListViewロングクリック処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                TitleDataItem item = (TitleDataItem) adapterView.getItemAtPosition(position);
                SQLiteDatabase db = new ITPMDataOpenHelper(MainActivity.this).getWritableDatabase();
                db.delete(ITPMDataOpenHelper.TABLE_NAME, ITPMDataOpenHelper.COLUMN_ID + "=" + item.getId(), null);
                db.close();
                new AllDataLoadTask().execute();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        new AllDataLoadTask().execute();
    }

    private void displayDataList(List<TitleDataItem> items) {

        // データをクリアする
        mAdapter.clear();

        // データを入れる
        mAdapter.addAll(items);

        // データの変更を通知する
        mAdapter.notifyDataSetChanged();
    }

    private class MainListAdapter extends ArrayAdapter<TitleDataItem> {

        private List<TitleDataItem> dataItemList = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public MainListAdapter(@NonNull Context context, @NonNull List<TitleDataItem> objects) {
            super(context, R.layout.layout_title_item, objects);
            dataItemList = objects;
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            TextView titleText;
            TitleDataItem item = dataItemList.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.layout_title_item, parent, false);
                titleText = convertView.findViewById(R.id.title_text_view);
                convertView.setTag(titleText);
            } else {
                titleText = (TextView) convertView.getTag();
            }

            titleText.setText(item.getTitle());

            return convertView;
        }

        @Nullable
        @Override
        public TitleDataItem getItem(int position) {
            return dataItemList.get(position);
        }

        @Override
        public int getCount() {
            return dataItemList.size();
        }
    }

    private class AllDataLoadTask extends AsyncTask<Void, Void, List<TitleDataItem>> {

        @Override
        protected List<TitleDataItem> doInBackground(Void... voids) {
            List items = new ArrayList<TitleDataItem>();
            // データ読み込み
            SQLiteDatabase db = new ITPMDataOpenHelper(MainActivity.this).getWritableDatabase();
            Cursor cursor = db.query(
                    ITPMDataOpenHelper.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ITPMDataOpenHelper.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(ITPMDataOpenHelper.COLUMN_TITLE));
                items.add(new TitleDataItem(id, title));
            }

            cursor.close();
            db.close();
            return items;
        }

        @Override
        protected void onPostExecute(List<TitleDataItem> itemList) {
            displayDataList(itemList);
        }
    }
}
