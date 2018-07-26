package com.kania.todotree.data;

public interface ITodoData extends ISubjectListener {
    long NON_ID = -1;
    long NON_DUEDATE = -1;
    int MAX_DEPTH = 10;

    long getId();
}
