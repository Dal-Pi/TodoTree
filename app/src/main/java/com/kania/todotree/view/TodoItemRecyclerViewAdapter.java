package com.kania.todotree.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.kania.todotree.R;
import com.kania.todotree.data.RequestTodoData;
import com.kania.todotree.data.TodoData;
import com.kania.todotree.data.TodoProvider;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class TodoItemRecyclerViewAdapter
        extends RecyclerView.Adapter<TodoItemRecyclerViewAdapter.TodoViewHolder> {

    public static final int NO_ITEM_SELECTED = -100;

    private Context mContext;

    private final List<TodoData> mItems;

    private int mSelectedPos;

    private ArrayList<OnTodoItemActionListener> mTodoActionListeners;

    public TodoItemRecyclerViewAdapter(Context context, List<TodoData> items) {
        mContext = context;
        mItems = items;
        mSelectedPos = NO_ITEM_SELECTED;
        mTodoActionListeners = new ArrayList<>();
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
        final TodoData todo = mItems.get(position);
        holder.mItem = todo;

        decorateTodoItem(holder, todo);

        holder.mContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(holder);
            }
        });

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("todo_tree", "checkbox selected, id:" + todo.getId() + ", checked:" + isChecked);
                TodoProvider.getInstance().updateTodo(todo.getId(), isChecked);
                setHandleButtonText(holder.mHandleTodo, todo);
            }
        });

        holder.mHandleTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todo.isCompleted()) {
                    if (todo.isRootTodo()) {
                        //TODO delete with option
                    } else {
                        //TODO delete without option
                    }
                } else {
                    notifySelectEditObservers(todo.getId());
                }
            }
        });

        holder.mAddSubTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.mEditSubTodoName.getText().toString();
                if (name.trim().isEmpty())
                    return;
                RequestTodoData requestTodoData = new RequestTodoData(todo.getSubject(), name, todo,
                        TodoDateUtil.getCurrent());
                TodoProvider.getInstance().addTodo(requestTodoData);
                holder.mEditSubTodoName.setText("");
                select(holder);
            }
        });
    }

    private void decorateTodoItem(final TodoViewHolder holder, final TodoData todo) {
        int color = todo.getSubject().getColor();
        holder.mDivider.setVisibility(todo.getDepth() == 0 ? View.VISIBLE : View.GONE);
        holder.mDueDate.setText(TodoDateUtil.getFormatedDateString(mContext, todo.getDueDate()));
        holder.mUpdated.setText(TodoDateUtil
                .getFormatedDateAndTimeString(mContext, todo.getLastUpdated()));
        holder.mCheckBox.setChecked(todo.isCompleted());
        ViewUtil.setCheckBoxColor(holder.mCheckBox, color, color);
        holder.mName.setText(todo.getName());
        holder.mName.setTextColor(color);
        setHandleButtonText(holder.mHandleTodo, todo);
        holder.mHandleTodo.setTextColor(color);
        holder.mAddSubTodo.setTextColor(color);
        ViewUtil.setIndentation(holder.mView, todo.getDepth());

        //holder.mIdDebug.setText(todo.getId() + " selected");
        //holder.mIdDebug.setTextColor(color);

        if (TodoProvider.getInstance().getSelected() == todo.getId()) {
            holder.mMenuLayout.setVisibility(View.VISIBLE);
            holder.mHandleTodo.setVisibility(View.VISIBLE);
        } else {
            holder.mMenuLayout.setVisibility(View.GONE);
            holder.mHandleTodo.setVisibility(View.INVISIBLE);
        }

        //holder.mDateLayout.setVisibility(View.VISIBLE);
    }

    private void setHandleButtonText(final Button btnHandle, final TodoData todo) {
        if (todo.isCompleted())
            if (todo.isRootTodo()) btnHandle.setText(R.string.item_menu_btn_done);
            else btnHandle.setText(R.string.item_menu_btn_delete);
        else btnHandle.setText(R.string.item_menu_btn_edit);
    }

    private void select(final TodoViewHolder holder) {
        int position = holder.getAdapterPosition();
        Log.d("todo_tree", "[TodoItemRecyclerViewAdapter] selected pos : " + holder.getAdapterPosition()
                + ", id = " + holder.mItem.getId());
        Log.d("todo_tree", "selected! " + holder.mItem.toString());

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
            hideInputMethod(holder.mEditSubTodoName);
        }
        notifySelectObservers(mSelectedPos == NO_ITEM_SELECTED ?
                TodoData.NON_ID : holder.mItem.getId());
    }

    public void cancelSelect() {
        mSelectedPos = NO_ITEM_SELECTED;
        TodoProvider.getInstance().cancelSelect();
        notifySelectObservers(TodoData.NON_ID);
    }

    private void hideInputMethod(EditText edit) {
        InputMethodManager inputManager =
                (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(edit.getWindowToken(),0);
        }
    }



    public void attachSelectListener(OnTodoItemActionListener listener) {
        mTodoActionListeners.add(listener);
    }

    private void notifySelectObservers(int todoId) {
        for (OnTodoItemActionListener listener : mTodoActionListeners) {
            listener.onSelectTodo(todoId);
        }
    }

    private void notifySelectEditObservers(int todoId) {
        for (OnTodoItemActionListener listener : mTodoActionListeners) {
            listener.onSelectEditTodo(todoId);
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
        public final EditText mEditSubTodoName;
        public final Button mAddSubTodo;
        public final Button mHandleTodo;

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
            mEditSubTodoName = view.findViewById(R.id.item_edit_sub_todo_name);
            mAddSubTodo = view.findViewById(R.id.item_btn_add_sub_todo);
            mHandleTodo = view.findViewById(R.id.item_btn_handle_todo);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }

    public interface OnTodoItemActionListener {
        void onSelectTodo(int id);
        void onSelectEditTodo(int id);
    }
}
