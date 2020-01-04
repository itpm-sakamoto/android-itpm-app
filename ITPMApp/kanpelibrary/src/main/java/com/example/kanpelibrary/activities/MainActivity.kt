package com.example.kanpelibrary.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kanpelibrary.R
import com.example.kanpelibrary.databases.ITPMDataOpenHelper
import com.example.kanpelibrary.pojo.TitleDataItem
import kotlinx.android.synthetic.main.activity_edit.toolbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: MainListAdapter
    private var isActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        isActive = true

        mAdapter = MainListAdapter(this, R.layout.layout_title_item, ArrayList())

        mainList.apply {

            adapter = mAdapter
            setOnItemClickListener { adapterView, _, position, _ ->
                // 画面遷移
                val item = adapterView.getItemAtPosition(position) as TitleDataItem
                val intent = Intent(this@MainActivity, EditActivity::class.java).apply {
                    putExtra(EditActivity.KEY_ID, item.id)
                    putExtra(EditActivity.KEY_TITLE, item.title)
                }
                startActivity(intent)
            }

            setOnItemLongClickListener { adapterView, _, position, _ ->
                // データ削除処理
                val item = adapterView.getItemAtPosition(position) as TitleDataItem
                val db = ITPMDataOpenHelper(this@MainActivity).writableDatabase
                db.delete(ITPMDataOpenHelper.TABLE_NAME, ITPMDataOpenHelper._ID + "=" + item.id, null)
                db.close()
                AllDataLoadTask().execute()
                true
            }
        }

        insertButton.setOnClickListener {
            // 画面遷移
            startActivity(Intent(this@MainActivity, EditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        isActive = true
        // データ読込処理
        AllDataLoadTask().execute()
    }

    override fun onPause() {
        isActive = false
        super.onPause()
    }

    /**
     * 非同期処理
     */
    private inner class AllDataLoadTask: AsyncTask<Void, Void, List<TitleDataItem>>() {

        override fun doInBackground(vararg p0: Void?): List<TitleDataItem> {
            val itemList = mutableListOf<TitleDataItem>()

            val db = ITPMDataOpenHelper(this@MainActivity).writableDatabase

            val cursor = db.query(
                ITPMDataOpenHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(ITPMDataOpenHelper._ID))
                val title = cursor.getString(cursor.getColumnIndex(ITPMDataOpenHelper.COLUMN_TITLE))
                itemList.add(TitleDataItem(id, title))
            }

            cursor.close()
            db.close()

            return itemList
        }

        override fun onPostExecute(result: List<TitleDataItem>?) {
            // データの表示処理へ
            if (isActive) result?.let { displayDataList(result) }
        }
    }

    private fun displayDataList(titleDataList: List<TitleDataItem>) {
        // リスト表示処理
        mAdapter.clear()

        mAdapter.addAll(titleDataList)

        mAdapter.notifyDataSetChanged()
    }

    /**
     * ListViewアダプター
     */
    private inner class MainListAdapter(context: Context, val resource: Int, val dataList: List<TitleDataItem> = mutableListOf<TitleDataItem>().toList())
        : ArrayAdapter<TitleDataItem>(context, resource, dataList) {

        private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

        override fun getItem(position: Int) = dataList[position]

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            var textView: TextView
            return if (view == null) {
                view = layoutInflater.inflate(resource, null).apply {
                    textView = findViewById(R.id.titleTextView)
                    tag = textView
                    textView.text = dataList[position].title
                }
                view
            } else {
                textView = view.tag as TextView
                textView.text = dataList[position].title
                view
            }
        }

        override fun getCount() = dataList.size
    }
}
