package com.kania.todotree.view.todolist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.inputmethod.InputMethodManager;

import com.kania.todotree.R;
import com.kania.todotree.TodoTree;
import com.kania.todotree.data.RequestTodoData;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoProvider;
import com.kania.todotree.view.common.EditTodoDialog;

import java.util.ArrayList;
import java.util.HashSet;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CheckListFragment extends Fragment
        implements TodoProvider.IDataObserver,
        TodoItemRecyclerViewAdapter.OnTodoItemActionListener {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_SELECTED_TODO = "selected_todo_id";
    private static final String ARG_SELECTED_SUBJECT = "selected_subject_id";
    private int mColumnCount = 1;

    private RecyclerView mCheckListView;
    private TodoItemRecyclerViewAdapter mAdapter;

    private FloatingActionButton mFab;

    private long mSelectedShowingSubjectId;

    public CheckListFragment() {
        mSelectedShowingSubjectId = SubjectData.NON_ID;
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

        mCheckListView = view.findViewById(R.id.frag_list_check);
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            mCheckListView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mCheckListView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mAdapter = new TodoItemRecyclerViewAdapter(getContext(),
                TodoProvider.getInstance().getShowingTodoList());
        mAdapter.setHasStableIds(true);
        mAdapter.attachSelectListener(this);
        //TODO make method
        if (savedInstanceState != null) {
            long selectedId = savedInstanceState.getLong(ARG_SELECTED_TODO, TodoData.NON_ID);
            mAdapter.setSelectedIdForInit(selectedId);
            mSelectedShowingSubjectId =
                    savedInstanceState.getLong(ARG_SELECTED_SUBJECT, SubjectData.NON_ID);
        }
        mCheckListView.setAdapter(mAdapter);
        mCheckListView.setHasFixedSize(true);

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
        //debug
        Log.d(TodoTree.TAG, "[CheckListFragment::onAttach] attached frag : " + this.hashCode());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TodoProvider.getInstance().detachObserver(this);
        //debug
        Log.d(TodoTree.TAG, "[CheckListFragment::onDetach] detached frag : " + this.hashCode());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_SELECTED_TODO, mAdapter.getSelectedId());
        outState.putLong(ARG_SELECTED_SUBJECT, mSelectedShowingSubjectId);
    }

    @Override
    public void onTodoAdded(ArrayList<TodoData> creates) {
        cancelSelectIfSelected();
        TodoProvider provider = TodoProvider.getInstance();
        for (TodoData added : creates) {
            if (added.isRootTodo() == false) {
                TodoData parent = provider.getTodo(added.getParent());
                int parentPos = provider.getIndex(parent);
                mAdapter.notifyItemChanged(parentPos);
            }
            int pos = provider.getIndex(added);
            Log.d(TodoTree.TAG, "[CheckListFragment::onTodoAdded] id:" + added + ", pos:" + pos);
            if (pos >= 0) {
                if (pos == 0)
                    mCheckListView.getLayoutManager().scrollToPosition(pos);
                mAdapter.notifyItemInserted(pos);
            }
        }
        hideInputMethod();
    }
    @Override
    public void onTodoRemoved(ArrayList<Integer> removePositions, HashSet<Long> parents) {
        cancelSelectIfSelected();
        TodoProvider provider = TodoProvider.getInstance();
        for (int removePos : removePositions) {
            mAdapter.notifyItemRemoved(removePos);
            Log.d(TodoTree.TAG, "[CheckListFragment::onTodoRemoved] pos : " + removePos);
        }
        //debug
        Log.d(TodoTree.TAG, "[CheckListFragment::onTodoRemoved] parents size : " + parents.size());
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
        hideInputMethod();
    }

    @Override
    public void onShowingTodoRefreshed() {
        mAdapter.notifyDataSetChanged();
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
    }

    @Override
    public void onSelectTodo(long id) {
        if (mFab != null) {
            mFab.setVisibility(id == TodoData.NON_ID ? View.VISIBLE : View.GONE);
        }
        hideInputMethod();
    }

    @Override
    public void onSelectEditTodo(long id) {
        showEditTodoDialog(id);
    }

    private void showEditTodoDialog(long todoId) {
        DialogFragment editTodoDialog =
                EditTodoDialog.newInstance(todoId, mSelectedShowingSubjectId);
        editTodoDialog.show(getActivity().getSupportFragmentManager(),
                EditTodoDialog.class.getName());
    }

    public boolean cancelSelectIfSelected() {
        if (mAdapter != null && (mAdapter.getSelectedId() != TodoData.NON_ID)) {
            mAdapter.cancelSelect();
            return true;
        }
        return false;
    }

    public void showingSubjectSelect(long id) {
        if (id != SubjectData.NON_ID) {
            mSelectedShowingSubjectId = id;
            return;
        }
        TodoProvider provider = TodoProvider.getInstance();
        long candidate = SubjectData.NON_ID;
        for (SubjectData subject : provider.getAllSubject()) {
            if (subject.isShowing()) {
                if (candidate != SubjectData.NON_ID) {
                    candidate = SubjectData.NON_ID;
                    break;
                }
                candidate = subject.getId();
            }
        }
        mSelectedShowingSubjectId = candidate;
    }

    private void hideInputMethod() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager =
                    (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}
