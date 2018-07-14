package com.kania.todotree.data;

public class TodoFactory {
    private SubjectData subject;
    private String name;
    private TodoData parent;
    private long insertDate;

    private long targetDate;

    public TodoFactory(SubjectData subject, String name, TodoData parent,
                       long insertDate) {
        this.subject = subject;
        this.name = name;
        this.parent = parent;
        this.insertDate = insertDate;
        targetDate = TodoData.NON_DUEDATE;
    }

    TodoData getTodo(int id) {
        //TODO
        return null;
    }

    public void setTargetDate(long targetDate) {
        this.targetDate = targetDate;
    }
}
