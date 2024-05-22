package com.example.qlsv;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText edtmalop, edttenlop, edtsiso;
    Button btnnhap, btnxoa, btncapnhat;
    ListView lv;
    ArrayList<String> myList;
    ArrayAdapter<String> myadapter;
    SQLiteDatabase mydatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtmalop = findViewById(R.id.edtmalop);
        edttenlop = findViewById(R.id.edttenlop);
        edtsiso = findViewById(R.id.edtsiso);
        btnnhap = findViewById(R.id.btnnhap);
        btncapnhat = findViewById(R.id.btncapnhat);
        btnxoa = findViewById(R.id.btnxoa);
        lv = findViewById(R.id.lv);

        myList = new ArrayList<>();
        myadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        lv.setAdapter(myadapter);

        mydatabase = openOrCreateDatabase("qlsinhvien.db", MODE_PRIVATE, null);

        try {
            String sql = "CREATE TABLE IF NOT EXISTS tbllop(malop TEXT PRIMARY KEY, tenlop TEXT, siso INTEGER)";
            mydatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("Error", "Table creation failed", e);
        }

        btnnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertRecord();
            }
        });

        btnxoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecord();
            }
        });

        btncapnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecord();
            }
        });

        updateListView(); // Initial call to populate the ListView
    }

    private void insertRecord() {
        try {
            String malop = edtmalop.getText().toString();
            String tenlop = edttenlop.getText().toString();
            int siso = Integer.parseInt(edtsiso.getText().toString());

            ContentValues myvalue = new ContentValues();
            myvalue.put("malop", malop);
            myvalue.put("tenlop", tenlop);
            myvalue.put("siso", siso);

            long result = mydatabase.insert("tbllop", null, myvalue);
            String msg = (result == -1) ? "Fail to Insert Record!" : "Insert record Successfully";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            Log.d("Insert", msg);
            updateListView(); // Update ListView after insert
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
            Log.e("Insert Error", "Invalid number format", e);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Insert operation failed", Toast.LENGTH_SHORT).show();
            Log.e("Insert Error", "Insert operation failed", e);
        }
    }

    private void deleteRecord() {
        try {
            String malop = edtmalop.getText().toString();
            int n = mydatabase.delete("tbllop", "malop = ?", new String[]{malop});

            String msg = (n == 0) ? "No record to Delete" : n + " record(s) deleted";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            Log.d("Delete", msg);
            updateListView(); // Update ListView after delete
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Delete operation failed", Toast.LENGTH_SHORT).show();
            Log.e("Delete Error", "Delete operation failed", e);
        }
    }

    private void updateRecord() {
        try {
            int siso = Integer.parseInt(edtsiso.getText().toString());
            String malop = edtmalop.getText().toString();

            ContentValues myvalue = new ContentValues();
            myvalue.put("siso", siso);

            int n = mydatabase.update("tbllop", myvalue, "malop = ?", new String[]{malop});

            String msg = (n == 0) ? "No record to Update" : n + " record(s) Updated";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            Log.d("Update", msg);
            updateListView(); // Update ListView after update
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
            Log.e("Update Error", "Invalid number format", e);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Update operation failed", Toast.LENGTH_SHORT).show();
            Log.e("Update Error", "Update operation failed", e);
        }
    }

    private void updateListView() {
        myList.clear();
        Cursor cursor = mydatabase.rawQuery("SELECT * FROM tbllop", null);
        if (cursor.moveToFirst()) {
            do {
                String malop = cursor.getString(0);
                String tenlop = cursor.getString(1);
                int siso = cursor.getInt(2);
                myList.add(malop + " - " + tenlop + " - " + siso);
            } while (cursor.moveToNext());
        }
        cursor.close();
        myadapter.notifyDataSetChanged();
    }
}
