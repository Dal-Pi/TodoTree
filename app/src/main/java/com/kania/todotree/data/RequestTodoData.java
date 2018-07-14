package com.kania.todotree.data;

public class RequestTodoData {
    int id;
    public SubjectData subject;
    public String name;
    public TodoData parent;
    public long targetDate;
    public long updatedDate;

    public RequestTodoData(SubjectData subject, String name, TodoData parent,
                           long updatedDate) {
        id = TodoData.NON_ID;
        this.subject = subject;
        this.name = name;
        this.parent = parent;
        this.updatedDate = updatedDate;
        targetDate = TodoData.NON_TARGETDATE;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setTargetDate(long targetDate) {
        this.targetDate = targetDate;
    }

    public TodoData createTodo(int newId) {
        if (id == newId) {
            //TODO no not make already created todo
            return null;
        } else {
            return new TodoData(newId, subject, name, parent,
                    false, targetDate, updatedDate, updatedDate);
        }
    }
}
