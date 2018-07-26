package com.kania.todotree.data;

import android.util.Log;

import java.util.ArrayList;

public class TodoData implements ITodoData {

    private long id;
    private long subjectId;
    private String name;
    private long parentId;
    private boolean isCompleted;
    private long dueDate;
    private long created;
    private long lastUpdated;

    private int depth;

    private ArrayList<Long> children;

    public TodoData(long id, long subject, String name, long parent,
                    boolean isCompleted, long dueDate, long created, long lastUpdated) {
        this.id = id;
        this.subjectId = subject;
        this.name = name;
        this.parentId = parent;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.created = created;
        this.lastUpdated = lastUpdated;

        children = new ArrayList<>();
        //TODO
        //evaluateDepth();
    }

    @Override
    public void onColorUpdated(String color) {
        //TODO is it need?
    }

    @Override
    public long getId() {
        return id;
    }

    public long getSubject() {
        return subjectId;
    }
    public void setSubject(long subject) {
        subjectId = subject;
        //TODO
        /*
        this.subject.removeListener(this);
        this.subject = subject;
        this.subject.addListener(this);
        Iterator<TodoData> it = children.iterator();
        while (it.hasNext()) {
            TodoData child = it.next();
            child.setSubject(subject);
        }
        */
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getParent() {
        return parentId;
    }
    public void setParent(long parent) {
        this.parentId = parent;
        //TODO
        //evaluateDepth();
    }

    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    //TODO
    /*
    public boolean isCheckable() {
        boolean ret = true;
        for (TodoData child : children)
            if (child.isCompleted() == false)
                return false;
        return ret;
    }
    */

    public long getDueDate() {
        return dueDate;
    }
    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public long getCreated() {
        return created;
    }
    public void setCreated(long created) {
        this.created = created;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void addSubTodo(long subTodo) {
        children.add(subTodo);
    }
    public void removeSubTodo(long subTodo) {
        children.remove(subTodo);
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
    public int getDepth() {
        return depth;
    }

    public ArrayList<Long> getChildren() {
        return children;
    }
    public void insertChild(long child) {
        Log.d("todo_tree", "[TodoData] added child : " + child + " to " + this.id);
        children.add(child);
    }
    public void removeChild(long child) {
        Log.d("todo_tree", "[TodoData] removed child : " + child + "from " + this.id);
        children.remove(child);
    }

    //TODO
    /*
    private void evaluateDepth() {
        int evaluated = 0;
        TodoData upperTodo = parent;
        while ( (upperTodo != null) && (evaluated < MAX_DEPTH) ) {
            evaluated++;
            upperTodo = upperTodo.parent;
        }
        this.depth = evaluated;
    }
    */

    public boolean isRootTodo() { return (parentId == ISubjectData.NON_ID);}

    //TODO
    /*
    public int getChildrenCount() {
        return getChildrenCountRecur(this);
    }
    private int getChildrenCountRecur(TodoData parentTodo) {
        int evaluated = parentTodo.getChildren().size();
        Log.d("todo_tree", "getChildrenCountRecur() id:" + parentTodo.getId() + ", childrenSize:" + parentTodo.getChildren().size());
        for (TodoData td : parentTodo.getChildren()) {
            evaluated += getChildrenCountRecur(td);
        }
        return evaluated;
    }
    */

    @Override
    public String toString() {
        return "depth:" + depth
                + ", id:" + id
                + ", subject:" + subjectId
                + ", name:" + name
                + ", parent:" + parentId
                + ", check:" + isCompleted
                + ", due:" + dueDate
                + ", create:" + created
                + ", update:" + lastUpdated;
    }
}
