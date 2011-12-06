package com.example;

import android.app.Activity;
import android.os.Bundle;

import com.example.sqliteassethelper.v1.R;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		MyDatabase db = new MyDatabase(this);
		db.getWritableDatabase();
		db.close();
	}
}