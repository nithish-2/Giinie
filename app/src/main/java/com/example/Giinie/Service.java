package com.example.Giinie;

import java.io.Serializable;

public class Service implements Serializable {

    private long id;
    private String name;
    private double basicPrice;
    private double standardPrice;
    private double premiumPrice;

    public Service(long id, String name, double basicPrice, double standardPrice, double premiumPrice) {
        this.id = id;
        this.name = name;
        this.basicPrice = basicPrice;
        this.standardPrice = standardPrice;
        this.premiumPrice = premiumPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceBasic() {
        return basicPrice;
    }

    public void setPriceBasic(double priceBasic) {
        this.basicPrice = priceBasic;
    }

    public double getPriceStandard() {
        return standardPrice;
    }

    public void setPriceStandard(double priceStandard) {
        this.standardPrice = priceStandard;
    }

    public double getPricePremium() {
        return premiumPrice;
    }

    public void setPricePremium(double pricePremium) {
        this.premiumPrice = pricePremium;
    }
}
