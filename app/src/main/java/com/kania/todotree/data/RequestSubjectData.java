package com.kania.todotree.data;

public class RequestSubjectData {
    public int id;
    public String name;
    public String color;

    public RequestSubjectData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SubjectData createSubject(int newId) {
        if (id == newId) {
            //TODO no not make already created subject
            return null;
        } else {
            return new SubjectData(newId, name, color);
        }
    }
}
