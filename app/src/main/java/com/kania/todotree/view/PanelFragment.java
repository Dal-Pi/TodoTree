package com.kania.todotree.view;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kania.todotree.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PanelFragment extends Fragment {

    public PanelFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panel, container, false);
    }
}
