package com.rasmitap.tailwebs_assigment2.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rasmitap.tailwebs_assigment2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Slider2Fragment extends Fragment {

    TextView txtembarkyourjourney,txtsubtitle2;

    public Slider2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider2, container, false);


        return view;
    }

}
