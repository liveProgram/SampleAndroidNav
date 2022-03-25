package com.live.programming.an21_functionalapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.live.programming.an21_functionalapp.R;
import com.live.programming.an21_functionalapp.Study;
import com.live.programming.an21_functionalapp.adapters.ImageSliderAdapter;
import com.live.programming.an21_functionalapp.adapters.RecyclerUIAdapter;
import com.live.programming.an21_functionalapp.databinding.FragmentHomeBinding;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SliderView slider;
    private ShimmerFrameLayout frame;
    RecyclerView rv;
    List<Study> studyList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        slider = binding.imageSlider;
        rv = binding.recyclerView;
        frame = binding.shimmering;

        frame.setVisibility(View.VISIBLE);
        frame.startShimmer();
        rv.setVisibility(View.INVISIBLE);

        GridLayoutManager gridManager = new GridLayoutManager(getActivity(), 1); // defining the manager
        rv.setLayoutManager(gridManager); // setting the manager

        // to apply adapter with data
        fetchAllCollection();

        slider.setAutoCycle(true);
        slider.setSliderAdapter(new ImageSliderAdapter(getActivity()));
        slider.setAutoCycleDirection(View.LAYOUT_DIRECTION_LTR);
        slider.startAutoCycle();
        slider.setScrollTimeInSec(2);
        return root;
    }

    private void fetchAllCollection() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection("study")
                .get() // all document fetch
                .addOnCompleteListener(task -> {
                    if(task.isComplete()){
                        QuerySnapshot qs = task.getResult();
                        List<DocumentSnapshot> list = qs.getDocuments();
                        for (DocumentSnapshot ds: list) {
                            // adding data to model
                            Study study = new Study(ds.getId(), ds.get("title").toString(),
                                    ds.get("desc").toString(), ds.get("image").toString(),
                                    ds.get("level").toString());
                            // adding model to list
                            studyList.add(study);
                        }
                        // adapter call
                        RecyclerUIAdapter adapter = new RecyclerUIAdapter(studyList, getActivity());
                        // set to rv
                        rv.setAdapter(adapter);
                    }else
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    frame.stopShimmer();
                  //  frame.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        frame.stopShimmer();
        binding = null;
    }
}