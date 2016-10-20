package com.healthtard.swiggyrestaurants;

import java.util.ArrayList;

/**
 * Created by pradeepjeswani on 19/10/16.
 */

public class Restaurant {

    private String name;
    private String city;
    private String area;
    private String rating;
    private String cid;
    private String delivery_time;
    private String costfortwo;
    private boolean closed;
    private ArrayList<String> cusine;
    private ArrayList<RestaurentChain> chain;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getCostfortwo() {
        return costfortwo;
    }

    public void setCostfortwo(String costfortwo) {
        this.costfortwo = costfortwo;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public ArrayList<String> getCusine() {
        return cusine;
    }

    public void setCusine(ArrayList<String> cusine) {
        this.cusine = cusine;
    }

    public ArrayList<RestaurentChain> getChain() {
        return chain;
    }

    public void setChain(ArrayList<RestaurentChain> chain) {
        this.chain = chain;
    }
}
