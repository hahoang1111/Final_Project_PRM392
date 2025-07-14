package com.example.final_project_prm392.Domain;

import java.util.Objects;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String profilePicture;
    private String medicalHistory;

    // Empty constructor for Firebase
    public User() {
        this.id = "";
        this.name = "";
        this.email = "";
        this.phone = "";
        this.profilePicture = "";
        this.medicalHistory = "";
    }

    public User(String id, String name, String email, String phone, String profilePicture, String medicalHistory) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profilePicture = profilePicture;
        this.medicalHistory = medicalHistory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(profilePicture, user.profilePicture) &&
                Objects.equals(medicalHistory, user.medicalHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, phone, profilePicture, medicalHistory);
    }
}

