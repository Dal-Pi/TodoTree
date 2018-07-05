package com.kania.todotree.view;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.AbstractTodoListItemViewHolder> {

    public final int VIEW_TYPE_ITEM = 1;
    public final int VIEW_TYPE_SELECTED = 2;

    private final List<TodoData> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<TodoData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public AbstractTodoListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SELECTED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_item, parent, false);
            return new SelectMenuViewHolder(view);
        } else {// viewType == VIEW_TYPE_ITEM
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final AbstractTodoListItemViewHolder holder, int position) {
        TodoData todo = mValues.get(position);

        holder.setItem(todo);

        /*
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

                    TodoProvider provider = TodoProvider.getInstance();
                    provider.select(holder.mItem.getId());
                }
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public abstract class AbstractTodoListItemViewHolder extends RecyclerView.ViewHolder {
        public AbstractTodoListItemViewHolder(View itemView) {
            super(itemView);
        }
        public abstract void setItem(TodoData todo);
    }

    public class ItemViewHolder extends AbstractTodoListItemViewHolder {
        public final View mView;
        public final AppCompatCheckBox mCheckBox;
        public final TextView mContentView;
        public TodoData mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mCheckBox = view.findViewById(R.id.item_checkbox);
            mContentView =  view.findViewById(R.id.content);
        }

        @Override
        public void setItem(TodoData todo) {
            mItem = todo;
            mCheckBox.setChecked(todo.isCompleted());
            int color = todo.getSubject().getColor();
            ViewUtil.setCheckBoxColor(mCheckBox, color, color);
            mContentView.setText(todo.getName());
            mContentView.setTextColor(color);
            ViewUtil.setIndentation(mView, todo.getDepth());

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(mItem);

                        TodoProvider.getInstance().select(mItem.getId());
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public class SelectMenuViewHolder extends AbstractTodoListItemViewHolder {
        public final View mView;
        public final TextView mContentView;
        public TodoData mItem;

        public SelectMenuViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.text_selected_id);
        }

        @Override
        public void setItem(TodoData todo) {
            mItem = todo;
            mContentView.setText(todo.getId() + " selected");
            mContentView.setTextColor(todo.getSubject().getColor());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("todotree", "getItemViewType, pos = " + TodoProvider.getInstance().getSelected());
        if (position == TodoProvider.getInstance().getSelected())
            return VIEW_TYPE_SELECTED;
        else
            return VIEW_TYPE_ITEM;
    }
}
