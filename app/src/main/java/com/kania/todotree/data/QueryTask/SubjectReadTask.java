package com.kania.todotree.data.QueryTask;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todotree.TodoTree;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoTreeDbHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SubjectReadTask extends AsyncTask<Void, Void, ArrayList<SubjectData>> {

    private WeakReference<Context> mContextRef;
    private SubjectReadTaskListener mListener;

    public SubjectReadTask(Context context, SubjectReadTaskListener listener) {
        mContextRef = new WeakReference<>(context);
        setListener(listener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<SubjectData> doInBackground(Void... params) {
        Log.d(TodoTree.TAG, "start to loading subjects");
        //TODO remove
        try {
            Thread.currentThread();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TodoTree.TAG, "end loading subjects");

        ArrayList<SubjectData> results = new ArrayList<>();
        TodoTreeDbHelper dbHelper = new TodoTreeDbHelper(mContextRef.get());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String[] projection = {
                TodoTree.SubjectEntry._ID,
                TodoTree.SubjectEntry.SUBJECT_NAME,
                TodoTree.SubjectEntry.COLOR,
        };
        Cursor subjectCursor = db.query(TodoTree.SubjectEntry.TABLE_NAME,
                projection, null, null, null, null, null);
        while (subjectCursor.moveToNext()) {
            long id = subjectCursor.getLong(subjectCursor.getColumnIndexOrThrow(TodoTree.SubjectEntry._ID));
            String name = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(TodoTree.SubjectEntry.SUBJECT_NAME));
            String color = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(TodoTree.SubjectEntry.COLOR));

            SubjectData subject = new SubjectData(id, name, color);
            results.add(subject);
        }
        db.close();
        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<SubjectData> results) {
        super.onPostExecute(results);
        Log.d(TodoTree.TAG, "[SubjectReadTask] read " + results.size() + " subjects");
        if (mListener != null)
            mListener.onReadSubject(results);
    }

    public void setListener(SubjectReadTaskListener listener) {
        mListener = listener;
    }

    public interface SubjectReadTaskListener {
        void onReadSubject(ArrayList<SubjectData> results);
    }
}
