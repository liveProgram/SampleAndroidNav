package com.live.programming.an21_functionalapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.live.programming.an21_functionalapp.R;
import com.live.programming.an21_functionalapp.Study;
import com.live.programming.an21_functionalapp.StudyActivity;
import com.live.programming.an21_functionalapp.databinding.LayoutRecyclerViewBinding;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class RecyclerUIAdapter extends RecyclerView.Adapter<RecyclerUIAdapter.MyViewHolder> {
    List<Study> list;
    Context context;

    public RecyclerUIAdapter(List<Study> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Study study = list.get(position);
        holder.binding.rTxt.setText(study.getTitle());
        Picasso
                .get()
                .load(study.getImage())
                .error(R.drawable.books)
                .into(holder.binding.rImg);

        holder.binding.getRoot().setOnClickListener(v -> {
            // navigation to an activity with data
            Intent nav = new Intent(context, StudyActivity.class);
            nav.putExtra("all_info", study);
            context.startActivity(nav);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LayoutRecyclerViewBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutRecyclerViewBinding.bind(itemView);
        }
    }
}
