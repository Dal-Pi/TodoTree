package com.kania.todotree.data.QueryTask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todotree.TodoTree;
import com.kania.todotree.data.TodoTreeDbHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TodoDeleteTask extends AsyncTask<Void, Integer, ArrayList<Long>> {

    private WeakReference<Context> mContextRef;
    private TodoDeleteTaskListener mListener;
    private ArrayList<Long> mDeleteItems;

    public TodoDeleteTask(Context context, TodoDeleteTaskListener listener) {
        mContextRef = new WeakReference<>(context);
        setListener(listener);
        mDeleteItems = new ArrayList<>();
    }

    public void setDeleteData(ArrayList<Long> data) {
        mDeleteItems.addAll(data);
    }

    @Override
    protected ArrayList<Long> doInBackground(Void... params) {

        //TODO remove
        if (mDeleteItems.size() != 0) {
            try {
                Thread.currentThread();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Long> results = new ArrayList<>();
        TodoTreeDbHelper dbHelper = new TodoTreeDbHelper(mContextRef.get());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        for (int i = 0; i < mDeleteItems.size(); ++i) {
            //TODO like needed?
            String selectionDeleteTodo =
                    TodoTree.TodoEntry._ID + " LIKE " + mDeleteItems.get(i);
            //TODO error check
            db.delete(TodoTree.TodoEntry.TABLE_NAME, selectionDeleteTodo, null);
            results.add(mDeleteItems.get(i));
            publishProgress(i + 1, mDeleteItems.size());
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
    protected void onPostExecute(ArrayList<Long> deletes) {
        super.onPostExecute(deletes);
        Log.d(TodoTree.TAG, "[TodoDeleteTask] deleted " + deletes.size() + " todos");
        //TODO size check
        if (mListener != null) {
            mListener.onDeletedTodo(deletes);
        }
    }

    public void setListener(TodoDeleteTaskListener listener) {
        mListener = listener;
    }

    public interface TodoDeleteTaskListener {
        void onProgressChanged(int completed, int max);
        void onDeletedTodo(ArrayList<Long> deletes);
    }
}
