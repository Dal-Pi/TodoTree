package com.kania.todotree.data.QueryTask;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kania.todotree.TodoTree;
import com.kania.todotree.data.ITodoProvider;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoTreeDbHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TodoReadTask
        extends AsyncTask<Void, Void, ArrayList<TodoData>> {

    private WeakReference<Context> mContextRef;
    private ITodoProvider mProvider;
    private TodoReadTaskListener mListener;

    public TodoReadTask(Context context, ITodoProvider provider, TodoReadTaskListener listener) {
        mContextRef = new WeakReference<>(context);
        mProvider = provider;
        setListener(listener);
    }

    @Override
    protected ArrayList<TodoData> doInBackground(Void... params) {
        ArrayList<TodoData> results = new ArrayList<>();
        TodoTreeDbHelper dbHelper = new TodoTreeDbHelper(mContextRef.get());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String[] projection = {
                TodoTree.TodoEntry._ID,
                TodoTree.TodoEntry.TODO_NAME,
                TodoTree.TodoEntry.SUBJECT_ID,
                TodoTree.TodoEntry.PARENT,
                TodoTree.TodoEntry.DUEDATE,
                TodoTree.TodoEntry.COMPLETE,
                TodoTree.TodoEntry.CREATED_DATE,
                TodoTree.TodoEntry.LAST_UPDATED_DATE
        };
        Cursor todoCursor = db.query(TodoTree.TodoEntry.TABLE_NAME,
                projection, null, null, null, null, null);
        while (todoCursor.moveToNext()) {
            long id = todoCursor.getLong(todoCursor.getColumnIndexOrThrow(TodoTree.TodoEntry._ID));
            String name = todoCursor.getString(todoCursor.getColumnIndexOrThrow(TodoTree.TodoEntry.TODO_NAME));
            long subject = todoCursor.getLong(todoCursor.getColumnIndexOrThrow(TodoTree.TodoEntry.SUBJECT_ID));
            long parent = todoCursor.getLong(todoCursor.getColumnIndexOrThrow(TodoTree.TodoEntry.PARENT));
            long duedate = todoCursor.getLong(todoCursor.getColumnIndexOrThrow(TodoTree.TodoEntry.DUEDATE));
            boolean complete = (0 < todoCursor.getInt(todoCursor.getColumnIndexOrThrow(TodoTree.TodoEntry.COMPLETE)));
            long created = todoCursor.getLong(todoCursor.getColumnIndexOrThrow(TodoTree.TodoEntry.CREATED_DATE));
            long updated = todoCursor.getLong(todoCursor.getColumnIndexOrThrow(TodoTree.TodoEntry.LAST_UPDATED_DATE));

            TodoData todo = new TodoData(id, subject, name, parent, complete, duedate, created, updated);
            results.add(todo);
        }
        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<TodoData> results) {
        super.onPostExecute(results);
        if (mListener != null)
            mListener.onReadTodo(results);
    }

    public void setListener(TodoReadTaskListener listener) {
        mListener = listener;
    }

    public interface TodoReadTaskListener {
        void onReadTodo(ArrayList<TodoData> creates);
    }
}
