package com.cenah.buyme.Models;

public class Rating {
    private String userPhone;
    private String productID;
    private String rateValue;
    private String comment;

    public Rating(String userPhone, String productID, String rateValue, String comment) {
        this.userPhone = userPhone;
        this.productID = productID;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public Rating() {
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }


    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
