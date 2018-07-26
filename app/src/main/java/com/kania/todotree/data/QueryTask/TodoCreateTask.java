package com.kania.todotree.data.QueryTask;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todotree.TodoTree;
import com.kania.todotree.data.RequestTodoData;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoTreeDbHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TodoCreateTask
        extends AsyncTask<Void, Integer, ArrayList<TodoData>> {

    private WeakReference<Context> mContextRef;
    private TodoCreateTaskListener mListener;
    private ArrayList<RequestTodoData> mItems;

    public TodoCreateTask(Context context, TodoCreateTaskListener listener) {
        mContextRef = new WeakReference<>(context);
        setListener(listener);
        mItems = new ArrayList<>();
    }

    public void setData(ArrayList<RequestTodoData> data) {
        mItems.addAll(data);
    }

    @Override
    protected ArrayList<TodoData> doInBackground(Void... params) {

        //TODO remove
        try {
            Thread.currentThread();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<TodoData> results = new ArrayList<>();
        ArrayList<RequestTodoData> requests = mItems;
        TodoTreeDbHelper dbHelper = new TodoTreeDbHelper(mContextRef.get());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues cvTodo = new ContentValues();
        for (int i = 0; i < requests.size(); ++i) {
            RequestTodoData requestTodoData = requests.get(i);
            cvTodo.put(TodoTree.TodoEntry.TODO_NAME, requestTodoData.name);
            cvTodo.put(TodoTree.TodoEntry.SUBJECT_ID, requestTodoData.subject);
            cvTodo.put(TodoTree.TodoEntry.PARENT, requestTodoData.parent);
            cvTodo.put(TodoTree.TodoEntry.DUEDATE, requestTodoData.dueDate);
            cvTodo.put(TodoTree.TodoEntry.CREATED_DATE, requestTodoData.updatedDate);
            cvTodo.put(TodoTree.TodoEntry.LAST_UPDATED_DATE, requestTodoData.updatedDate);
            long id = db.insert(TodoTree.TodoEntry.TABLE_NAME, null, cvTodo);
            results.add(requestTodoData.createTodo(id));

            publishProgress(i + 1, requests.size());
        }
        return results;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mListener != null)
            mListener.onProgressChanged(values[0], values[1]);
    }

    @Override
    protected void onPostExecute(ArrayList<TodoData> updates) {
        super.onPostExecute(updates);
        Log.d(TodoTree.TAG, "[TodoCreateTask] created " + updates.size() + " todos");
        if (mListener != null)
            mListener.onCreatedTodo(updates);
    }

    public void setListener(TodoCreateTaskListener listener) {
        mListener = listener;
    }

    public interface TodoCreateTaskListener {
        void onProgressChanged(int completed, int max);
        void onCreatedTodo(ArrayList<TodoData> creates);
    }
}
