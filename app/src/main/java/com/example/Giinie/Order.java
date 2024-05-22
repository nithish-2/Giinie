package com.example.Giinie;

import java.util.Date;

public class Order {
    private long id;
    private long userId;
    private String serviceName;
    private String planName;
    private Date orderDate;
    private String userName;
    private String userPhone;
    private String userAddress;

    public Order(long id, long userId, String serviceName, String planName, Date orderDate, String userName, String userPhone, String userAddress) {
        this.id = id;
        this.userId = userId;
        this.serviceName = serviceName;
        this.planName = planName;
        this.orderDate = orderDate;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPlanName() {
        return planName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }
}
