package com.kania.todotree.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckboxEnabled(todo) == false) {
                    Toast.makeText(mContext, "not checkable.", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean currentCheck = todo.isCompleted();
                boolean resultCheck = !currentCheck;
                Log.d("todo_tree", "checkbox selected, id:" + todo.getId() + ", now checked:" + currentCheck);
                //TODO
                TodoProvider.getInstance().completeTodo(mContext, todo.getId(), resultCheck);
                holder.mCheckBox.setChecked(resultCheck);
                holder.mHandleTodo.setVisibility(resultCheck ? View.VISIBLE : View.INVISIBLE);
                decorateHandleButton(holder.mHandleTodo, todo);
            }
        });

        holder.mHandleTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todo.isCompleted()) {
                    AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.dialog_delete_todo_title)
//                            .setMessage(R.string.dialog_delete_todo_text)
                            .setPositiveButton(R.string.dialog_edit_subject_btn_Delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO
//                                    TodoProvider.getInstance().deleteTodo(todo.getId());
                                }
                            })
                            .setNegativeButton(R.string.dialog_edit_todo_btn_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });
                    if (todo.isRootTodo())
                        confirmDialogBuilder.setMessage(R.string.dialog_delete_todo_text_complete);
                    else
                        confirmDialogBuilder.setMessage(R.string.dialog_delete_todo_text_complete_sub);
                    AlertDialog confirmDialog = confirmDialogBuilder.create();
                    confirmDialog.show();
                } else {
                    notifySelectEditObservers(todo.getId());
                }
            }
        });

        holder.mAddSubTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.mEditSubTodoName.getText().toString();
                //TODO textutil
                if (name.trim().isEmpty()) {
                    Toast.makeText(mContext, "cannot make empty name", Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestTodoData requestTodoData = new RequestTodoData(todo.getSubject(), name,
                        todo.getId(), TodoDateUtil.getCurrent());
                releaseCompleteBySubTodo(todo);
                TodoProvider.getInstance().editTodo(v.getContext(), requestTodoData);
                holder.mEditSubTodoName.setText("");
                select(holder);
            }
        });
    }

    private void decorateTodoItem(final TodoViewHolder holder, final TodoData todo) {
        int color = TodoProvider.getInstance().getSubject(todo.getSubject()).getColor();
        holder.mDivider.setVisibility(todo.getDepth() == 0 ? View.VISIBLE : View.GONE);
        holder.mDueDate.setText(TodoDateUtil.getFormatedDateString(mContext, todo.getDueDate()));
        holder.mUpdated.setText(TodoDateUtil
                .getFormatedDateAndTimeString(mContext, todo.getLastUpdated()));
        decorateCheckbox(holder.mCheckBox, todo);
        holder.mName.setText(todo.getName());
        holder.mName.setTextColor(color);
        holder.mHandleTodo.setTextColor(color);
        holder.mAddSubTodo.setTextColor(color);
        ViewUtil.setIndentation(holder.mView, todo.getDepth());

        //TODO debug
        holder.mIdDebug.setText(todo.getId() + " selected");
        holder.mIdDebug.setTextColor(color);

        if (TodoProvider.getInstance().getSelected() == todo.getId()) {
            holder.mMenuLayout.setVisibility(View.VISIBLE);
        } else {
            holder.mMenuLayout.setVisibility(View.GONE);
        }
        decorateHandleButton(holder.mHandleTodo, todo);
    }

    private void decorateCheckbox(final AppCompatCheckBox checkbox, final TodoData todo) {
        int color = TodoProvider.getInstance().getSubject(todo.getSubject()).getColor();
        checkbox.setChecked(todo.isCompleted());
        ViewUtil.setCheckBoxColor(checkbox, color);
        checkbox.setEnabled(isCheckboxEnabled(todo));
    }

    private boolean isCheckboxEnabled(final TodoData todo) {
        TodoProvider provider = TodoProvider.getInstance();
        boolean currentCheck = todo.isCompleted();
        if (currentCheck && (todo.isRootTodo() == false))
            if (provider.getTodo(todo.getParent()).isCompleted())
                return false;
        if (currentCheck == false && provider.isCheckable(todo.getId()) == false)
            return false;
        return true;
    }

    private void decorateHandleButton(final Button btnHandle, final TodoData todo) {
        boolean isVisible = todo.isCompleted() ||
                (TodoProvider.getInstance().getSelected() == todo.getId());
        btnHandle.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        if (todo.isCompleted()) {
            if (todo.isRootTodo()) btnHandle.setText(R.string.item_menu_btn_done);
            else btnHandle.setText(R.string.item_menu_btn_delete);
        }
        else btnHandle.setText(R.string.item_menu_btn_edit);
    }

    private void select(final TodoViewHolder holder) {
        int position = holder.getAdapterPosition();
        Log.d("todo_tree", "[TodoItemRecyclerViewAdapter] selected pos : " + holder.getAdapterPosition()
                + ", id = " + holder.mItem.getId());
        Log.d("todo_tree", "[TodoItemRecyclerViewAdapter] selected item : " + holder.mItem.toString());

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

    private void releaseCompleteBySubTodo(final TodoData todo) {
        TodoProvider provider = TodoProvider.getInstance();
        TodoData target = todo;
        while (target != null) {
            target.setCompleted(false);
            target = provider.getTodo(target.getParent());
        }
        notifyDataSetChanged();
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

    private void notifySelectObservers(long todoId) {
        for (OnTodoItemActionListener listener : mTodoActionListeners) {
            listener.onSelectTodo(todoId);
        }
    }

    private void notifySelectEditObservers(long todoId) {
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
        void onSelectTodo(long id);
        void onSelectEditTodo(long id);
    }
}
