package com.example.final_project_prm392.Domain;

import java.util.Date;
import java.util.Objects;

public class Appointment {
    private String id;
    private int doctorId;
    private String userId;
    private Date date;
    private String time;
    private String status; // pending, confirmed, completed, cancelled
    private String service;
    private String notes;
    private Date createdAt;

    // Empty constructor for Firebase
    public Appointment() {
        this.id = "";
        this.doctorId = 0;
        this.userId = "";
        this.date = new Date();
        this.time = "";
        this.status = "pending";
        this.service = "";
        this.notes = "";
        this.createdAt = new Date();
    }

    public Appointment(String id, int doctorId, String userId, Date date, String time, String status, String service, String notes, Date createdAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.service = service;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return doctorId == that.doctorId &&
                Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(date, that.date) &&
                Objects.equals(time, that.time) &&
                Objects.equals(status, that.status) &&
                Objects.equals(service, that.service) &&
                Objects.equals(notes, that.notes) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, doctorId, userId, date, time, status, service, notes, createdAt);
    }
}

