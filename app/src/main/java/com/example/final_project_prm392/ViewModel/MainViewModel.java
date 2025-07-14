package com.example.final_project_prm392.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_project_prm392.Domain.CategoryModel;
import com.example.final_project_prm392.Domain.DoctorsModel;
import com.example.final_project_prm392.Repository.MainRepository;

import java.util.List;

public class MainViewModel extends ViewModel {
    private final MainRepository repository;

    public MainViewModel() {
        this.repository = new MainRepository();
    }

    public LiveData<List<CategoryModel>> loadCategory() {
        return repository.loadCategory();
    }

    public LiveData<List<DoctorsModel>> loadDoctors() {
        return repository.loadDoctor();
    }
}
