package com.example;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class MyDatabase extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "northwind";
	private static final int DATABASE_VERSION = 2;

	public MyDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
		//setForcedUpgradeVersion(2);
	}

	public Cursor getEmployees() {

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"0 _id", "FullName"}; 
		String sqlTables = "Employees";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null,
				null, null, null);

		c.moveToFirst();
		return c;

	}

}
