package com.kania.todotree.data;

import android.graphics.Color;

import java.util.ArrayList;

public class SubjectData {
    private int mId;
    private String mName;
    private String mColorString;

    private ArrayList<ITodoData> mListeners;

    public SubjectData(int id, String name, String color) {
        mId = id;
        mName = name;
        mColorString = color;

        mListeners = new ArrayList<>();
    }

    int getId() { return mId; }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }

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

    public void onColorChanged(String newColor) {
        mColorString = newColor;
        for (ITodoData listener : mListeners)
            listener.onColorUpdated(mColorString);
    }
}
