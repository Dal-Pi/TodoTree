package com.kania.todotree.data;

import android.util.Log;

import java.util.ArrayList;

public class TodoData implements ITodoData {

    public static final int NON_ID = -1;
    public static final int NON_DUEDATE = -1;
    public static final int MAX_DEPTH = 10;

    private int id;
    private SubjectData subject;
    private String name;
    private TodoData parent;
    private boolean isCompleted;
    private long dueDate;
    private long created;
    private long lastUpdated;

    private int depth;

    private ArrayList<TodoData> children;

    public TodoData(int id, SubjectData subject, String name, TodoData parent,
                    boolean isCompleted, long dueDate, long created, long lastUpdated) {
        this.id = id;
        this.subject = subject;
        this.name = name;
        this.parent = parent;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.created = created;
        this.lastUpdated = lastUpdated;

        children = new ArrayList<>();
        evaluateDepth();
    }

    @Override
    public void onColorUpdated(String color) {
        //TODO is it need?
    }

    @Override
    public int getId() {
        return id;
    }

    public SubjectData getSubject() {
        return subject;
    }
    public void setSubject(SubjectData subject) {
        this.subject.removeListener(this);
        this.subject = subject;
        this.subject.addListener(this);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public TodoData getParent() {
        return parent;
    }
    public void setParent(TodoData parent) {
        this.parent = parent;
        evaluateDepth();
    }

    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

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

    public void addSubTodo(TodoData subTodo) {
        children.add(subTodo);
    }
    public void removeSubTodo(TodoData subTodo) {
        children.remove(subTodo);
    }

    public int getDepth() {
        return depth;
    }

    public ArrayList<TodoData> getChildren() {
        return children;
    }
    public void insertChild(TodoData child) {
        Log.d("todo_tree", "insertChild() added child : " + child.getId());
        children.add(child);
    }
    public void removeChild(TodoData child) {
        Log.d("todo_tree", "removeChild() removed child : " + child.getId());
        children.remove(child);
    }

    private void evaluateDepth() {
        int evaluated = 0;
        TodoData upperTodo = parent;
        while ( (upperTodo != null) && (evaluated < MAX_DEPTH) ) {
            evaluated++;
            upperTodo = upperTodo.parent;
        }
        this.depth = evaluated;
    }

    public boolean isRootTodo() { return (parent == null);}

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

    @Override
    public String toString() {
        return "depth:" + depth
                + ", id:" + id
                + ", subject:" + subject.getId()
                + ", name:" + name
                + ", parent:" + ((parent != null) ? parent.getId() : NON_ID)
                + ", check:" + isCompleted
                + ", due:" + dueDate
                + ", create:" + created
                + ", update:" + lastUpdated;
    }
}
