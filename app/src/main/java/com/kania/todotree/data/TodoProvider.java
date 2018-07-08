package com.kania.todotree.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class TodoProvider {

    public static int NO_SELECTED = -1;
    private static TodoProvider mInstance;

    private ArrayList<TodoData> mTodoList;
    private HashMap<Integer, TodoData> mTodoMap;
    private int mSelected = NO_SELECTED;

    public static TodoProvider getInstance() {
        if (mInstance == null)
            mInstance = new TodoProvider();
        return mInstance;
    }
    private TodoProvider() {
        mTodoList = new ArrayList<>();
        mTodoMap = new HashMap<>();

        //dummy data [start]
        SubjectData sub1 = new SubjectData(1, "#FF9696E1");
        TodoData todo1_1 = new TodoData(1, sub1, "todo1_1", null,
        false, 0, 0, 0);
        addTodo(todo1_1);
        TodoData todo2_1 = new TodoData(2, sub1, "todo2_1", todo1_1,
                false, 0, 0, 0);
        addTodo(todo2_1);
        TodoData todo3_1 = new TodoData(3, sub1, "todo3_1", todo2_1,
                true, 0, 0, 0);
        addTodo(todo3_1);
        TodoData todo4_1 = new TodoData(4, sub1, "todo4_1", todo3_1,
                true, 0, 0, 0);
        addTodo(todo4_1);

        SubjectData sub2 = new SubjectData(2, "#FFB4D25A");
        TodoData todo5_1 = new TodoData(5, sub2, "todo5_1", null,
                true, 0, 0, 0);
        addTodo(todo5_1);
        TodoData todo5_2 = new TodoData(6, sub2, "todo5_2", null,
                false, 0, 0, 0);
        addTodo(todo5_2);
        TodoData todo6_1 = new TodoData(7, sub2, "todo6_1", todo5_2,
                false, 0, 0, 0);
        addTodo(todo6_1);

        SubjectData sub3 = new SubjectData(3, "#FFFF7F7F");
        TodoData todo7_1 = new TodoData(8, sub3, "todo7_1", null,
                true, 0, 0, 0);
        addTodo(todo7_1);
        TodoData todo8_1 = new TodoData(9, sub3, "todo8_1", null,
                true, 0, 0, 0);
        addTodo(todo8_1);
        TodoData todo9_1 = new TodoData(10, sub3, "todo9_1", null,
                true, 0, 0, 0);
        addTodo(todo9_1);
        TodoData todo10_1 = new TodoData(11, sub3, "todo10_1", null,
                true, 0, 0, 0);
        addTodo(todo10_1);
        TodoData todo11_1 = new TodoData(12, sub3, "todo11_1", null,
                true, 0, 0, 0);
        addTodo(todo11_1);

        //dummy data [end]
    }

    private void addTodo(TodoData todo) {
        mTodoList.add(todo);
        mTodoMap.put(todo.getId(), todo);
    }

    public ArrayList<TodoData> getAllTodo() {
        return mTodoList;
    }

    public int getSelected() {
        return mSelected;
    }
    public void select(int id) {

        if (mTodoMap.containsKey(id)) {
            if (mSelected != id) {
                mSelected = id;
                //TODO debug
                Log.d("todo_tree", "[TodoProvider] selected : " + mSelected);
            } else {
                //TODO debug
                Log.d("todo_tree", "[TodoProvider] canceled : " + mSelected);
                cancelSelect();
            }
        } else {
            //TODO debug
            Log.d("todo_tree", "[TodoProvider] do not find selected id : " + id);
            cancelSelect();
        }
    }

    public void cancelSelect() {
        mSelected = NO_SELECTED;
    }
}
