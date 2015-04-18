package edu.nyu.scps.cursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by danielessien on 4/17/15.
 */
public class Helper extends SQLiteOpenHelper {

    public Helper(Context context) {
        super(context, "stooges.db", null, 1);	//1 is version number
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //a Java array of five Strings containing five SQLite statements.
        String[] statements = {
                "CREATE TABLE people ("
                        + "	_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "	name TEXT"
                        + ");",

                "INSERT INTO people (_id, name, phone) VALUES (NULL, 'Moe', '');",
                "INSERT INTO people (_id, name, phone) VALUES (NULL, 'Larry', '');",
                "INSERT INTO people (_id, name, phone) VALUES (NULL, 'Curly', '');",
                "INSERT INTO people (_id, name, phone) VALUES (NULL, 'Shemp', '');"
        };

        for (String statement: statements) {
            db.execSQL(statement);
        }
    }

    public Cursor getCursor() {
        SQLiteDatabase db = getReadableDatabase(); // the db passed to onCreate
        //can say "_id, name" instead of "*", but _id must be included.
        Cursor cursor = db.rawQuery("SELECT * FROM people;", null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor sortByName() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM people ORDER BY name;", null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor sortById() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM people ORDER BY _id;", null);
        cursor.moveToFirst();
        return cursor;
    }

    public void addContact(String name, String phoneNumber){
        SQLiteDatabase db = getReadableDatabase();
        Cursor subCursor = db.rawQuery("SELECT * FROM people;", null);
        int column_idx = subCursor.getColumnIndex("phone");
        if(column_idx >= 0){
            String query = "Select * from people where phone like '%" + phoneNumber + "%'";
            subCursor = db.rawQuery(query, null);
            if(subCursor.getCount() <= 0){
                String insert = ("INSERT INTO people (_id, name, phone) VALUES (NULL,'"+name+"','"+phoneNumber+"');");
                db.execSQL(insert);
            }
        }
        else{
            String query = "Select * from people where name like '%" + name + "%'";
            subCursor = db.rawQuery(query, null);
            if(subCursor.getCount() <= 0){
                String[] statements = {
                        "ALTER TABLE people ADD COLUMN phone TEXT",
                        "INSERT INTO people (_id, name, phone) VALUES (NULL,'"+name+"','"+phoneNumber+"');"
                };
                for (String statement: statements) {
                    db.execSQL(statement);
                }
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
