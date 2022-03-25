package com.live.programming.an21_functionalapp.ui.courses;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.live.programming.an21_functionalapp.R;
import com.live.programming.an21_functionalapp.databinding.FragmentCoursesBinding;


public class CoursesFragment extends Fragment {
    FragmentCoursesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCoursesBinding.inflate(inflater, container, false);

        // add onclick listener to button here

        //getActivity().startActivity();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}