package com.kania.todotree.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.ContextCompat;

import com.kania.todotree.R;

import static com.kania.todotree.TodoTree.SubjectEntry;
import static com.kania.todotree.TodoTree.TodoEntry;

/**
 * Created by user on 2016-01-10.
 */
public class TodoTreeDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todotree.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = " NOT NULL";

    private static final String SQL_CREATE_SUBJECT_ENTRIES =
            "CREATE TABLE " + SubjectEntry.TABLE_NAME + " (" +
                    SubjectEntry._ID + " INTEGER PRIMARY KEY," +
                    SubjectEntry.SUBJECT_NAME + TEXT_TYPE + COMMA_SEP +
                    SubjectEntry.COLOR + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_TODO_ENTRIES =
            "CREATE TABLE " + TodoEntry.TABLE_NAME + " (" +
                    TodoEntry._ID + " INTEGER PRIMARY KEY," +
                    TodoEntry.SUBJECT_ID + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.TODO_NAME + TEXT_TYPE + COMMA_SEP +
                    TodoEntry.PARENT + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.COMPLETE + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.DUEDATE + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.CREATED_DATE + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.LAST_UPDATED_DATE + INTEGER_TYPE +
                    " )";

    private static final String SQL_DELETE_SUBJECT_ENTRIES =
            "DROP TABLE IF EXISTS " + SubjectEntry.TABLE_NAME;

    private static final String SQL_DELETE_TODO_ENTRIES =
            "DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME;

    private Context mContext;

    public TodoTreeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createWithDefaultSubject(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SUBJECT_ENTRIES);
        db.execSQL(SQL_DELETE_TODO_ENTRIES);
        onCreate(db);
    }

    private void createWithDefaultSubject(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SUBJECT_ENTRIES);
        ContentValues cvDefaultSubject = new ContentValues();
        cvDefaultSubject.put(SubjectEntry.SUBJECT_NAME,
                mContext.getString(R.string.default_subject_name));
        cvDefaultSubject.put(SubjectEntry.COLOR,
                mContext.getString(R.string.default_subject_color_string));
        db.insert(SubjectEntry.TABLE_NAME, null, cvDefaultSubject);
        db.execSQL(SQL_CREATE_TODO_ENTRIES);
    }
}
