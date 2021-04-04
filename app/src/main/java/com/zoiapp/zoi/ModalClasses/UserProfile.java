package com.zoiapp.zoi.ModalClasses;

public class UserProfile {
    private String userAge;
    private String userEmail;
    private String userName;
    private   int stage;
    private String applicantName;
    private String street;
    private String town;
    private String state;
    private String deliveryAddress;
    private String imageId;
    private String Phn;
    private String address;
    private String listItemNames;
    private String listItemQuantity;
    private String listItemPrize;
    private String listItemSerialNO;
    private String productDetails;
    private int deliveryCharges;
    private String totalPrize;
    private String time;
    private String id;
    private String orderState;
    private String ItemNames,ItemQuantity,ItemPrice;
    private String orderId,date,orderTime,orderDetails;
    private int itemCount,status;
    private boolean isSelected;
    private String martLocation;




    private String userLocation;
    public UserProfile(String userLocation)
    {
        this.userLocation=userLocation;
    }



    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserProfile() {
    }

    public UserProfile(String userAge, String userEmail, String userName) {
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public UserProfile(String orderId,int orderStatus,int orderItemCount,String orderDate,String orderTime)
    {
        this.orderId = orderId;
        status = orderStatus;
        itemCount = orderItemCount;
        date = orderDate;
        this.orderTime = orderTime;
    }



    public UserProfile(int stage) {
        this.stage = stage;
    }

    public UserProfile(String ItemNames,String ItemQuantity){
        this.ItemNames = ItemNames;
        this.ItemQuantity = ItemQuantity;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }


    public String getPhn() {
        return Phn;
    }

    public void setPhn(String phn) {
        Phn = phn;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getListItemNames() {
        return listItemNames;
    }

    public void setListItemNames(String listItemNames) {
        this.listItemNames = listItemNames;
    }

    public String getListItemQuantity() {
        return listItemQuantity;
    }

    public void setListItemQuantity(String listItemQuantity) {
        this.listItemQuantity = listItemQuantity;
    }

    public String getListItemPrize() {
        return listItemPrize;
    }

    public void setListItemPrize(String listItemPrize) {
        this.listItemPrize = listItemPrize;
    }

    public String getListItemSerialNO() {
        return listItemSerialNO;
    }

    public void setListItemSerialNO(String listItemSerialNO) {
        this.listItemSerialNO = listItemSerialNO;
    }

    public String getTotalPrize() {
        return totalPrize;
    }

    public void setTotalPrize(String totalPrize) {
        this.totalPrize = totalPrize;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public  String getItemNames() {
        return ItemNames;
    }

    public void setItemNames(String itemNames) {
        ItemNames = itemNames;
    }

    public  String getItemQuantity() {
        return ItemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        ItemQuantity = itemQuantity;
    }

    public String getItemPrice() {
        return ItemPrice;
    }

    public void setItemPrice(String itemPrice) {
        ItemPrice = itemPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String getMartLocation() {
        return martLocation;
    }

    public void setMartLocation(String martLocation) {
        this.martLocation = martLocation;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public int getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(int deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }
}