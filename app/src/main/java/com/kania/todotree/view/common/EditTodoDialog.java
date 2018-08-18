package com.kania.todotree.view.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kania.todotree.R;
import com.kania.todotree.data.RequestTodoData;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoProvider;
import com.kania.todotree.view.utils.TodoDateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class EditTodoDialog extends DialogFragment implements TodoProvider.IDataObserver,
        View.OnClickListener,
        TodoDatePickerDialog.OnDateSetListener {
    private static final String ARG_BASE_TODO_ID = "baseTodoId";
    private static final String ARG_SELECTED_SUBJECT_ID = "SelectedSubjectId";
    private static final String ARG_SET_DUE_DATE = "setDueDate";
    private static final String ARG_EDITED_NAME = "editedNAme";

    //need to save args
    private long mBaseTodoId;
    private long mSelectedSubjectId;
    private long mSetDueDate;
    private String mEditedName;

    private Spinner mSpinner;
    private EditText mEditName;
    private AppCompatCheckBox mCheckDueDate;
    private View mLayoutDueDate;
    private Button mBtnDueDate;

    private SubjectSpinerAdapter mSubjectSpinerAdapter;

    public EditTodoDialog() {
        // Required empty public constructor
    }

    public static EditTodoDialog newInstance(long baseTodoId) {
        EditTodoDialog fragment = new EditTodoDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_BASE_TODO_ID, baseTodoId);
        fragment.setArguments(args);
        return fragment;
    }

    public void saveStates(Bundle bundle) {
        bundle.putLong(ARG_BASE_TODO_ID, mBaseTodoId);
        bundle.putLong(ARG_SELECTED_SUBJECT_ID, mSelectedSubjectId);
        bundle.putString(ARG_EDITED_NAME, mEditedName);
        bundle.putLong(ARG_SET_DUE_DATE, mSetDueDate);
    }

    public void restoreSavedData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mBaseTodoId = savedInstanceState.getLong(ARG_BASE_TODO_ID);
            mSelectedSubjectId = savedInstanceState.getLong(ARG_SELECTED_SUBJECT_ID);
            mEditedName = savedInstanceState.getString(ARG_EDITED_NAME);
            mSetDueDate = savedInstanceState.getLong(ARG_SET_DUE_DATE);
        } else {
            if (getArguments() != null)
                mBaseTodoId = getArguments().getLong(ARG_BASE_TODO_ID);
            else
                mBaseTodoId = TodoData.NON_ID;
            if (isEditDialog()) {
                TodoData baseTodo = TodoProvider.getInstance().getTodo(mBaseTodoId);
                mSelectedSubjectId = baseTodo.getSubject();
                mEditedName = baseTodo.getName();
                mSetDueDate = baseTodo.getDueDate();
            } else {
                mSelectedSubjectId = SubjectData.NON_ID;
                mEditedName = "";
                mSetDueDate = TodoData.NON_DUEDATE;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        restoreSavedData(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_add_todo, null);

        setSubjectView(dialogLayout);
        setTodoNameView(dialogLayout);
        setDueDateView(dialogLayout);
        setDialogButtonClickEvent(builder, dialogLayout);

        return builder.create();
    }

    private void setSubjectView(View dialogLayout) {
        //spinner
        ArrayList<SubjectData> subjectList = TodoProvider.getInstance().getAllSubject();
        mSpinner = dialogLayout.findViewById(R.id.dialog_add_todo_spinner_subject);
        mSubjectSpinerAdapter = new SubjectSpinerAdapter(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, subjectList);
        mSubjectSpinerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSubjectSpinerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                SubjectData subject = (SubjectData)adapterView.getSelectedItem();
                mSelectedSubjectId = subject.getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSelectedSubjectId = SubjectData.NON_ID;
            }
        });
        selectSubject(mSelectedSubjectId);

        //add subject button
        View btnAddSubject = dialogLayout.findViewById(R.id.dialog_add_todo_btn_subject);
        btnAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSubjectDialog addSubjectDialog =
                        AddSubjectDialog.newInstance(SubjectData.NON_ID);
                addSubjectDialog.show(getActivity().getSupportFragmentManager(),
                        AddSubjectDialog.class.getName());
            }
        });

        View subjectLayout = dialogLayout.findViewById(R.id.dialog_add_todo_layout_subject);
        if (isEditDialog()) {
            TodoData baseTodo = TodoProvider.getInstance().getTodo(mBaseTodoId);
            btnAddSubject.setEnabled(baseTodo.isRootTodo());
            mSpinner.setEnabled(baseTodo.isRootTodo());
            subjectLayout.setVisibility(baseTodo.isRootTodo() ? View.VISIBLE : View.GONE);
        } else {
            subjectLayout.setVisibility(View.VISIBLE);
            btnAddSubject.setEnabled(true);
            mSpinner.setEnabled(true);
        }
    }

    private void setTodoNameView(View dialogLayout) {
        mEditName = dialogLayout.findViewById(R.id.dialog_add_todo_edit_name);
        mEditName.setText(mEditedName);
    }

    private void setDueDateView(View dialogLayout) {
        //layout
        mCheckDueDate = dialogLayout.findViewById(R.id.dialog_add_todo_check_due_date);
        mLayoutDueDate = dialogLayout.findViewById(R.id.dialog_add_todo_layout_due_date);
        mBtnDueDate = dialogLayout.findViewById(R.id.dialog_add_todo_btn_due_date);

        boolean isSetDueDate = (mSetDueDate != TodoData.NON_DUEDATE);
        setDueDateLayout(isSetDueDate);
        mCheckDueDate.setChecked(isSetDueDate);

        //duedate button
        mBtnDueDate.setOnClickListener(this);
        //checkbox
        mCheckDueDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setDueDateLayout(isChecked);
            }
        });
    }

    private void setDueDateLayout(boolean isSetDueDate) {
        if (isSetDueDate) {
            mLayoutDueDate.setVisibility(View.VISIBLE);
            mBtnDueDate.setText(TodoDateUtil.getFormatedDateString(getContext(), mSetDueDate));
        } else {
            mLayoutDueDate.setVisibility(View.GONE);
            Calendar today = Calendar.getInstance();
            Date todayWithoutTime = TodoDateUtil.removeTimeFromDate(today.getTime());
            mSetDueDate = todayWithoutTime.getTime();
        }
        mBtnDueDate.setText(TodoDateUtil.getFormatedDateString(getContext(), mSetDueDate));
    }

    private void setDialogButtonClickEvent(AlertDialog.Builder builder, View dialogLayout) {
        builder.setView(dialogLayout);
        setPositiveButtonClickEvent(builder);
        setNegativeButtonClickEvent(builder);
        setNeutralButtonClickEvent(builder);
    }

    private void setPositiveButtonClickEvent(AlertDialog.Builder builder) {
        int positiveStringId = isAddDialog() ?
                R.string.dialog_edit_todo_btn_add : R.string.dialog_edit_todo_btn_update;
        builder.setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                publishTodo();
            }
        });
    }
    private void setNegativeButtonClickEvent(AlertDialog.Builder builder) {
        int NegativeStringId = R.string.dialog_edit_todo_btn_cancel;
        builder.setNegativeButton(NegativeStringId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do noting
            }
        });
    }
    private void setNeutralButtonClickEvent(AlertDialog.Builder builder) {
        if (isAddDialog())
            return;
        int neutralStringId = R.string.dialog_edit_subject_btn_Delete;
        //TODO problem
        builder.setNeutralButton(neutralStringId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Context context = getActivity();
                AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog_delete_todo_title)
                        .setMessage(R.string.dialog_delete_todo_text_not_complete)
                        .setPositiveButton(R.string.dialog_edit_subject_btn_Delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TodoProvider.getInstance().deleteTodo(context, mBaseTodoId);
                            }
                        })
                        .setNegativeButton(R.string.dialog_edit_todo_btn_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
                AlertDialog confirmDialog = confirmDialogBuilder.create();
                confirmDialog.show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        TodoProvider.getInstance().attachObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        TodoDatePickerDialog dialog = (TodoDatePickerDialog) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(TodoDatePickerDialog.class.getName());
        if (dialog != null)
            dialog.setListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TodoProvider.getInstance().detachObserver(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStates(outState);
    }

    @Override
    public void onTodoAdded(ArrayList<TodoData> creates) {
        //do nothing
    }
    @Override
    public void onTodoRemoved(ArrayList<Integer> removePositions, HashSet<Long> parents) {
        //do nothing
    }
    @Override
    public void onTodoUpdated(ArrayList<RequestTodoData> origins, ArrayList<TodoData> updates) {
        //do nothing
    }

    @Override
    public void onRefreshTodo() {
        //do nothing
    }

    @Override
    public void onSubjectAdded(ArrayList<Long> creates) {
        for (long added : creates) {
            Log.d("todo_tree", "onSubjectAdded() called, id : " + added);
            updateSubjectList();
            selectSubject(added);
            mSelectedSubjectId = added;
        }
    }
    @Override
    public void onSubjectRemoved(ArrayList<Long> removes) {
        updateSubjectList();
    }
    @Override
    public void onSubjectUpdated(ArrayList<Long> updates) {
        updateSubjectList();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBtnDueDate.getId()) {
            Calendar setDate = Calendar.getInstance();
            setDate.setTimeInMillis(mSetDueDate);
            TodoDatePickerDialog datePickerDialog = TodoDatePickerDialog.newInstance(
                    setDate.getTime(), this);
            datePickerDialog.show(getActivity().getSupportFragmentManager(),
                    TodoDatePickerDialog.class.getName());
        }
    }

    @Override
    public void onDateSet(Date setDate) {
        mSetDueDate = TodoDateUtil.getTimeInMillis(setDate);
        if (mBtnDueDate != null) {
            mBtnDueDate.setText(TodoDateUtil.getFormatedDateString(
                    getContext(), setDate));
        }
    }

    private void updateSubjectList() {
        if (mSubjectSpinerAdapter != null)
            mSubjectSpinerAdapter.notifyDataSetChanged();
    }

    private void selectSubject(long id) {
        if (mSpinner != null) {
            int pos = mSubjectSpinerAdapter.getPositionBySubjectId(id);
            Log.d("todo_tree", "selectSubject() pos : " + pos);
            if (pos < mSubjectSpinerAdapter.getCount())
                mSpinner.setSelection(pos);
        }
    }

    private void publishTodo() {
        //SubjectData subject = TodoProvider.getInstance().getSubject(mSelectedSubjectId);
        String todoName = mEditName.getText().toString();
        if (isValidName(todoName) == false)
            return;

        RequestTodoData requestTodoData;
        if (isEditDialog()) {
            TodoData baseTodo = TodoProvider.getInstance().getTodo(mBaseTodoId);
            requestTodoData = new RequestTodoData(mSelectedSubjectId, todoName, baseTodo.getParent(),
                    TodoDateUtil.getCurrent());
            requestTodoData.setId(baseTodo.getId());
        } else {
            requestTodoData = new RequestTodoData(mSelectedSubjectId, todoName, TodoData.NON_ID,
                    TodoDateUtil.getCurrent());
        }
        if (mCheckDueDate.isChecked()) {
            requestTodoData.setDueDate(mSetDueDate);
        }
        TodoProvider.getInstance().editTodo(getActivity(), requestTodoData);
    }

    private boolean isValidName(String name) {
        if (name.trim().isEmpty()) {
            Toast.makeText(getActivity(), "cannot make empty name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isEditDialog() {
        return !isAddDialog();
    }
    private boolean isAddDialog() {
        return mBaseTodoId == TodoData.NON_ID;
    }

    class SubjectSpinerAdapter extends ArrayAdapter<SubjectData> {

        private List<SubjectData> mItems;

        public SubjectSpinerAdapter(@NonNull Context context, int resource,
                                    @NonNull List<SubjectData> items) {
            super(context, resource, items);
            mItems = items;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            setSubjectOnView(view, position);
            return view;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            setSubjectOnView(view, position);
            return view;
        }



        public int getPositionBySubjectId(long subjectId) {
            int ret = 0;
            for (int i = 0; i < mItems.size(); ++i)
                if (mItems.get(i).getId() == subjectId) {
                    ret = i;
                    break;
                }
            return ret;
        }

        private void setSubjectOnView(View view, int position) {
            TextView tv = (TextView) view;
            SubjectData subject = mItems.get(position);
            tv.setText(subject.getName());
            tv.setTextColor(subject.getColor());
        }
    }
}
