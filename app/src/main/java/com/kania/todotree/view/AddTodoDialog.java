package com.kania.todotree.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCompleteAddTodo} interface
 * to handle interaction events.
 * Use the {@link AddTodoDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTodoDialog extends DialogFragment implements TodoProvider.IDataObserver,
        View.OnClickListener,
        TodoDatePickerDialog.OnDateSetListener {
    private static final String ARG_BASE_TODO_ID = "baseTodoId";
    private static final String ARG_SELECTED_SUBJECT_ID = "SelectedSubjectId";
    private static final String ARG_SET_DUE_DATE = "setDueDate";
    private static final String ARG_EDITED_NAME = "editedNAme";


    //need to save args
    private int mBaseTodoId;
    private int mSelectedSubjectId;
    private long mSetDueDate;
    private String mEditedName;

    private Spinner mSpinner;
    private EditText mEditName;
    private AppCompatCheckBox mCheckDueDate;
    private View mLayoutDueDate;
    private Button mBtnDueDate;

    private SubjectSpinerAdapter mSubjectSpinerAdapter;

    public AddTodoDialog() {
        // Required empty public constructor
    }

    public static AddTodoDialog newInstance(int baseTodoId) {
        AddTodoDialog fragment = new AddTodoDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_BASE_TODO_ID, baseTodoId);
        fragment.setArguments(args);
        return fragment;
    }

    public void saveArgs(Bundle args) {
        args.putInt(ARG_BASE_TODO_ID, TodoData.NON_ID);
        args.putInt(ARG_SELECTED_SUBJECT_ID, mSelectedSubjectId);
        args.putString(ARG_EDITED_NAME, mEditedName);
        args.putLong(ARG_SET_DUE_DATE, mSetDueDate);
    }

    public void restoreSavedData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mBaseTodoId = TodoData.NON_ID;
            mSelectedSubjectId = savedInstanceState.getInt(ARG_SELECTED_SUBJECT_ID);
            mEditedName = savedInstanceState.getString(ARG_EDITED_NAME);
            mSetDueDate = savedInstanceState.getLong(ARG_SET_DUE_DATE);
        } else {
            mBaseTodoId = getArguments().getInt(ARG_BASE_TODO_ID);
            if (mBaseTodoId != TodoData.NON_ID) {
                TodoData baseTodo = TodoProvider.getInstance().getTodo(mBaseTodoId);
                mSelectedSubjectId = baseTodo.getSubject().getId();
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
        builder.setView(dialogLayout)
                .setPositiveButton(R.string.dialog_add_todo_btn_add,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                publishTodo();
                            }
                        })
                .setNegativeButton(R.string.dialog_add_todo_btn_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do noting
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
        saveArgs(outState);
    }

    @Override
    public void onTodoAdded(TodoData added) {
        //do nothing
    }
    @Override
    public void onTodoRemoved(TodoData removed) {
        //do nothing
    }
    @Override
    public void onTodoUpdated(RequestTodoData prev, TodoData updated) {
        //do nothing
    }

    @Override
    public void onSubjectAdded(SubjectData added) {
        Log.d("todo_tree", "onSubjectAdded() called, id : " + added.getId());
        updateSubjectList();
        selectSubject(added.getId());
        mSelectedSubjectId = added.getId();
    }
    @Override
    public void onSubjectRemoved(SubjectData removed) {
        updateSubjectList();
    }
    @Override
    public void onSubjectUpdated(SubjectData prev, SubjectData updated) {
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

    private void selectSubject(int id) {
        if (mSpinner != null) {
            int pos = mSubjectSpinerAdapter.getPositionBySubjectId(id);
            Log.d("todo_tree", "selectSubject() pos : " + pos);
            if (pos != SubjectData.NON_ID)
                mSpinner.setSelection(pos);
        }
    }

    private void publishTodo() {
        SubjectData subject = TodoProvider.getInstance().getSubject(mSelectedSubjectId);
        String todoName = mEditName.getText().toString();
        if (todoName.trim().isEmpty()) {
            Toast.makeText(getActivity(),
                    "cannot make empty name", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestTodoData requestTodoData = new RequestTodoData(subject, todoName, null,
                TodoDateUtil.getCurrent());
        if (mCheckDueDate.isChecked()) {
            requestTodoData.setDueDate(mSetDueDate);
        }
        TodoProvider.getInstance().addTodo(requestTodoData);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCompleteAddTodo {
        void onCompleteAddTodo(TodoData completedTodo);
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



        public int getPositionBySubjectId(int subjectId) {
            int ret = SubjectData.NON_ID;
            for (SubjectData sd : mItems)
                if (sd.getId() == subjectId)
                    ret = sd.getId();
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
