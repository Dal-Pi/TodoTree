package com.kania.todotree.data.QueryTask;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todotree.TodoTree;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoTreeDbHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TodoReadTask extends AsyncTask<Void, Void, ArrayList<TodoData>> {

    private WeakReference<Context> mContextRef;
    private TodoReadTaskListener mListener;

    public TodoReadTask(Context context, TodoReadTaskListener listener) {
        mContextRef = new WeakReference<>(context);
        setListener(listener);
    }

    @Override
    protected ArrayList<TodoData> doInBackground(Void... params) {
        Log.d(TodoTree.TAG, "start to loading todos");
        //TODO remove
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TodoTree.TAG, "end loading todos");

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
        db.close();
        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<TodoData> results) {
        super.onPostExecute(results);
        Log.d(TodoTree.TAG, "[TodoReadTask] read " + results.size() + " todos");
        if (mListener != null)
            mListener.onReadTodo(results);
    }

    public void setListener(TodoReadTaskListener listener) {
        mListener = listener;
    }

    public interface TodoReadTaskListener {
        void onReadTodo(ArrayList<TodoData> results);
    }
}
