package com.assignment.todoplanner.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.todoplanner.R;
import com.assignment.todoplanner.adapter.TaskListAdpater;
import com.assignment.todoplanner.database.SharedPreferencesConfig;
import com.assignment.todoplanner.pojos.Task;
import com.assignment.todoplanner.viewmodel.MainViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private final List<Task> taskList = new LinkedList<>();
    private SharedPreferencesConfig preferencesConfig;
    private MainViewModel mainViewModel;
    private TaskListAdpater taskListAdpater;
    private RecyclerView recyclerView;
    private boolean isNewUser;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        preferencesConfig = new SharedPreferencesConfig(requireActivity().getApplicationContext());
        DashboardFragmentArgs args = DashboardFragmentArgs.fromBundle(getArguments());
        this.isNewUser = args.getIsNewUser();
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        recyclerView = view.findViewById(R.id.dashboard_recycler_view);
        taskListAdpater = new TaskListAdpater(taskList, this::onTaskClick, this::onTaskStatusClick);
        TextView emptyText = view.findViewById(R.id.dashboard_body);
        if (isNewUser) {
            Task task = new Task("Welcome " + preferencesConfig.readUserName(), "* Start adding new Tasks by using '+' button" +
                    "\n* Pick suitable color from color palette" +
                    "\n* Delete a task by swiping left/right" +
                    "\n* Mark completed task by clicking the icon on top right" +
                    "\n* Sync your tasks across multiple devices by using same account", R.color.colorRed, false);
            mainViewModel.insertTask(task);
        }
        mainViewModel.getAllTasks().observe(requireActivity(), tasks -> {
            taskList.clear();
            if (!tasks.isEmpty()) {
                taskList.addAll(tasks);
                taskListAdpater.notifyDataSetChanged();
                emptyText.setVisibility(View.GONE);
            } else
                emptyText.setVisibility(View.VISIBLE);

        });
        recyclerView.setAdapter(taskListAdpater);

        setSwipeOnDelete();

        setHasOptionsMenu(true);
        view.findViewById(R.id.dashboard_fab).setOnClickListener(v2 -> navigateToCreateTask());

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Dashboard");
        toolbar.setSubtitle(preferencesConfig.readUserName());
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return view;
    }

    private void onTaskStatusClick(int position) {
        mainViewModel.OnStatusClick(position);
    }

    private void onTaskClick(int position) {
        mainViewModel.OnTaskClick(position);
        navigateToCreateTask();
    }

    private void setSwipeOnDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Task deletedTask = taskList.get(viewHolder.getAdapterPosition());
                taskList.remove(viewHolder.getAdapterPosition());
                taskListAdpater.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar.make(recyclerView, "Deleted Task : " + deletedTask.getTitle(), 5000).setAction("Undo", v ->
                        mainViewModel.insertTask(deletedTask)
                ).show();
                mainViewModel.deleteTask(deletedTask);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void navigateToCreateTask() {
        NavHostFragment.findNavController(DashboardFragment.this)
                .navigate(R.id.action_DashboardFragment_to_TaskFragment);
    }

    private void signOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && getContext() != null) {
            mAuth.signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener((Activity) getContext(), task -> {
                        if (task.isSuccessful()) {
                            preferencesConfig.writeLogInStatus(false);
                            Toast.makeText(getContext(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                            navigateToLogin();
                        }
                    });
        }
    }

    private void navigateToLogin() {
        NavHostFragment.findNavController(DashboardFragment.this)
                .navigate(R.id.action_DashboardFragment_to_LoginFragment);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            signOut();
            return true;
        } else if (itemId == R.id.action_about_me) {
            showDialogBox();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogBox() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
        View mView = getLayoutInflater().inflate(R.layout.about_me_layout, null);
        TextView call, email, github, resume;
        call = mView.findViewById(R.id.about_me_call);
        email = mView.findViewById(R.id.about_me_email);
        github = mView.findViewById(R.id.about_me_github);
        resume = mView.findViewById(R.id.about_me_resume);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        call.setOnClickListener(view -> {
            String uri = "tel:8375983710";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        });
        email.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:ankitv397@gmail.com"));
            startActivity(Intent.createChooser(intent, "Send Email"));
        });
        github.setOnClickListener(view -> {
            String url = "https://github.com/varshneyankit/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
        resume.setOnClickListener(view -> {
            String url = "https://drive.google.com/file/d/1Bw39pvqG-ModIvUYLCqPyZZe-lMA6eVm/view?usp=sharing";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
        alertDialog.show();
    }
}