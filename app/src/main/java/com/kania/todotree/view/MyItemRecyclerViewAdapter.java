package com.kania.todotree.view;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kania.todotree.R;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoProvider;
import com.kania.todotree.view.CheckListFragment.OnListFragmentInteractionListener;
import com.kania.todotree.view.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter
        extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.TodoViewHolder> {

    public static final int NO_ITEM_SELECTED = -100;
    private final List<TodoData> mItems;
    private final OnListFragmentInteractionListener mListener;

    private int mSelectedPos;

    public MyItemRecyclerViewAdapter(List<TodoData> items, OnListFragmentInteractionListener listener) {
        mItems = items;
        mListener = listener;
        mSelectedPos = NO_ITEM_SELECTED;
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_list_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TodoViewHolder holder, final int position) {
        TodoData todo = mItems.get(position);
        holder.mItem = todo;

        decorateTodoItem(holder, todo);

        holder.mContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    //TODO debug
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);

                    select(holder);
                }
            }
        });
    }

    private void decorateTodoItem(final TodoViewHolder holder, TodoData todo) {
        holder.mDivider.setVisibility(todo.getDepth() == 0 ? View.VISIBLE : View.GONE);
        holder.mDueDate.setText(todo.getTargetDate() + ""); //TODO
        holder.mUpdated.setText(todo.getLastUpdated() + ""); //TODO
        holder.mCheckBox.setChecked(todo.isCompleted());
        int color = todo.getSubject().getColor();
        ViewUtil.setCheckBoxColor(holder.mCheckBox, color, color);
        holder.mName.setText(todo.getName());
        holder.mName.setTextColor(color);
        ViewUtil.setIndentation(holder.mView, todo.getDepth());

        holder.mIdDebug.setText(todo.getId() + " selected");
        holder.mIdDebug.setTextColor(color);

        if (TodoProvider.getInstance().getSelected() == todo.getId()) {
            holder.mMenuLayout.setVisibility(View.VISIBLE);
            holder.mFinishTodo.setVisibility(View.VISIBLE);
        } else {
            holder.mMenuLayout.setVisibility(View.GONE);
            holder.mFinishTodo.setVisibility(View.GONE);
        }
    }

    private void select(final TodoViewHolder holder) {
        int position = holder.getAdapterPosition();
        Log.d("todo_tree", "[MyItemRecyclerViewAdapter] selected pos : " + holder.getAdapterPosition()
                + ", id = " + holder.mItem.getId());

        TodoProvider.getInstance().select(holder.mItem.getId());

        if (mSelectedPos == NO_ITEM_SELECTED) {
            mSelectedPos = position;
            notifyItemChanged(position);
        } else {
            if (mSelectedPos != position) {
                int prev = mSelectedPos;
                mSelectedPos = position;
                notifyItemChanged(prev);
                notifyItemChanged(position);
            } else {
                int prev = mSelectedPos;
                mSelectedPos = NO_ITEM_SELECTED;
                notifyItemChanged(prev);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final View mDivider;

        public final View mDateLayout;
        public final TextView mDueDate;
        public final TextView mUpdated;

        public final View mContentLayout;
        public final AppCompatCheckBox mCheckBox;
        public final TextView mName;

        public final View mMenuLayout;
        public final TextView mIdDebug;
        public final Button mAddSubTodo;
        public final Button mFinishTodo;

        public TodoData mItem;

        public TodoViewHolder(View view) {
            super(view);
            mView = view;

            mDivider = view.findViewById(R.id.item_divider);

            mDateLayout = view.findViewById(R.id.item_layout_date);
            mDueDate = view.findViewById(R.id.item_text_due_date);
            mUpdated = view.findViewById(R.id.item_text_last_updated);

            mContentLayout = view.findViewById(R.id.item_layout_content);
            mCheckBox = view.findViewById(R.id.item_checkbox_todo);
            mName =  view.findViewById(R.id.item_text_name);

            mMenuLayout = view.findViewById(R.id.item_layout_select_menu);
            mIdDebug =  view.findViewById(R.id.item_text_selected_id_debug);
            mAddSubTodo = view.findViewById(R.id.item_btn_add_sub_todo);
            mFinishTodo = view.findViewById(R.id.item_btn_finish_todo);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
