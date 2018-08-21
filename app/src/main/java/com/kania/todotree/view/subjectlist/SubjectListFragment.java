package com.kania.todotree.view.subjectlist;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kania.todotree.R;
import com.kania.todotree.TodoTree;
import com.kania.todotree.data.RequestTodoData;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoProvider;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A placeholder fragment containing a simple view.
 */
public class SubjectListFragment extends Fragment
        implements TodoProvider.IDataObserver,
        SubjectListRecyclerViewAdapter.OnSubjectItemActionListener {

    private RecyclerView mSubjectListView;
    private SubjectListRecyclerViewAdapter mAdapter;

    private View mLayoutAll;
    private Button mBtnAll;

    private OnShowingSubjectListener mListener;

    public static SubjectListFragment newInstance() {
        SubjectListFragment fragment = new SubjectListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject_list, container, false);

        mSubjectListView = view.findViewById(R.id.sublistfrag_list);
        mLayoutAll = view.findViewById(R.id.sublistfrag_layout_all);
        mBtnAll = view.findViewById(R.id.sublistfrag_btn_all);

        Context context = view.getContext();
        mSubjectListView.setLayoutManager(
                new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        mAdapter = new SubjectListRecyclerViewAdapter(getContext(),
                TodoProvider.getInstance().getAllSubject());
        mAdapter.setSubjectItemListener(this);
        mSubjectListView.setAdapter(mAdapter);

        decorateAllBtnByShowing(checkAllSubjectShowing());
        mBtnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean bAllShowing = checkAllSubjectShowing();
                TodoProvider.getInstance().setAllSubjectVisibility(!bAllShowing);
                decorateAllBtnByShowing(!bAllShowing);
                mAdapter.notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onSelectAllSubject();
                }
            }
        });

        return view;
    }

    private boolean checkAllSubjectShowing() {
        boolean bAllShowing = true;
        ArrayList<SubjectData> subjects = TodoProvider.getInstance().getAllSubject();
        for (SubjectData subject : subjects) {
            if (subject.isShowing() == false) {
                bAllShowing = false;
                break;
            }
        }
        return bAllShowing;
    }

    private void decorateAllBtnByShowing(boolean bAllShowing) {
        if (bAllShowing) {
            mBtnAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            mLayoutAll.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.all_subject_btn_color));
        } else {
            mBtnAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.all_subject_btn_color));
            mLayoutAll.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShowingSubjectListener) {
            mListener = (OnShowingSubjectListener)context;
        } else {
            Log.e(TodoTree.TAG, "[SubjectListFragment::onAttach] context do not implement OnShowingSubjectListener");
        }
        TodoProvider.getInstance().attachObserver(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        TodoProvider.getInstance().detachObserver(this);
    }

    @Override
    public void onSubjectSelected(long subjectId) {
        decorateAllBtnByShowing(checkAllSubjectShowing());
        if (mListener != null) {
            mListener.onSelectSubject(subjectId);
        }
    }

    @Override
    public void onTodoAdded(ArrayList<TodoData> creates) {
        // do nothing
    }

    @Override
    public void onTodoUpdated(ArrayList<RequestTodoData> origins, ArrayList<TodoData> updates) {
        // do nothing
    }

    @Override
    public void onTodoRemoved(ArrayList<Integer> removePositions, HashSet<Long> parents) {
        // do nothing
    }

    @Override
    public void onShowingTodoRefreshed() {
        // do nothing
    }

    @Override
    public void onSubjectAdded(ArrayList<Long> creates) {
        //TODO handle each todo
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSubjectRemoved(ArrayList<Long> removes) {
        //TODO handle each todo
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSubjectUpdated(ArrayList<Long> updates) {
        //TODO handle each todo
        mAdapter.notifyDataSetChanged();
    }

    public interface OnShowingSubjectListener {
        void onSelectAllSubject();
        void onSelectSubject(long subjectId);
    }
}
