package com.kania.todotree.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class TodoProvider {

    public static int NO_SELECTED = -1;
    private static TodoProvider mInstance;

    private ArrayList<TodoData> mTodoList;
    private HashMap<Integer, TodoData> mTodoMap;
    private ArrayList<SubjectData> mSubjectList;
    private HashMap<Integer, SubjectData> mSubjectMap;
    private int mSelected = NO_SELECTED;

    private ArrayList<IDataObserver> mObservers;

    public static TodoProvider getInstance() {
        if (mInstance == null)
            mInstance = new TodoProvider();
        return mInstance;
    }
    private TodoProvider() {
        mObservers = new ArrayList<>();

        mTodoList = new ArrayList<>();
        mTodoMap = new HashMap<>();
        mSubjectList = new ArrayList<>();
        mSubjectMap = new HashMap<>();

        SubjectData defaultSubject = new SubjectData(0, "defualt", "#FF555555");
        insertSubject(defaultSubject);

        //dummy data [start]
        /*
        SubjectData sub1 = new SubjectData(1, "subject_1", "#FF9696E1");
        insertSubject(sub1);
        SubjectData sub2 = new SubjectData(2, "subject_2", "#FFB4D25A");
        insertSubject(sub2);
        SubjectData sub3 = new SubjectData(3, "subject_3", "#FFFF7F7F");
        insertSubject(sub3);

        TodoData todo1_1 = new TodoData(1, sub1, "todo1_1", null,
        false, 0, 0, 0);
        insertTodo(todo1_1);
        TodoData todo2_1 = new TodoData(2, sub1, "todo2_1", todo1_1,
                false, 0, 0, 0);
        insertTodo(todo2_1);
        TodoData todo3_1 = new TodoData(3, sub1, "todo3_1", todo2_1,
                true, 0, 0, 0);
        insertTodo(todo3_1);
        TodoData todo4_1 = new TodoData(4, sub1, "todo4_1", todo3_1,
                true, 0, 0, 0);
        insertTodo(todo4_1);

        TodoData todo5_1 = new TodoData(5, sub2, "todo5_1", null,
                true, 0, 0, 0);
        insertTodo(todo5_1);
        TodoData todo5_2 = new TodoData(6, sub2, "todo5_2", null,
                false, 0, 0, 0);
        insertTodo(todo5_2);
        TodoData todo6_1 = new TodoData(7, sub2, "todo6_1", todo5_2,
                false, 0, 0, 0);
        insertTodo(todo6_1);

        TodoData todo7_1 = new TodoData(8, sub3, "todo7_1", null,
                true, 0, 0, 0);
        insertTodo(todo7_1);
        TodoData todo8_1 = new TodoData(9, sub3, "todo8_1", null,
                true, 0, 0, 0);
        insertTodo(todo8_1);
        TodoData todo9_1 = new TodoData(10, sub3, "todo9_1", null,
                true, 0, 0, 0);
        insertTodo(todo9_1);
        TodoData todo10_1 = new TodoData(11, sub3, "todo10_1", null,
                true, 0, 0, 0);
        insertTodo(todo10_1);
        TodoData todo11_1 = new TodoData(12, sub3, "todo11_1", null,
                true, 0, 0, 0);
        insertTodo(todo11_1);
        */
        //dummy data [end]
    }

    private void insertSubject(SubjectData subject) {
        mSubjectList.add(subject);
        mSubjectMap.put(subject.getId(), subject);
    }
    private void removeSubject(SubjectData subject) {
        mSubjectList.remove(subject);
        mSubjectMap.remove(subject.getId());
    }

    private void insertTodo(TodoData todo) {
        mTodoList.add(todo);
        mTodoMap.put(todo.getId(), todo);
    }
    private void removeTodo(TodoData todo) {
        mTodoList.remove(todo);
        mTodoMap.remove(todo.getId());
    }

    public ArrayList<TodoData> getAllTodo() {
        return mTodoList;
    }

    public ArrayList<SubjectData> getAllSubject() {
        return mSubjectList;
    }

    public TodoData getTodo(int id) {
        return mTodoMap.get(id);
    }

    public SubjectData getSubject(int id) {
        return mSubjectMap.get(id);
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

    public void addTodo(RequestTodoData requested) {
        //TODO use DB
        int maxId  = 0;
        for (TodoData td : mTodoList) {
            if (td.getId() > maxId)
                maxId = td.getId();
        }
        TodoData todo = requested.createTodo(maxId + 1);
        insertTodo(todo);

        for (IDataObserver observer : mObservers) {
            observer.onTodoAdded(todo);
        }
    }
    public void deleteTodo(TodoData requested) {
        //TODO use DB
        removeTodo(requested);

        for (IDataObserver observer : mObservers) {
            observer.onTodoRemoved(requested);
        }
    }
    public void updateTodo(RequestTodoData requested) {
        //TODO use DB
        if (requested.id != TodoData.NON_ID) {
            RequestTodoData prev = new RequestTodoData(requested.subject,
                    requested.name, requested.parent, requested.updatedDate);
            prev.setId(requested.id);
            prev.setTargetDate(requested.targetDate);

            TodoData target = mTodoMap.get(requested.id);
            target.setSubject(requested.subject);
            target.setName(requested.name);
            target.setParent(requested.parent);
            target.setTargetDate(requested.targetDate);
            target.setLastUpdated(requested.updatedDate);

            for (IDataObserver observer : mObservers) {
                observer.onTodoUpdated(prev, target);
            }
        }
    }

    public void addSubject(RequestSubjectData requested) {
        //TODO use DB
        int maxId  = 0;
        for (SubjectData sd : mSubjectList) {
            if (sd.getId() > maxId)
                maxId = sd.getId();
        }
        SubjectData subject = requested.createSubject(maxId + 1);
        insertSubject(subject);

        for (IDataObserver observer : mObservers) {
            observer.onSubjectAdded(subject);
        }
    }
    public void deleteSubject(SubjectData requested) {
        //TODO use DB

    }
    public void updateSubject(SubjectData requested) {
        //TODO use DB

    }

    public void attachObserver(IDataObserver observer) {
        mObservers.add(observer);
    }
    public void detachObserver(IDataObserver observer) {
        mObservers.remove(observer);
    }

    public interface IDataObserver {
        void onTodoAdded(TodoData added);
        void onTodoRemoved(TodoData removed);
        void onTodoUpdated(RequestTodoData prev, TodoData updated);

        void onSubjectAdded(SubjectData added);
        void onSubjectRemoved(SubjectData removed);
        void onSubjectUpdated(SubjectData prev, SubjectData updated);
    }
}
