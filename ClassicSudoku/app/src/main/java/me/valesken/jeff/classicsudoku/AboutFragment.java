package me.valesken.jeff.classicsudoku;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jeff on 7/11/2015.
 *
 * This fragment contains the layout for the app's about page. It does very little.
 */
public class AboutFragment extends Fragment {
    public AboutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        getActivity().setTitle(getResources().getString(R.string.app_name).concat(" - About"));

        return rootView;
    }
}
