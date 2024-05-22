package com.example.Giinie;

public class Review {
    private long id;
    private long userId; // Change to userId to match your setup
    private String servicePlanName;
    private String reviewText;
    private float rating;
    private String photoPath;

    public Review(long id, long userId, String servicePlanName, String reviewText, float rating, String photoPath) {
        this.id = id;
        this.userId = userId;
        this.servicePlanName = servicePlanName;
        this.reviewText = reviewText;
        this.rating = rating;
        this.photoPath = photoPath;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getServicePlanName() {
        return servicePlanName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public float getRating() {
        return rating;
    }

    public String getPhotoPath() {
        return photoPath;
    }
}
