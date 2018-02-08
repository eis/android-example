package com.github.eis.myandroidapp.slide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.eis.myandroidapp.R;

public class ScreenSlidePagerFragment extends Fragment {

    private TextView textView;
    private String text = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);
        textView = rootView.findViewById(R.id.textcontent);
        textView.setText(text);

        return rootView;
    }

    public void setText(String text) {
        this.text = text;
    }
}
