package com.kania.todotree;

import android.provider.BaseColumns;

public class TodoTree {
    public static final String TAG = "todo_tree";

    public TodoTree() {}

    public static class SubjectEntry implements BaseColumns {
        public static final String TABLE_NAME = "subject";
        public static final String SUBJECT_NAME = "name";
        public static final String COLOR = "color";
    }

    public static class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String TODO_NAME = "name";
        public static final String SUBJECT_ID = "sub_id";
        public static final String PARENT = "parent";
        public static final String DUEDATE = "date";
        public static final String COMPLETE = "complete";
        public static final String CREATED_DATE = "created_date";
        public static final String LAST_UPDATED_DATE = "last_updated";
    }
}
