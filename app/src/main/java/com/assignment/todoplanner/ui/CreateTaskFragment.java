package com.assignment.todoplanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.todoplanner.R;
import com.assignment.todoplanner.adapter.ColorPickerAdpater;
import com.assignment.todoplanner.pojos.Task;
import com.assignment.todoplanner.viewmodel.MainViewModel;

import java.util.LinkedList;
import java.util.List;

public class CreateTaskFragment extends Fragment {
    private static Integer taskColor;
    private LinearLayout rootLayout;
    private MainViewModel mainViewModel;
    private Task currentTask;
    private EditText taskTitle, taskBody;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        taskTitle = view.findViewById(R.id.create_task_title_edit_text);
        taskBody = view.findViewById(R.id.create_task_body_edit_text);
        RecyclerView recyclerView = view.findViewById(R.id.create_task_color_picker_recycler);
        ColorPickerAdpater colorPickerAdpater = new ColorPickerAdpater(getColorsList(), mainViewModel::OnColorClick);
        recyclerView.setAdapter(colorPickerAdpater);
        rootLayout = view.findViewById(R.id.create_task_root_layout);

        mainViewModel.getTaskColor().observe(requireActivity(), color -> {
            taskColor = color;
            if (taskColor != null)
                rootLayout.setBackgroundColor(ContextCompat.getColor(view.getContext(), color));
        });
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.dashboard_fab_description);
        if (mainViewModel.getCurrentTask() != null) {
            toolbar.setTitle("Update Task");
            currentTask = mainViewModel.getCurrentTask();
            taskTitle.setText(currentTask.getTitle());
            taskBody.setText(currentTask.getDescription());
            rootLayout.setBackgroundColor(ContextCompat.getColor(view.getContext(), currentTask.getColor()));
        }
        onBackPressed();
        setHasOptionsMenu(true);
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return view;
    }

    private void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mainViewModel.setCurrentTaskToNull();
                mainViewModel.setTaskColorToNull();
                navigateToDashboard();
            }
        });
    }

    private void saveTask() {
        if (taskTitle != null && taskBody != null) {
            String title = taskTitle.getText().toString().trim();
            String body = taskBody.getText().toString().trim();
            if (!TextUtils.isEmpty(title)) {
                if (currentTask != null) {
                    currentTask.setTitle(title);
                    currentTask.setDescription(body);
                    if (taskColor != null)
                        currentTask.setColor(taskColor);
                    mainViewModel.updateTask(currentTask);
                    Toast.makeText(getContext(), "Task Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Task task;
                    if (taskColor != null)
                        task = new Task(title, body, taskColor, false);
                    else
                        task = new Task(title, body, getColorsList().get(0), false);
                    mainViewModel.insertTask(task);
                    Toast.makeText(getContext(), "Task Created", Toast.LENGTH_SHORT).show();
                }
                mainViewModel.setTaskColorToNull();
                mainViewModel.setCurrentTaskToNull();
                navigateToDashboard();
            } else
                Toast.makeText(getContext(), "Task must have a title !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void navigateToDashboard() {
        CreateTaskFragmentDirections.ActionTaskFragmentToDashboardFragment action = CreateTaskFragmentDirections.actionTaskFragmentToDashboardFragment(false);
        NavHostFragment.findNavController(CreateTaskFragment.this)
                .navigate(action);
    }

    private List<Integer> getColorsList() {
        // Creating list of color
        List<Integer> colorList = new LinkedList<>();
        colorList.add(R.color.white);
        colorList.add(R.color.colorYellow);
        colorList.add(R.color.colorBlue);
        colorList.add(R.color.colorGreenAccent);
        colorList.add(R.color.colorOrange);
        colorList.add(R.color.colorPink);
        colorList.add(R.color.colorPinkAccent);
        colorList.add(R.color.colorRed);
        colorList.add(R.color.colorAccent);

        return colorList;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            saveTask();
            return true;
        } else if (itemId == R.id.action_share) {
            shareTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareTask() {
        String title = taskTitle.getText().toString().trim();
        String body = taskBody.getText().toString().trim();
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n" + body);
            startActivity(Intent.createChooser(shareIntent, "My Task"));
        }
    }
}