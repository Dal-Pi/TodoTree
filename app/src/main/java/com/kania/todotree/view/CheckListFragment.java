package com.kania.todotree.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kania.todotree.R;
import com.kania.todotree.data.RequestTodoData;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoProvider;

public class CheckListFragment extends Fragment implements TodoProvider.IDataObserver{
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private TodoItemRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CheckListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CheckListFragment newInstance(int columnCount) {
        CheckListFragment fragment = new CheckListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new TodoItemRecyclerViewAdapter(getContext(),
                    TodoProvider.getInstance().getAllTodo());
            mAdapter.setHasStableIds(true);
            recyclerView.setAdapter(mAdapter);

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TodoProvider.getInstance().attachObserver(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TodoProvider.getInstance().detachObserver(this);
    }

    @Override
    public void onTodoAdded(TodoData added, int position) {
        Log.d("todo_tree", "onTodoAdded() id:" + added.getId() + ", pos:" + position);
        mAdapter.notifyItemChanged(position);
    }
    @Override
    public void onTodoRemoved(TodoData removed, int position) {
        mAdapter.notifyItemChanged(position);
    }
    @Override
    public void onTodoUpdated(RequestTodoData prev, TodoData updated, int position) {
        mAdapter.notifyItemChanged(position);
    }
    @Override
    public void onSubjectAdded(SubjectData added) {
        //TODO
    }
    @Override
    public void onSubjectRemoved(SubjectData removed) {
        //TODO
    }
    @Override
    public void onSubjectUpdated(SubjectData prev, SubjectData updated) {
        //TODO
    }
}
