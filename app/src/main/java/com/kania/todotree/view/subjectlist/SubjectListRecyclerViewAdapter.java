package com.kania.todotree.view.subjectlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kania.todotree.R;
import com.kania.todotree.data.SubjectData;
import com.kania.todotree.data.TodoProvider;
import com.kania.todotree.view.todolist.TodoItemRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SubjectListRecyclerViewAdapter
        extends RecyclerView.Adapter<SubjectListRecyclerViewAdapter.SubjectViewHolder> {

    private Context mContext;
    private List<SubjectData> mItems;
    private OnSubjectItemActionListener mListener;

    public SubjectListRecyclerViewAdapter(Context context, List<SubjectData> items) {
        mContext = context;
        mItems = items;
    }

    public void setSubjectItemListener(OnSubjectItemActionListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_list_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubjectViewHolder holder, int position) {
        final SubjectData subject = mItems.get(position);
        holder.mItem = subject;
        holder.mBtnTitle.setText(subject.getName());
        decorateItemByShowing(holder, subject.isShowing());

        holder.mBtnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decorateItemByShowing(holder, !holder.mItem.isShowing());
                TodoProvider.getInstance()
                        .setSubjectVisibility(holder.mItem.getId(), !holder.mItem.isShowing());
                if (mListener != null) {
                    mListener.onSubjectSelected(holder.mItem.getId());
                }
            }
        });
    }

    private void decorateItemByShowing(final SubjectViewHolder holder, boolean bShowing) {
        //TODO change design
        if (bShowing) {
            holder.mBtnTitle.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.mLayout.setBackgroundColor(holder.mItem.getColor());
        } else {
            holder.mBtnTitle.setTextColor(holder.mItem.getColor());
            holder.mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {
        public View mLayout;
        public Button mBtnTitle;
        public SubjectData mItem;
        public SubjectViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.subject_item_layout);
            mBtnTitle = view.findViewById(R.id.subject_item_btn_title);
        }
    }

    public interface OnSubjectItemActionListener {
        void onSubjectSelected(long subjectId);
    }
}
