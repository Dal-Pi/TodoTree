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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.kania.todotree.R;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCompleteAddTodo} interface
 * to handle interaction events.
 * Use the {@link AddTodoDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTodoDialog extends DialogFragment {
    private TodoData mBaseTodo;
    private Button mBtnTargetDate;

    private OnCompleteAddTodo mListener;

    public AddTodoDialog() {
        // Required empty public constructor
    }

    public static AddTodoDialog newInstance(TodoData baseTodo) {
        AddTodoDialog fragment = new AddTodoDialog();
        //Bundle args = new Bundle();
        if (baseTodo != null) {
            //TODO use baseTodo
        }
        //fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //TODO
        //if (getArguments() != null) {
        //}

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_add_todo, null);
        ArrayList<SubjectData> subjectList = TodoProvider.getInstance().getAllSubject();
        Spinner spinner = dialogLayout.findViewById(R.id.dialog_add_todo_spinner_subject);
        SubjectSpinerAdapter subjectSpinerAdapter = new SubjectSpinerAdapter(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, subjectList);
        subjectSpinerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(subjectSpinerAdapter);

        View btnAddSubject = dialogLayout.findViewById(R.id.dialog_add_todo_btn_subject);
        btnAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSubjectDialog addSubjectDialog = AddSubjectDialog.newInstance();
                addSubjectDialog.show(getActivity().getSupportFragmentManager(),
                        AddSubjectDialog.class.getName());
            }
        });

        mBtnTargetDate = dialogLayout.findViewById(R.id.dialog_add_todo_btn_target_date);
        mBtnTargetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoDatePickerDialog datePickerDialog = TodoDatePickerDialog.newInstance(
                        new TodoDatePickerDialog.onDateSet() {
                    @Override
                    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
                        //TODO arrange
                        if (mBtnTargetDate != null) {
                            mBtnTargetDate.setText(year + "/" + monthOfYear + "/" + dayOfMonth);
                        }
                    }
                });
                datePickerDialog.show(getActivity().getSupportFragmentManager(),
                        TodoDatePickerDialog.class.getName());
            }
        });

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
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCompleteAddTodo) {
            mListener = (OnCompleteAddTodo) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCompleteAddTodo");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void publishTodo() {
        mListener.onCompleteAddTodo(null);
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

        private void setSubjectOnView(View view, int position) {
            TextView tv = (TextView) view;
            SubjectData subject = mItems.get(position);
            tv.setText(subject.getName());
            tv.setTextColor(subject.getColor());
        }
    }
}
