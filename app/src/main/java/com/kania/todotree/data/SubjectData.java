package com.kania.todotree.data;

import android.graphics.Color;

import java.util.ArrayList;

public class SubjectData implements ISubjectData {
    private long mId;
    private String mName;
    private String mColorString;
    private boolean mShowing;

    private ArrayList<ITodoData> mListeners;

    public SubjectData(long id, String name, String color) {
        mId = id;
        mName = name;
        mColorString = color;
        mShowing = true;

        mListeners = new ArrayList<>();
    }

    @Override
    public long getId() { return mId; }

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

    public boolean isShowing() {
        return mShowing;
    }
    public void setShowing(boolean showing) {
        mShowing = showing;
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
