package com.assignment.todoplanner.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.assignment.todoplanner.database.AppDatabase;
import com.assignment.todoplanner.database.SharedPreferencesConfig;
import com.assignment.todoplanner.pojos.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> taskColor = new MutableLiveData<>();
    private final SharedPreferencesConfig preferencesConfig;
    private Task currentTask;
    private final MutableLiveData<List<Task>> dataList = new MutableLiveData<>();
    private CollectionReference firestore;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        preferencesConfig = new SharedPreferencesConfig(getApplication().getApplicationContext());
        updateUser();
    }

    public void updateUser() {
        firestore = FirebaseFirestore.getInstance().collection("users")
                .document(preferencesConfig.readUserEmail()).collection("tasks");
    }

    public MutableLiveData<List<Task>> getAllTasks() {
        firestore.orderBy("creationTime", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        Log.e("TAG", "getAllTasks: " + error.getMessage());
                    } else {
                        List<DocumentSnapshot> documents = value.getDocuments();
                        List<Task> taskList = new LinkedList<>();
                        documents.forEach(documentSnapshot -> {
                            Task task = documentSnapshot.toObject(Task.class);
                            if (task != null)
                                taskList.add(task);
                        });
                        dataList.setValue(taskList);
                    }
                });
        return dataList;
    }

    public void insertTask(Task task) {
        DocumentReference document = firestore.document();
        task.setId(document.getId());
        document.set(task).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(getApplication().getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT);
            }
        });
    }

    public void updateTask(Task task) {
        firestore.document(task.getId()).set(task)
                .addOnSuccessListener(unused -> Toast.makeText(getApplication().getApplicationContext(), "Data Updated", Toast.LENGTH_SHORT))
                .addOnFailureListener(e -> Toast.makeText(getApplication().getApplicationContext(), "Data Not Updated: " + e.getMessage(), Toast.LENGTH_SHORT));
    }

    public void deleteTask(Task task) {
        firestore.document(task.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Log.e("TAG", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.e("TAG", "Error deleting document", e));
    }

    public void OnTaskClick(int position) {
        currentTask = Objects.requireNonNull(dataList.getValue()).get(position);
    }

    public void OnStatusClick(int position) {
        Task task = Objects.requireNonNull(dataList.getValue()).get(position);
        if (task.getTaskStatus()) {
            task.setTaskStatus(false);
        } else {
            task.setTaskStatus(true);
            task.setCompletionTime(Timestamp.now().toDate());
        }
        updateTask(task);
        getAllTasks();
    }

    public void OnColorClick(int color) {
        taskColor.setValue(color);
    }

    public MutableLiveData<Integer> getTaskColor() {
        return taskColor;
    }

    public void setTaskColorToNull() {
        taskColor.setValue(null);
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTaskToNull() {
        currentTask = null;
    }
}