package com.assignment.todoplanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.todoplanner.R;
import com.assignment.todoplanner.interfaces.OnTaskClickListener;
import com.assignment.todoplanner.interfaces.OnTaskStatusClickListener;
import com.assignment.todoplanner.pojos.Task;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskListAdpater extends RecyclerView.Adapter<TaskListAdpater.ViewHolder> {
    private final List<Task> taskList;
    private final OnTaskClickListener taskClickListener;
    private final OnTaskStatusClickListener statusClickListener;

    public TaskListAdpater(List<Task> itemsList, OnTaskClickListener taskClickListener, OnTaskStatusClickListener statusClickListener) {
        this.taskList = itemsList;
        this.taskClickListener = taskClickListener;
        this.statusClickListener = statusClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskCard.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), task.getColor()));

        holder.taskTitle.setText(task.getTitle());
        holder.taskDescription.setText(task.getDescription());
        holder.taskCreationTime.setText(getFormattedDate(task.getCreationTime()));
        if (task.getTaskStatus()) {
            holder.taskStatus.setImageResource(R.drawable.ic_filled_check_circle_32);
            holder.taskCompletionTime.setText(getFormattedDate(task.getCompletionTime()));
            holder.taskCompletionTimeView.setVisibility(View.VISIBLE);
            holder.taskDescription.setMaxLines(2);
            holder.taskCard.setAlpha(0.75f);
            holder.taskCard.setElevation(0f);
            holder.taskCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));
        } else {
            holder.taskStatus.setImageResource(R.drawable.ic_baseline_check_circle_outline_32);
            holder.taskCompletionTimeView.setVisibility(View.GONE);
            holder.taskCard.setAlpha(1f);
            holder.taskDescription.setMaxLines(Integer.MAX_VALUE);
            holder.taskCard.setElevation(8f);
            if (task.getColor() != null)
                holder.taskCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), task.getColor()));
        }
        holder.taskCard.setOnClickListener(view -> taskClickListener.onTaskClick(position));
        holder.taskStatus.setOnClickListener(view -> statusClickListener.onStatusClick(position));

    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss aa", Locale.getDefault());
        return formatter.format(date);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDescription, taskCreationTime, taskCompletionTime;
        MaterialCardView taskCard;
        ImageView taskStatus;
        LinearLayout taskCompletionTimeView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.item_title);
            taskDescription = itemView.findViewById(R.id.item_body);
            taskCard = itemView.findViewById(R.id.item_card);
            taskCompletionTime = itemView.findViewById(R.id.item_completion_time);
            taskCreationTime = itemView.findViewById(R.id.item_creation_time);
            taskStatus = itemView.findViewById(R.id.item_status);
            taskCompletionTimeView = itemView.findViewById(R.id.item_completion_view);
        }
    }
}