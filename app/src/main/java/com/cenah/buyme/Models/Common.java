package com.cenah.buyme.Models;

public class Common {

    public static User currnetUser;
    public static final String DELETE = "delete";

    public static String convertCodeToSatus(String status) {

        switch (status) {
            case "0":
                return "was given";
            case "1":
                return "on the way";
            default:
                return "shipped";
        }

    }

}
