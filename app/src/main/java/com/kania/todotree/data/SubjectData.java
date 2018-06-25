package com.kania.todotree.data;

import android.graphics.Color;

import java.util.ArrayList;

public class SubjectData {
    private int mId;
    private String mColorString;

    private ArrayList<ITodoData> mListeners;

    public SubjectData(int id, String color) {
        mId = id;
        mColorString = color;

        mListeners = new ArrayList<>();
    }

    int getId() { return mId; }

    public int getColor() {
        return Color.parseColor(mColorString);
    }
    public void setColor(String color) {
        mColorString = color;
    }

    public void addListener(ITodoData listener) {
        mListeners.add(listener);
    }
    public void removeListener(ITodoData listener) {
        mListeners.remove(listener);
    }

    public void changeColor(String newColor) {
        mColorString = newColor;
        for (ITodoData listener : mListeners)
            listener.onColorUpdated(mColorString);
    }
}
