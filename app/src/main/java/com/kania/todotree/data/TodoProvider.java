package com.kania.todotree.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class TodoProvider {

    public static int NO_SELECTED = -1;
    private static TodoProvider mInstance;

    private ArrayList<TodoData> mRootTodoList;
    private ArrayList<TodoData> mAllTodoList;
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

        mSubjectList = new ArrayList<>();
        mSubjectMap = new HashMap<>();

        mRootTodoList = new ArrayList<>();
        mAllTodoList = new ArrayList<>();
        mTodoMap = new HashMap<>();

        //TODO add DB when create
        SubjectData defaultSubject = new SubjectData(0, "defualt", "#FF555555");
        insertSubject(defaultSubject);
    }

    private void insertSubject(SubjectData subject) {
        mSubjectList.add(subject);
        mSubjectMap.put(subject.getId(), subject);
    }
    private void removeSubject(SubjectData subject) {
        mSubjectList.remove(subject);
        mSubjectMap.remove(subject.getId());
    }

    private int insertTodo(TodoData todo) {
        int pos;
        if (todo.getParent() == null) {
            Log.d("todo_tree", todo.getId() + "is the root Todo");
            pos = 0;
            mRootTodoList.add(pos, todo);
            mAllTodoList.add(pos, todo);
        } else {
            TodoData parent = mTodoMap.get(todo.getParent().getId());
            Log.d("todo_tree", "insertTodo() find parent:" + parent.getId());
            int childrenCount = parent.getChildrenCount();
            Log.d("todo_tree", parent.getId() + "'s children count = " + childrenCount);
            pos = mAllTodoList.indexOf(parent) + childrenCount + 1;
            if (mAllTodoList.size() >= pos) {
                mAllTodoList.add(pos, todo);
            } else {
                Log.e("todo_tree", "insertTodo() position over");
            }
        }
        mTodoMap.put(todo.getId(), todo);
        return pos;
    }

    private int removeTodo(TodoData todo) {
        if (todo.getParent() == null) {
            mRootTodoList.remove(todo);
        }
        int pos = mAllTodoList.indexOf(todo);
        mAllTodoList.remove(todo);
        mTodoMap.remove(todo.getId());
        return pos;
    }

    public ArrayList<TodoData> getAllTodo() {
        return mAllTodoList;
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
        for (TodoData td : mAllTodoList) {
            if (td.getId() > maxId)
                maxId = td.getId();
        }
        TodoData todo = requested.createTodo(maxId + 1);

        int pos = insertTodo(todo);
        if (todo.getParent() != null) {
            todo.getParent().insertChild(todo);
        }

        for (IDataObserver observer : mObservers) {
            observer.onTodoAdded(todo, pos);
        }
    }
    public void deleteTodo(TodoData requested) {
        //TODO use DB
        if (requested.getParent() != null) {
            requested.getParent().removeChild(requested);
        }
        int pos = removeTodo(requested);

        for (IDataObserver observer : mObservers) {
            observer.onTodoRemoved(requested, pos);
        }
    }
    public void updateTodo(RequestTodoData requested) {
        //TODO use DB
        if (requested.id != TodoData.NON_ID) {
            RequestTodoData prev = new RequestTodoData(requested.subject,
                    requested.name, requested.parent, requested.updatedDate);
            prev.setId(requested.id);
            prev.setDueDate(requested.targetDate);

            TodoData target = mTodoMap.get(requested.id);
            target.setSubject(requested.subject);
            target.setName(requested.name);
            //target.setParent(requested.parent); //TODO it can be changed?
            target.setDueDate(requested.targetDate);
            target.setLastUpdated(requested.updatedDate);

            int pos = mAllTodoList.indexOf(target);
            for (IDataObserver observer : mObservers) {
                observer.onTodoUpdated(prev, target, pos);
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
        void onTodoAdded(TodoData added, int position);
        void onTodoRemoved(TodoData removed, int position);
        void onTodoUpdated(RequestTodoData prev, TodoData updated, int position);

        void onSubjectAdded(SubjectData added);
        void onSubjectRemoved(SubjectData removed);
        void onSubjectUpdated(SubjectData prev, SubjectData updated);
    }
}
