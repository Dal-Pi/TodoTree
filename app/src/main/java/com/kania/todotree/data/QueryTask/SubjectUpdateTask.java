package com.kania.todotree.data.QueryTask;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todotree.R;
import com.kania.todotree.TodoTree;
import com.kania.todotree.data.RequestSubjectData;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoTreeDbHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SubjectUpdateTask
        extends AsyncTask<Void, Integer, ArrayList<SubjectData>> {

    private WeakReference<Context> mContextRef;
    private SubjectCreateTaskListener mListener;
    private ArrayList<RequestSubjectData> mItems;

    private ProgressDialog mProgressDialog;

    public SubjectUpdateTask(Context context, SubjectCreateTaskListener listener) {
        mContextRef = new WeakReference<>(context);
        setListener(listener);
        mItems = new ArrayList<>();
    }

    public void setData(ArrayList<RequestSubjectData> data) {
        mItems.addAll(data);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContextRef.get(), null,
                mContextRef.get().getResources().getString(R.string.dialog_loading_title));
    }

    @Override
    protected ArrayList<SubjectData> doInBackground(Void... params) {

        //TODO remove
        try {
            Thread.currentThread();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mItems == null) return null;
        ArrayList<SubjectData> results = new ArrayList<>();
        ArrayList<RequestSubjectData> requests = mItems;
        TodoTreeDbHelper dbHelper = new TodoTreeDbHelper(mContextRef.get());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues cvSubject= new ContentValues();
        for (int i = 0; i < requests.size(); ++i) {
            RequestSubjectData requestSubjectData = requests.get(i);
            cvSubject.put(TodoTree.SubjectEntry.SUBJECT_NAME, requestSubjectData.name);
            cvSubject.put(TodoTree.SubjectEntry.COLOR, requestSubjectData.color);
            long id = db.insert(TodoTree.SubjectEntry.TABLE_NAME, null, cvSubject);
            results.add(requestSubjectData.createSubject(id));

            publishProgress(i + 1, requests.size());
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
    protected void onPostExecute(ArrayList<SubjectData> updates) {
        super.onPostExecute(updates);
        Log.d(TodoTree.TAG, "[SubjectCreateTask] created " + updates.size() + " subjects");
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        if (mListener != null)
            mListener.onCreatedSubject(updates);
    }

    public void setListener(SubjectCreateTaskListener listener) {
        mListener = listener;
    }

    public interface SubjectCreateTaskListener {
        void onProgressChanged(int completed, int max);
        void onCreatedSubject(ArrayList<SubjectData> updates);
    }
}
