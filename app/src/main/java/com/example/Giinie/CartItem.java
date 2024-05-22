package com.example.Giinie;

import java.util.Date;
import java.io.Serializable;

public class CartItem implements Serializable {
    private String serviceName;
    private String servicePlan;
    private Date date;
    private double price;

    public CartItem(String serviceName, String servicePlan, Date date) {
        this.serviceName = serviceName;
        this.servicePlan = servicePlan;
        this.date = date;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServicePlan(String servicePlan) {
        this.servicePlan = servicePlan;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServicePlan() {
        return servicePlan;
    }

    public Date getDate() {
        return date;
    }
}
