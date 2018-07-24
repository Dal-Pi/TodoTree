package com.kania.todotree.data.QueryTask;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kania.todotree.TodoTree;
import com.kania.todotree.data.RequestTodoData;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoTreeDbHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TodoCreateTask
        extends AsyncTask<RequestTodoData, Integer, ArrayList<TodoData>> {

    private WeakReference<Context> mContextRef;
    private TodoCreateTaskListener mListener;

    public TodoCreateTask(Context context, TodoCreateTaskListener listener) {
        mContextRef = new WeakReference<>(context);
        setListener(listener);
    }

    @Override
    protected ArrayList<TodoData> doInBackground(RequestTodoData... requests) {
        ArrayList<TodoData> results = new ArrayList<>();
        TodoTreeDbHelper dbHelper = new TodoTreeDbHelper(mContextRef.get());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues cvTodo = new ContentValues();
        for (int i = 0; i < requests.length; ++i) {
            cvTodo.put(TodoTree.TodoEntry.TODO_NAME, requests[i].name);
            cvTodo.put(TodoTree.TodoEntry.SUBJECT_ID, requests[i].subject.getId());
            cvTodo.put(TodoTree.TodoEntry.PARENT, requests[i].parent.getId());
            cvTodo.put(TodoTree.TodoEntry.DUEDATE, requests[i].dueDate);
            cvTodo.put(TodoTree.TodoEntry.CREATED_DATE, requests[i].updatedDate);
            cvTodo.put(TodoTree.TodoEntry.LAST_UPDATED_DATE, requests[i].updatedDate);
            long id = db.insert(TodoTree.TodoEntry.TABLE_NAME, null, cvTodo);
            results.add(requests[i].createTodo(id));

            publishProgress(i + 1, requests.length);
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
