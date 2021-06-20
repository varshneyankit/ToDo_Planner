package com.assignment.todoplanner.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.todoplanner.R;
import com.assignment.todoplanner.interfaces.OnColorClickListener;

import java.util.List;

public class ColorPickerAdpater extends RecyclerView.Adapter<ColorPickerAdpater.ViewHolder> {
    private final List<Integer> colors;
    private final OnColorClickListener listener;

    public ColorPickerAdpater(List<Integer> colors, OnColorClickListener listener) {
        this.colors = colors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setBackground(ContextCompat.getColor(holder.itemView.getContext(), colors.get(position)));
        holder.pickerView.setOnClickListener(view -> listener.onColorClick(colors.get(position)));
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View pickerView;
        GradientDrawable gd = new GradientDrawable();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pickerView = itemView.findViewById(R.id.color_item_circle);
            gd.setShape(GradientDrawable.OVAL);
            gd.setStroke(2, Color.BLACK, 0, 0);
            pickerView.setBackground(gd);
        }

        public void setBackground(int color) {
            gd.setColor(color);
        }
    }
}