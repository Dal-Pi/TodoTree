package com.kania.todotree.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todotree.TodoTree;
import com.kania.todotree.data.QueryTask.SubjectUpdateTask;
import com.kania.todotree.data.QueryTask.SubjectReadTask;
import com.kania.todotree.data.QueryTask.TodoDeleteTask;
import com.kania.todotree.data.QueryTask.TodoUpdateTask;
import com.kania.todotree.data.QueryTask.TodoReadTask;
import com.kania.todotree.view.utils.TodoDateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TodoProvider implements ITodoProvider {

    private static TodoProvider mInstance;

    private ArrayList<TodoData> mRootTodoList;
    private ArrayList<TodoData> mAllTodoList;
    private ArrayList<TodoData> mShowingTodoList;
    private HashMap<Long, TodoData> mTodoMap;
    private ArrayList<SubjectData> mSubjectList;
    private HashMap<Long, SubjectData> mSubjectMap;

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

        mShowingTodoList = new ArrayList<>();
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
                arrangeTodo(results);
                for (IDataObserver observer : mObservers) {
                    ArrayList<TodoData> ret = new ArrayList<>();
                    for (TodoData todo : mShowingTodoList)
                        ret.add(todo);
                    observer.onTodoAdded(ret);
                }

                //TODO debug
                //logAllTodo();
            }
        });
        todoReadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        Log.d(TodoTree.TAG, "[TodoProvider::loadAllData] data loading complete");
    }

    private void arrangeTodo(ArrayList<TodoData> todos) {
        for (TodoData todo : todos)
        {
            if (todo.isRootTodo())
                mRootTodoList.add(todo);
            mTodoMap.put(todo.getId(), todo);
        }
        for (long todoId : mTodoMap.keySet())
        {
            TodoData todo = mTodoMap.get(todoId);
            if (todo.isRootTodo() == false)
                mTodoMap.get(todo.getParent()).insertChild(todoId);
        }
        for (TodoData root : mRootTodoList) {
            root.setDepth(0);
            arrangeTodoRecur(root);
        }
        updateShowingList();
    }
    private void arrangeTodoRecur(TodoData target) {
        mAllTodoList.add(target);
        for (long todoId : target.getChildren()) {
            TodoData todo = mTodoMap.get(todoId);
            todo.setDepth(target.getDepth() + 1);
            arrangeTodoRecur(todo);
        }
    }

    private void updateShowingList() {
        mShowingTodoList.clear();
        for (TodoData todo : mAllTodoList) {
            if (mSubjectMap.get(todo.getSubject()).isShowing()) {
                mShowingTodoList.add(todo);
            }
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
        return mShowingTodoList.indexOf(todo);
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
            mShowingTodoList.add(pos, todo);
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
                int showingPos = mShowingTodoList.indexOf(parent) + childrenCount + 1;
                mShowingTodoList.add(showingPos, todo);
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

    //TODO change as ID
    private void removeTodo(TodoData todo) {
        if (todo.isRootTodo() == false) {
            TodoData parent = mTodoMap.get(todo.getParent());
            parent.removeChild(todo.getId());
        }
        if (todo.isRootTodo()) {
            mRootTodoList.remove(todo);
        }
        mShowingTodoList.remove(todo);
        mAllTodoList.remove(todo);
        mTodoMap.remove(todo.getId());
    }

    public ArrayList<TodoData> getAllTodo() {
        return mAllTodoList;
    }

    public ArrayList<SubjectData> getAllSubject() {
        return mSubjectList;
    }

    public ArrayList<TodoData> getShowingTodoList() {
        return mShowingTodoList;
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

        TodoUpdateTask createTask = new TodoUpdateTask(context, new TodoUpdateTask.TodoUpdateTaskListener() {
            @Override
            public void onProgressChanged(int completed, int max) {
                //TODO make progress using data
            }
            @Override
            public void onCreatedTodo(ArrayList<TodoData> creates) {
                handleAddResult(creates);
            }

            @Override
            public void onUpdatedTodo(ArrayList<RequestTodoData> updates) {
                replaceUpdateTodos(updates);
            }
        });
        createTask.setAddData(requests);
        createTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void handleAddResult(ArrayList<TodoData> creates) {
        ArrayList<TodoData> ret = new ArrayList<>();
        for (TodoData added : creates) {
            ret.add(added);
            insertTodo(added);
        }
        for (IDataObserver observer : mObservers) {
            observer.onTodoAdded(ret);
        }
    }

    private void updateTodo(Context context, RequestTodoData requested) {
        ArrayList<RequestTodoData> requests = new ArrayList<>();
        TodoData target = mTodoMap.get(requested.id);
        //TODO refector!
        if (target.isRootTodo() && (target.getSubject() != requested.subject)) {
            ArrayList<Long> allTodoList = new ArrayList<>();
            setAllChildTodoList(allTodoList, requested.id);
            for (Long eachTodoId : allTodoList) {
                TodoData eachTodo = mTodoMap.get(eachTodoId);
                RequestTodoData eachRequest;
                if (eachTodo.isRootTodo()) {
                    eachRequest = new RequestTodoData(requested.subject, requested.name, requested.parent, requested.updatedDate);
                    eachRequest.setDueDate(requested.dueDate);
                    eachRequest.setComplete(requested.complete);
                    eachRequest.setId(requested.id);
                } else {
                    eachRequest = new RequestTodoData(requested.subject, eachTodo.getName(), eachTodo.getParent(), eachTodo.getLastUpdated());
                    eachRequest.setDueDate(eachTodo.getDueDate());
                    eachRequest.setComplete(eachTodo.isCompleted());
                    eachRequest.setId(eachTodo.getId());
                }
                requests.add(eachRequest);
            }
        } else {
            requests.add(requested);
        }

        TodoUpdateTask createTask = new TodoUpdateTask(context, new TodoUpdateTask.TodoUpdateTaskListener() {
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
                handleUpdateTodo(updates);
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
        TodoUpdateTask createTask = new TodoUpdateTask(context, new TodoUpdateTask.TodoUpdateTaskListener() {
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
                handleUpdateTodo(updates);
            }
        });
        createTask.setEditData(completes);
        createTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void handleUpdateTodo(ArrayList<RequestTodoData> updates) {
        ArrayList<RequestTodoData> origins = new ArrayList<>();
        ArrayList<TodoData> updatedTodos;
        //debug
        for (RequestTodoData requested : updates) {
            Log.d(TodoTree.TAG, "[TodoProvider::handleUpdateTodo] requested id " + requested.id);
        }
        for (RequestTodoData requested : updates) {
            TodoData origin = mTodoMap.get(requested.id);
            //debug
            Log.d(TodoTree.TAG, "[TodoProvider::handleUpdateTodo] origin : " + origin.toString());
            RequestTodoData originData = new RequestTodoData(origin.getSubject(), origin.getName(), origin.getParent(), origin.getDueDate());
            originData.setDueDate(origin.getDueDate());
            originData.setComplete(origin.isCompleted());
            originData.setId(origin.getId());
            origins.add(originData);
        }

        updatedTodos = replaceUpdateTodos(updates);

        for (IDataObserver observer : mObservers) {
            observer.onTodoUpdated(origins, updatedTodos);
        }
    }

    private ArrayList<TodoData> replaceUpdateTodos(ArrayList<RequestTodoData> updates) {
        ArrayList<TodoData> ret = new ArrayList<>();
        for (RequestTodoData updated : updates) {
            TodoData old = mTodoMap.get(updated.id);
            old.setName(updated.name);
            old.setSubject(updated.subject);
            //TODO parent can change?
            old.setCompleted(updated.complete);
            old.setDueDate(updated.dueDate);
            old.setLastUpdated(updated.updatedDate);
            ret.add(old);
        }
        return ret;
    }

    public void deleteTodo(Context context, long requestTodoId) {
        TodoData requested = mTodoMap.get(requestTodoId);
        if (requested == null) {
            Log.e(TodoTree.TAG, "[TodoProvider::deleteTodo] already deleted todo. id:" + requestTodoId);
        }
        ArrayList<Long> deleteTodoList = new ArrayList<>();
        setAllChildTodoList(deleteTodoList, requestTodoId);

        TodoDeleteTask todoDeleteTask = new TodoDeleteTask(context, new TodoDeleteTask.TodoDeleteTaskListener() {
            @Override
            public void onProgressChanged(int completed, int max) {
                //TODO make progress using data
            }

            @Override
            public void onDeletedTodo(ArrayList<Long> deletes) {
                ArrayList<Integer> deletePositions = new ArrayList<>();
                HashSet<Long> updateCandidate = new HashSet<>();
                for (long deleted : deletes) {
                    Log.d(TodoTree.TAG, "[TodoProvider::deleteTodo] deleted id:" + deleted);
                    TodoData todo = mTodoMap.get(deleted);
                    deletePositions.add(mShowingTodoList.indexOf(todo));
                    if (todo.getParent() != TodoData.NON_ID)
                        updateCandidate.add(todo.getParent());
                    removeTodo(todo);
                }
                for (long deleted : deletes) {
                    updateCandidate.remove(deleted);
                }
                //debug
                Log.d(TodoTree.TAG, "[TodoProvider::deleteTodo] updateCandidate size :" + updateCandidate.size());
                for (IDataObserver observer : mObservers) {
                    observer.onTodoRemoved(deletePositions, updateCandidate);
                }
            }
        });
        todoDeleteTask.setDeleteData(deleteTodoList);
        todoDeleteTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void setSubjectVisibility(List<Long> subjectIds, boolean showing) {
        for (long subjectId : subjectIds) {
            if (subjectId == SubjectData.NON_ID)
                Log.e(TodoTree.TAG, "[TodoProvider::setSubjectVisibility] invalid id selected");
            else
                mSubjectMap.get(subjectId).setShowing(showing);
        }
        updateShowingList();
        for (IDataObserver observer : mObservers)
            observer.onRefreshTodo();
    }

    private void setAllChildTodoList(ArrayList<Long> deleteTodoList, long targetId) {
        TodoData target = mTodoMap.get(targetId);
        for (long todoId : target.getChildren())
            setAllChildTodoList(deleteTodoList, todoId);
        deleteTodoList.add(targetId);
    }

    private int getChildrenCount(TodoData todo) {
        return getChildrenCountRecur(todo);
    }
    private int getChildrenCountRecur(TodoData todo) {
        int evaluated = todo.getChildren().size();
        Log.d(TodoTree.TAG, "getChildrenCountRecur() id:" + todo.getId() + ", childrenSize:" + todo.getChildren().size());
        for (long td : todo.getChildren()) {
            evaluated += getChildrenCountRecur(mTodoMap.get(td));
        }
        return evaluated;
    }

    public boolean isCheckable(long id) {
        boolean ret = true;
        //debug
        Log.d(TodoTree.TAG, "[TodoProvider::isCheckable] check id :" + id);
        TodoData todo = mTodoMap.get(id);
        if (todo == null) {
            Log.e(TodoTree.TAG, "[TodoProvider::isCheckable] todo is null");
        }
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
        void onTodoAdded(ArrayList<TodoData> creates);
        void onTodoUpdated(ArrayList<RequestTodoData> origins, ArrayList<TodoData> updates);
        //TODO refector!
        void onTodoRemoved(ArrayList<Integer> removePositions, HashSet<Long> parents);
        void onRefreshTodo();

        void onSubjectAdded(ArrayList<Long> creates);
        void onSubjectRemoved(ArrayList<Long> removes);
        //TODO send request datas
        void onSubjectUpdated(ArrayList<Long> updates);
    }
}
