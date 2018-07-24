package com.kania.todotree.data;

public interface ITodoData extends ISubjectListener {
    int NON_ID = -1;
    int NON_DUEDATE = -1;

    long getId();
}
