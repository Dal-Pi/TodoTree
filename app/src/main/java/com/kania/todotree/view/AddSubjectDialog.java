package com.kania.todotree.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kania.todotree.R;
import com.kania.todotree.data.RequestSubjectData;
import com.kania.todotree.data.TodoProvider;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddSubjectDialog} interface
 * to handle interaction events.
 * Use the {@link AddSubjectDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddSubjectDialog extends DialogFragment {
    private static final String ARG_BASE_SUBJECT_ID = "baseSubjectId";
    private long mBaseSubjectId;
    //private OnCompleteAddSubject mListener;

    private EditText mEditName;
    private Spinner mSpinnerColor;

    public AddSubjectDialog() {
        // Required empty public constructor
    }

    //TODO I did not define save args but work. define clearly
    public static AddSubjectDialog newInstance(long baseSubjectId) {
        AddSubjectDialog fragment = new AddSubjectDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_BASE_SUBJECT_ID, baseSubjectId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mBaseSubjectId = getArguments().getLong(ARG_BASE_SUBJECT_ID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_add_subject, null);
        mEditName = dialogLayout.findViewById(R.id.dialog_add_subject_edit_name);
        mSpinnerColor = dialogLayout.findViewById(R.id.dialog_add_subject_spinner_color);
        ColorSpinerAdapter subjectSpinerAdapter = new ColorSpinerAdapter(getActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                getContext().getResources().getStringArray(R.array.color_palette));
        subjectSpinerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinnerColor.setAdapter(subjectSpinerAdapter);

        builder.setView(dialogLayout)
                .setPositiveButton(R.string.dialog_edit_subject_btn_add,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                publishSubject();
                            }
                        })
                .setNegativeButton(R.string.dialog_edit_subject_btn_cancel,
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
        /*
        if (context instanceof OnCompleteAddSubject) {
            mListener = (OnCompleteAddSubject) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCompleteAddSubject");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    private void publishSubject() {
        String name;
        String color;
        if (mEditName != null) {
            name = mEditName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getActivity(),
                        "cannot make empty name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mSpinnerColor != null) {
                color = mSpinnerColor.getSelectedItem().toString();
                if (color.isEmpty()) {
                    Log.e("todo_tree", "publishSubject() color is empty");
                    return;
                }
                RequestSubjectData requestData = new RequestSubjectData(name, color);
                TodoProvider.getInstance().addSubject(getActivity(), requestData);
            }
        }
    }

    class ColorSpinerAdapter extends ArrayAdapter<String> {

        private String[] mItems;

        public ColorSpinerAdapter(@NonNull Context context, int resource, @NonNull String[] items) {
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
            String colorString = mItems[position];
            tv.setTextColor(Color.parseColor(colorString));
        }
    }
}
