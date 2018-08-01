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

public class TodoUpdateTask
        extends AsyncTask<Void, Integer, ArrayList<TodoData>> {

    private WeakReference<Context> mContextRef;
    private TodoUpdateTaskListener mListener;
    private ArrayList<RequestTodoData> mAddItems;
    private ArrayList<RequestTodoData> mEditItems;

    public TodoUpdateTask(Context context, TodoUpdateTaskListener listener) {
        mContextRef = new WeakReference<>(context);
        setListener(listener);
        mAddItems = new ArrayList<>();
        mEditItems = new ArrayList<>();
    }

    public void setAddData(ArrayList<RequestTodoData> data) {
        mAddItems.addAll(data);
    }
    public void setEditData(ArrayList<RequestTodoData> data) {
        mEditItems.addAll(data);
    }

    @Override
    protected ArrayList<TodoData> doInBackground(Void... params) {

        //TODO remove
        if (mAddItems.size() != 0) {
            try {
                Thread.currentThread();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        ArrayList<TodoData> results = new ArrayList<>();
        TodoTreeDbHelper dbHelper = new TodoTreeDbHelper(mContextRef.get());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues cvTodo = new ContentValues();

        for (int i = 0; i < mEditItems.size(); ++i) {
            RequestTodoData requestTodoData = mEditItems.get(i);
            cvTodo.put(TodoTree.TodoEntry.TODO_NAME, requestTodoData.name);
            cvTodo.put(TodoTree.TodoEntry.SUBJECT_ID, requestTodoData.subject);
            //cvTodo.put(TodoTree.TodoEntry.PARENT, requestTodoData.parent);
            cvTodo.put(TodoTree.TodoEntry.COMPLETE, requestTodoData.complete);
            cvTodo.put(TodoTree.TodoEntry.DUEDATE, requestTodoData.dueDate);
            //cvTodo.put(TodoTree.TodoEntry.CREATED_DATE, requestTodoData.updatedDate);
            cvTodo.put(TodoTree.TodoEntry.LAST_UPDATED_DATE, requestTodoData.updatedDate);
            //TODO like needed?
            String selectionUpdateTodo =
                    TodoTree.TodoEntry._ID + " LIKE " + requestTodoData.id;
            //TODO error check
            db.update(TodoTree.TodoEntry.TABLE_NAME, cvTodo, selectionUpdateTodo, null);

            publishProgress(i + 1, mEditItems.size());
        }

        for (int i = 0; i < mAddItems.size(); ++i) {
            RequestTodoData requestTodoData = mAddItems.get(i);
            cvTodo.put(TodoTree.TodoEntry.TODO_NAME, requestTodoData.name);
            cvTodo.put(TodoTree.TodoEntry.SUBJECT_ID, requestTodoData.subject);
            cvTodo.put(TodoTree.TodoEntry.PARENT, requestTodoData.parent);
            cvTodo.put(TodoTree.TodoEntry.COMPLETE, requestTodoData.complete);
            cvTodo.put(TodoTree.TodoEntry.DUEDATE, requestTodoData.dueDate);
            cvTodo.put(TodoTree.TodoEntry.CREATED_DATE, requestTodoData.updatedDate);
            cvTodo.put(TodoTree.TodoEntry.LAST_UPDATED_DATE, requestTodoData.updatedDate);
            long id = db.insert(TodoTree.TodoEntry.TABLE_NAME, null, cvTodo);
            if (id != TodoData.NON_ID)
                results.add(requestTodoData.createTodo(id));

            publishProgress(i + 1, mAddItems.size());
        }
        db.close();
        return results;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mListener != null)
            mListener.onProgressChanged(values[0], values[1]);
    }

    @Override
    protected void onPostExecute(ArrayList<TodoData> creates) {
        super.onPostExecute(creates);
        Log.d(TodoTree.TAG, "[TodoCreateTask::onPostExecute] created " + creates.size() + ", updated " + mEditItems.size() + " todos");
        if (mListener != null) {
            //debug
            for (RequestTodoData requested : mEditItems)
                Log.d(TodoTree.TAG, "[TodoCreateTask::onPostExecute] updated id " + requested.id);
            mListener.onUpdatedTodo(mEditItems);
            mListener.onCreatedTodo(creates);
        }
    }

    public void setListener(TodoUpdateTaskListener listener) {
        mListener = listener;
    }

    public interface TodoUpdateTaskListener {
        void onProgressChanged(int completed, int max);
        void onCreatedTodo(ArrayList<TodoData> creates);
        void onUpdatedTodo(ArrayList<RequestTodoData> updates);
    }
}
