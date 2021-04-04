package com.zoiapp.zoi.ModalClasses;

public class ProductDetails
{
    private String code;
    private String name;
    private String qunt;
    private double offer;
    private String catCode;//Category Code
    private String catName;//Catergory Name
    private double price;
    private String serialNumber;


    public ProductDetails(String code, String name, String qunt,String catCode) {
        this.code = code;
        this.name = name;
        this.qunt = qunt;
        this.catCode = catCode;
    }

    public ProductDetails(String code, String name,String qunt, String catCode,double offer) {
        this(code,name,qunt,catCode);
        this.offer = offer;
    }

    public ProductDetails(String code, String qunt) {
        this.code = code;
        this.qunt = qunt;
    }

    public ProductDetails(String catName, String catCode,int random) {
        this.catName = catName;
        this.catCode = catCode;
    }

    public ProductDetails(double price, String qunt) {
        this.price = price;
        this.qunt = qunt;
    }

    public ProductDetails(String serialNumber, String name ,String qunt, double price) {
        this.name = name;
        this.qunt = qunt;
        this.price = price;
        this.serialNumber = serialNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQunt() {
        return qunt;
    }

    public void setQunt(String qunt) {
        this.qunt = qunt;
    }

    public String getCatCode() {
        return catCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public double getOffer() {
        return offer;
    }

    public void setOffer(double offer) {
        this.offer = offer;
    }
}