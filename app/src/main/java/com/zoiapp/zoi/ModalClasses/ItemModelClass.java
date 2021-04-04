package com.zoiapp.zoi.ModalClasses;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ItemModelClass implements Serializable {
    private String productName,productPrice,imageUrl,productQuantity,productCode,productDetails;
    private String categoryName,categoryCode,categoryDetails;
    private String sliderPagesData;
    private int quantity,productCount;
    private boolean retrivalState  = true;
    private String martLocation;
    private double price;
    private double offer;
    //private String productCount;

    public ItemModelClass() {
    }

    public ItemModelClass(String martLocation) {
        this.martLocation = martLocation;
    }

    public ItemModelClass(String categoryName, String categoryCode, String imageUrl,String martLocation) {
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
        this.martLocation = martLocation;
    }

    public ItemModelClass(String productName, int price, String productQuantity, String imageUrl ) {
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.productQuantity = productQuantity;

    }
    public ItemModelClass(String productName,int quantity, int price, String imageUrl ) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;

    }


  /*  public ItemModelClass(String productName, String productPrice, String imageUrl, String productQuantity) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        this.productQuantity = productQuantity;
       // this.price = price;
    }*/

    public ItemModelClass(String categoryName, String categoryCode, String imageUrl) {
        this.categoryName = categoryName;
        this.categoryCode = categoryCode;
        this.imageUrl = imageUrl;
    }

    public ItemModelClass(String productCode, String productName, String productQuantity, double price,int productCount, String imageUrl, double offer) {
        this.productCode = productCode;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.productQuantity = productQuantity;
        this.price = price;
        this.productCount = productCount;
        this.offer = offer;
    }

    public ItemModelClass( String productCode,String productName,double price, String productQuantity,  String imageUrl) {
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.productQuantity = productQuantity;
        this.productCode = productCode;
        this.price = price;
    }

    public ItemModelClass( String productCode, String productName, double price, String productQuantity,  String imageUrl,double offer) {
        this(productCode,productName,price,productQuantity,imageUrl);
        this.offer = offer;
    }


    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public boolean isRetrivalState() {
        return retrivalState;
    }

    public void setRetrivalState(boolean retrivalState) {
        this.retrivalState = retrivalState;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getMartLocation() {
        return martLocation;
    }

    public void setMartLocation(String martLocation) {
        this.martLocation = martLocation;
    }

    public double getOffer() {
        return offer;
    }

    public void setOffer(double offer) {
        this.offer = offer;
    }

    public String getSliderPagesData() {
        return sliderPagesData;
    }

    public void setSliderPagesData(String sliderPagesData) {
        this.sliderPagesData = sliderPagesData;
    }

    public String getCategoryDetails() {
        return categoryDetails;
    }

    public void setCategoryDetails(String categoryDetails) {
        this.categoryDetails = categoryDetails;
    }

    public static String originalProductName(String name)
    {
        String[] originlName =name.split("_");
        String returnName = "";
        for(int i=0;i<originlName.length-1;i++)
        {
            returnName = returnName+originlName[i]+" ";
        }
        returnName = returnName+originlName[originlName.length-1];
        return returnName;
    }


    public static String rawProductName(String name)
    {
        String[] originlName =name.split(" ");
        String returnName = "";
        for(int i=0;i<originlName.length-1;i++)
        {
            returnName = returnName+originlName[i]+"_";
        }
        returnName = returnName+originlName[originlName.length-1];
        return returnName;
    }
}