package com.kania.todotree.view;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kania.todotree.R;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.view.CheckListFragment.OnListFragmentInteractionListener;
import com.kania.todotree.view.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<TodoData> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<TodoData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TodoData todo = mValues.get(position);
        holder.mItem = todo;
        holder.mCheckBox.setChecked(todo.isCompleted());
        int color = todo.getSubject().getColor();
        ViewUtil.setCheckBoxColor(holder.mCheckBox, color, color);
        holder.mContentView.setText(mValues.get(position).getName());
        holder.mContentView.setTextColor(color);
        ViewUtil.setIndentation(holder.mView, todo.getDepth());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AppCompatCheckBox mCheckBox;
        public final TextView mContentView;
        public TodoData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCheckBox = (AppCompatCheckBox) view.findViewById(R.id.item_checkbox);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
