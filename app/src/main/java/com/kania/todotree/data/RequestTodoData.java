package com.kania.todotree.data;

public class RequestTodoData {
    public long id;
    public long subject;
    public String name;
    public long parent;
    public long dueDate;
    public long updatedDate;
    public boolean complete;

    public RequestTodoData(long subject, String name, long parent, long updatedDate) {
        id = TodoData.NON_ID;
        this.subject = subject;
        this.name = name;
        this.parent = parent;
        this.updatedDate = updatedDate;
        dueDate = TodoData.NON_DUEDATE;
        complete = false;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setDueDate(long targetDate) {
        this.dueDate = targetDate;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public TodoData createTodo(long newId) {
        if (id == newId) {
            //TODO no not make already created todo
            return null;
        } else {
            return new TodoData(newId, subject, name, parent,
                    false, dueDate, updatedDate, updatedDate);
        }
    }
}
