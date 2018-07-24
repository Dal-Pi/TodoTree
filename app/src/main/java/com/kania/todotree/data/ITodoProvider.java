package com.kania.todotree.data;

public interface ITodoProvider {
    SubjectData getSubject(long id);
    TodoData getTodo(long id);
}
