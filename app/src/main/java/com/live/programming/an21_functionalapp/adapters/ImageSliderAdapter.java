package com.live.programming.an21_functionalapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.live.programming.an21_functionalapp.R;
import com.live.programming.an21_functionalapp.databinding.LayoutImageSliderBinding;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.Holder> {

    private List<Integer> imageList = new ArrayList<>();
    private Context ctx;
    private String[] titles = new String[]{"Books", "Explore", "Gaming"};

    public ImageSliderAdapter(Context ctx){
        this.ctx = ctx;
        imageList.add(R.drawable.books);
        imageList.add(R.drawable.icon1);
        imageList.add(R.drawable.gaming);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.layout_image_slider,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        viewHolder.sliderBinding.img.setImageResource(imageList.get(position));
        viewHolder.sliderBinding.txt.setText(titles[position]);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    public class Holder extends ViewHolder {
        LayoutImageSliderBinding sliderBinding;
        public Holder(View itemView) {
            super(itemView);
            sliderBinding = LayoutImageSliderBinding.bind(itemView);
        }
    }
}
