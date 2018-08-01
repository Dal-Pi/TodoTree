package com.kania.todotree.view;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kania.todotree.R;
import com.kania.todotree.TodoTree;
import com.kania.todotree.data.RequestTodoData;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CheckListFragment extends Fragment
        implements TodoProvider.IDataObserver,
        TodoItemRecyclerViewAdapter.OnTodoItemActionListener {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private TodoItemRecyclerViewAdapter mAdapter;

    private FloatingActionButton mFab;

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
        View view = inflater.inflate(R.layout.fargment_todo_list, container, false);

        RecyclerView checkListView = view.findViewById(R.id.frag_list_check);
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            checkListView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            checkListView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mAdapter = new TodoItemRecyclerViewAdapter(getContext(),
                TodoProvider.getInstance().getAllTodo());
        mAdapter.setHasStableIds(true);
        mAdapter.attachSelectListener(this);
        checkListView.setAdapter(mAdapter);
        checkListView.setHasFixedSize(true);

        mFab = view.findViewById(R.id.frag_fab_add_todo);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTodoDialog(TodoData.NON_ID);
            }
        });
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
    public void onTodoAdded(ArrayList<TodoData> creates) {
        TodoProvider provider = TodoProvider.getInstance();
        for (TodoData added : creates) {
            if (added.isRootTodo() == false) {
                TodoData parent = provider.getTodo(added.getParent());
                int parentPos = provider.getIndex(parent);
                mAdapter.notifyItemChanged(parentPos);
            }
            int pos = provider.getIndex(added);
            Log.d(TodoTree.TAG, "[CheckListFragment::onTodoAdded] id:" + added + ", pos:" + pos);
            if (pos >= 0)
                mAdapter.notifyItemInserted(pos);
        }
    }
    @Override
    public void onTodoRemoved(ArrayList<Integer> removePositions, HashSet<Long> parents) {
        TodoProvider provider = TodoProvider.getInstance();
        for (int removePos : removePositions) {
            mAdapter.notifyItemRemoved(removePos);
            Log.d(TodoTree.TAG, "onTodoRemoved(), pos:" + removePos);
        }
        for (long parent : parents) {
            TodoData todo = provider.getTodo(parent);
            int pos = provider.getIndex(todo);
            mAdapter.notifyItemChanged(pos);
        }
    }
    @Override
    public void onTodoUpdated(ArrayList<RequestTodoData> origins, ArrayList<TodoData> updates) {
        TodoProvider provider = TodoProvider.getInstance();
        HashSet<Long> updateSet = new HashSet<>();
        for (TodoData updated : updates) {
            updateSet.add(updated.getId());
            if (updated.isRootTodo() == false)
                updateSet.add(updated.getParent());
            updateSet.addAll(updated.getChildren());
        }
        for (long updated : updateSet) {
            TodoData todo = provider.getTodo(updated);
            int pos = provider.getIndex(todo);
            Log.d(TodoTree.TAG, "[CheckListFragment::onTodoUpdated] id:" + updated + ", pos:" + pos);
            mAdapter.notifyItemChanged(pos);
        }
    }

    @Override
    public void onSubjectAdded(ArrayList<Long> creates) {
        //TODO
    }
    @Override
    public void onSubjectRemoved(ArrayList<Long> removes) {
        //TODO
    }
    @Override
    public void onSubjectUpdated(ArrayList<Long> updates) {
        //TODO
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelectTodo(long id) {
        if (mFab != null) {
            mFab.setVisibility(id == TodoData.NON_ID ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onSelectEditTodo(long id) {
        showEditTodoDialog(id);
    }

    private void showEditTodoDialog(long todoId) {
        DialogFragment editTodoDialog = EditTodoDialog.newInstance(todoId);
        editTodoDialog.show(getActivity().getSupportFragmentManager(),
                EditTodoDialog.class.getName());
    }
}
