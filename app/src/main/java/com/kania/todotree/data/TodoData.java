package com.kania.todotree.data;

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
    private long targetDate;
    private long created;
    private long lastUpdated;

    private int depth;

    private ArrayList<TodoData> children;

    public TodoData(int id, SubjectData subject, String name, TodoData parent,
                    boolean isCompleted, long targetDate, long created, long lastUpdated) {
        this.id = id;
        this.subject = subject;
        this.name = name;
        this.parent = parent;
        this.isCompleted = isCompleted;
        this.targetDate = targetDate;
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

    public long getTargetDate() {
        return targetDate;
    }
    public void setTargetDate(long targetDate) {
        this.targetDate = targetDate;
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

    private void evaluateDepth() {
        int evaluated = 0;
        TodoData upperTodo = parent;
        while ( (upperTodo != null) && (evaluated < MAX_DEPTH) ) {
            evaluated++;
            upperTodo = upperTodo.parent;
        }
        this.depth = evaluated;
    }
}
