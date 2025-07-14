package com.example.final_project_prm392.Repository;

import androidx.annotation.NonNull;

import com.example.final_project_prm392.Domain.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private final FirebaseDatabase database;
    private final DatabaseReference categoriesRef;

    public CategoryRepository() {
        this.database = FirebaseDatabase.getInstance();
        this.categoriesRef = database.getReference("Category");
    }

    public interface CategoriesCallback {
        void onSuccess(List<CategoryModel> categories);

        void onFailure(Exception e);
    }

    public interface CategoryCallback {
        void onSuccess(CategoryModel category);

        void onFailure(Exception e);
    }

    public void getAllCategories(CategoriesCallback callback) {
        categoriesRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    List<CategoryModel> categories = new ArrayList<>();
                    for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                        CategoryModel category = childSnapshot.getValue(CategoryModel.class);
                        if (category != null) {
                            categories.add(category);
                        }
                    }
                    callback.onSuccess(categories);
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public void getCategoryById(int categoryId, CategoryCallback callback) {
        categoriesRef.orderByChild("Id").equalTo(categoryId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                                CategoryModel category = childSnapshot.getValue(CategoryModel.class);
                                if (category != null) {
                                    callback.onSuccess(category);
                                    return;
                                }
                            }
                            callback.onFailure(new Exception("Category not found"));
                        } else {
                            callback.onFailure(new Exception("Category not found"));
                        }
                    }
                });
    }
}

