package com.kania.todotree.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todotree.TodoTree;
import com.kania.todotree.data.QueryTask.SubjectUpdateTask;
import com.kania.todotree.data.QueryTask.SubjectReadTask;
import com.kania.todotree.data.QueryTask.TodoUpdateTask;
import com.kania.todotree.data.QueryTask.TodoReadTask;
import com.kania.todotree.view.TodoDateUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class TodoProvider implements ITodoProvider {

    public static int NO_SELECTED = -1;
    private static TodoProvider mInstance;

    private ArrayList<TodoData> mRootTodoList;
    private ArrayList<TodoData> mAllTodoList;
    private HashMap<Long, TodoData> mTodoMap;
    private ArrayList<SubjectData> mSubjectList;
    private HashMap<Long, SubjectData> mSubjectMap;
    private long mSelected = NO_SELECTED;

    private ArrayList<IDataObserver> mObservers;

    public static TodoProvider getInstance() {
        if (mInstance == null)
            mInstance = new TodoProvider();
        return mInstance;
    }
    private TodoProvider() {
        Log.d(TodoTree.TAG, "TodoProvider constructor initiated");
        mObservers = new ArrayList<>();

        mSubjectList = new ArrayList<>();
        mSubjectMap = new HashMap<>();

        mRootTodoList = new ArrayList<>();
        mAllTodoList = new ArrayList<>();
        mTodoMap = new HashMap<>();
    }

    public void loadAllData(Context context) {
        Log.d(TodoTree.TAG, "[loadAllData] TodoProvider data loading...");
        SubjectReadTask subjectReadTask = new SubjectReadTask(context, new SubjectReadTask.SubjectReadTaskListener() {
            @Override
            public void onReadSubject(ArrayList<SubjectData> results) {
                ArrayList<Long> ret = new ArrayList<>();
                for (SubjectData subject : results) {
                    insertSubject(subject);
                    ret.add(subject.getId());
                }
                for (IDataObserver observer : mObservers) {
                    observer.onSubjectUpdated(ret);
                }
            }
        });
        subjectReadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        TodoReadTask todoReadTask = new TodoReadTask(context, new TodoReadTask.TodoReadTaskListener() {
            @Override
            public void onReadTodo(ArrayList<TodoData> results) {
                ArrayList<Long> ret = arrangeTodo(results);
                for (IDataObserver observer : mObservers) {
                    //observer.onTodoUpdated(ret);
                    observer.onTodoAdded(ret);
                }

                //debug
                logAllTodo();
            }
        });
        todoReadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        Log.d(TodoTree.TAG, "[loadAllData] TodoProvider data loading complete");
    }

    private ArrayList<Long> arrangeTodo(ArrayList<TodoData> todos) {
        ArrayList<Long> ret = new ArrayList<>();
        for (TodoData todo : todos)
        {
            ret.add(todo.getId());
            if (todo.getParent() == TodoData.NON_ID)
                mRootTodoList.add(todo);
            mTodoMap.put(todo.getId(), todo);
        }
        for (long todoId : mTodoMap.keySet())
        {
            TodoData todo = mTodoMap.get(todoId);
            if (todo.getParent() != TodoData.NON_ID)
                mTodoMap.get(todo.getParent()).insertChild(todoId);
        }
        for (TodoData root : mRootTodoList) {
            root.setDepth(0);
            arrangeTodoRecur(root);
        }
        return ret;
    }
    private void arrangeTodoRecur(TodoData target) {
        mAllTodoList.add(target);
        for (long todoId : target.getChildren()) {
            TodoData todo = mTodoMap.get(todoId);
            todo.setDepth(target.getDepth() + 1);
            arrangeTodoRecur(todo);
        }
    }

    @Override
    public SubjectData getSubject(long id) {
        return mSubjectMap.get(id);
    }

    @Override
    public TodoData getTodo(long id) {
        return mTodoMap.get(id);
    }

    public int getIndex(TodoData todo) {
        return mAllTodoList.indexOf(todo);
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
        mTodoMap.put(todo.getId(), todo);
        int pos;
        if (todo.getParent() == ITodoData.NON_ID) {
            Log.d("todo_tree", "[insertTodo] " + todo.getId() + "is the root Todo");
            pos = 0;
            mRootTodoList.add(pos, todo);
            mAllTodoList.add(pos, todo);
        } else {
            TodoData parent = mTodoMap.get(todo.getParent());
            Log.d("todo_tree", "[insertTodo] find parent:" + parent.getId());
            int childrenCount = getChildrenCount(parent);
            Log.d("todo_tree", "[insertTodo] " + parent.getId() + "'s children count = " + childrenCount);
            parent.insertChild(todo.getId());
            evaluateDepth(todo);
            pos = mAllTodoList.indexOf(parent) + childrenCount + 1;
            if (mAllTodoList.size() >= pos) {
                mAllTodoList.add(pos, todo);
            } else {
                Log.e("todo_tree", "[insertTodo] position over");
            }
        }
    }

    private void evaluateDepth(TodoData todo) {
        int evaluated = 0;
        TodoData targetTodo = todo;
        //TODO max depth
        while ( (targetTodo.getParent() != TodoData.NON_ID) /* && (evaluated < ITodoData.MAX_DEPTH) */) {
            evaluated++;
            targetTodo = mTodoMap.get(targetTodo.getParent());
        }
        todo.setDepth(evaluated);
    }

    private int removeTodo(TodoData todo) {
        if (todo.isRootTodo()) {
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

    public long getSelected() {
        return mSelected;
    }
    public void select(long id) {

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
            Log.e("todo_tree", "[TodoProvider] do not find selected id : " + id);
            cancelSelect();
        }
    }

    public void cancelSelect() {
        mSelected = NO_SELECTED;
    }

    public void editTodo(Context context, RequestTodoData requested) {
        if (requested.id == TodoData.NON_ID)
            addTodo(context, requested);
        else
            updateTodo(context, requested);

    }
    private void addTodo(Context context, RequestTodoData requested) {
        ArrayList<RequestTodoData> requests = new ArrayList<>();
        requests.add(requested);

        TodoUpdateTask createTask = new TodoUpdateTask(context, new TodoUpdateTask.TodoCreateTaskListener() {
            @Override
            public void onProgressChanged(int completed, int max) {
                //TODO make progress using data
            }
            @Override
            public void onCreatedTodo(ArrayList<TodoData> creates) {
                ArrayList<Long> ret = new ArrayList<>();
                for (TodoData added : creates) {
                    ret.add(added.getId());
                    insertTodo(added);
                }
                for (IDataObserver observer : mObservers) {
                    observer.onTodoAdded(ret);
                }
            }

            @Override
            public void onUpdatedTodo(ArrayList<RequestTodoData> updates) {
                replaceUpdateTodos(updates);
            }
        });
        createTask.setAddData(requests);
        createTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void updateTodo(Context context, RequestTodoData requested) {
        ArrayList<RequestTodoData> requests = new ArrayList<>();
        requests.add(requested);

        TodoUpdateTask createTask = new TodoUpdateTask(context, new TodoUpdateTask.TodoCreateTaskListener() {
            @Override
            public void onProgressChanged(int completed, int max) {
                //TODO make progress using data
            }
            @Override
            public void onCreatedTodo(ArrayList<TodoData> creates) {
                //do nothing
            }

            @Override
            public void onUpdatedTodo(ArrayList<RequestTodoData> updates) {
                replaceUpdateTodos(updates);
            }
        });
        createTask.setEditData(requests);
        createTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void completeTodo(Context context, long requestTodoId, boolean completed) {
        TodoData target = mTodoMap.get(requestTodoId);
        RequestTodoData request = new RequestTodoData(target.getSubject(), target.getName(),
                target.getParent(), TodoDateUtil.getCurrent());
        request.setId(requestTodoId);
        request.setDueDate(target.getDueDate());
        //only here changed
        request.setComplete(completed);
        ArrayList<RequestTodoData> completes = new ArrayList<>();
        completes.add(request);

        //TODO merge
        TodoUpdateTask createTask = new TodoUpdateTask(context, new TodoUpdateTask.TodoCreateTaskListener() {
            @Override
            public void onProgressChanged(int completed, int max) {
                //TODO make progress using data
            }
            @Override
            public void onCreatedTodo(ArrayList<TodoData> creates) {
                //do nothing
            }

            @Override
            public void onUpdatedTodo(ArrayList<RequestTodoData> updates) {
                replaceUpdateTodos(updates);
            }
        });
        createTask.setEditData(completes);
        createTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void replaceUpdateTodos(ArrayList<RequestTodoData> updates) {
        ArrayList<Long> ret = new ArrayList<>();
        for (RequestTodoData updated : updates) {
            ret.add(updated.id);
            TodoData old = mTodoMap.get(updated.id);
            old.setName(updated.name);
            old.setSubject(updated.subject);
            //TODO parent can change?
            old.setCompleted(updated.complete);
            old.setDueDate(updated.dueDate);
            old.setLastUpdated(updated.updatedDate);
        }
        for (IDataObserver observer : mObservers) {
            observer.onTodoUpdated(ret);
        }
    }

    /*
    public void deleteTodo(Context context, long requestTodoId) {
        //TODO use DB
        TodoData requested = mTodoMap.get(requestTodoId);
        if (requested == null) {
            Log.e(TodoTree.TAG, "already deleted todo. id:" + requestTodoId);
        }

        ArrayList<Long> deleteTodoList = new ArrayList<>();
        setDeleteTodoList(deleteTodoList, requested);
        //debug
        for (TodoData todo : deleteTodoList)
            Log.d(TodoTree.TAG, "deletelist : " + todo.toString());

        for (TodoData todo : deleteTodoList) {
            //TODO use DB
            //delete DB

            //delete on list
            //disconnect from parent
            if (todo.isRootTodo() == false)
                todo.getParent().removeChild(todo);
            removeTodo(todo);
        }

        for (IDataObserver observer : mObservers) {
            observer.onTodoRemoved(requested);
        }
        Log.d("todo_tree", "deleteTodo() deleted todo id:" + requestTodoId);
    }
    */

    private void setDeleteTodoList(ArrayList<Long> deleteTodoList, long targetId) {
        TodoData target = mTodoMap.get(targetId);
        for (long todoId : target.getChildren())
            setDeleteTodoList(deleteTodoList, todoId);
        deleteTodoList.add(targetId);
    }

    private int getChildrenCount(TodoData todo) {
        return getChildrenCountRecur(todo);
    }
    private int getChildrenCountRecur(TodoData todo) {
        int evaluated = todo.getChildren().size();
        Log.d("todo_tree", "getChildrenCountRecur() id:" + todo.getId() + ", childrenSize:" + todo.getChildren().size());
        for (long td : todo.getChildren()) {
            evaluated += getChildrenCountRecur(mTodoMap.get(td));
        }
        return evaluated;
    }

    public boolean isCheckable(long id) {
        boolean ret = true;
        TodoData todo = mTodoMap.get(id);
        for (long childId : todo.getChildren())
            if (mTodoMap.get(childId).isCompleted() == false)
                return false;
        return ret;
    }

    public void addSubject(Context context, RequestSubjectData requested) {
        ArrayList<RequestSubjectData> requests = new ArrayList<>();
        requests.add(requested);

        SubjectUpdateTask createTask = new SubjectUpdateTask(context, new SubjectUpdateTask.SubjectCreateTaskListener() {
            @Override
            public void onProgressChanged(int completed, int max) {
                //TODO make progress using data
            }
            @Override
            public void onCreatedSubject(ArrayList<SubjectData> updates) {
                ArrayList<Long> ret = new ArrayList<>();
                for (SubjectData added : updates) {
                    insertSubject(added);
                    ret.add(added.getId());
                }
                for (IDataObserver observer : mObservers) {
                    observer.onSubjectAdded(ret);
                }
            }
        });
        createTask.setData(requests);
        createTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
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

    //TODO remove debug
    public void logAllTodo() {
        Log.d(TodoTree.TAG, "-----Todo List start-----");
        for (TodoData rootTodo : mRootTodoList) {
            logTodoRecur(rootTodo);
        }
        Log.d(TodoTree.TAG, "-----Todo List end-----");
    }
    private void logTodoRecur(TodoData target) {
        String depth = "";
        for (int i = 0; i < target.getDepth(); ++i) {
            depth += " ";
        }
        Log.d(TodoTree.TAG, depth + target.toString());
        for (long todoId : target.getChildren()) {
            TodoData todo = mTodoMap.get(todoId);
            logTodoRecur(todo);
        }
    }

    public interface IDataObserver {
        void onTodoAdded(ArrayList<Long> creates);
        void onTodoRemoved(ArrayList<Long> removes);
        //TODO send request datas
        void onTodoUpdated(ArrayList<Long> updates);

        void onSubjectAdded(ArrayList<Long> creates);
        void onSubjectRemoved(ArrayList<Long> removes);
        //TODO send request datas
        void onSubjectUpdated(ArrayList<Long> updates);
    }
}
