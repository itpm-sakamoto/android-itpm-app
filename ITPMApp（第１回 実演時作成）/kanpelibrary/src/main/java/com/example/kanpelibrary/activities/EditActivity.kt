package com.example.kanpelibrary.activities

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kanpelibrary.R
import com.example.kanpelibrary.databases.ITPMDataOpenHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {

    companion object {
        const val KEY_ID = "key_id"
        const val KEY_TITLE = "key_title"
    }

    private var selectId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)

        selectId = intent.getIntExtra(KEY_ID, -1)
        val title = intent.getStringExtra(KEY_TITLE)
        titleEditText.setText(title)

        when (selectId) {
            -1 -> supportActionBar?.title = getString(R.string.a_new)
            else -> supportActionBar?.title = getString(R.string.edit)
        }

        saveButton.setOnClickListener {
            if (titleEditText.text.isEmpty()) {
                // エラー表示
                val inputErrorBar = Snackbar.make(findViewById<View>(R.id.rootContainer), R.string.error_no_title, Snackbar.LENGTH_SHORT)
                inputErrorBar.show()
            } else {
                val contentValues = ContentValues()
                if (selectId != -1) {
                    contentValues.put(ITPMDataOpenHelper._ID, selectId)
                }
                contentValues.put(ITPMDataOpenHelper.COLUMN_TITLE, titleEditText.text.toString())
                val db = ITPMDataOpenHelper(this@EditActivity).writableDatabase
                if (selectId == -1) {
                    // 新規追加
                    db.insert(ITPMDataOpenHelper.TABLE_NAME, null, contentValues)
                } else {
                    // 更新処理
                    db.update(ITPMDataOpenHelper.TABLE_NAME, contentValues, ITPMDataOpenHelper._ID + "=" + selectId, null)
                }
                db.close()
                finish()
            }
        }
    }

}
