package com.cenah.buyme.Models;

public class Products {
   private String name,description,price,discount,menuId,image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public Products(String name, String description, String price, String discount, String menuId, String image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.menuId = menuId;
        this.image = image;
    }

    public Products() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
