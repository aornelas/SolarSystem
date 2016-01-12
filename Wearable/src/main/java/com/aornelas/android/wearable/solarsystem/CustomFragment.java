package com.aornelas.android.wearable.solarsystem;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomFragment extends Fragment {

    public static final String NAME_KEY = "NAME_KEY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final Bundle bundle = getArguments();
        final String name;
        if (bundle != null) {
            name = bundle.getString(NAME_KEY);
        } else {
            name = "";
        }

        View view = inflater.inflate(R.layout.custom_fragment, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(name);

        return view;
    }
}
