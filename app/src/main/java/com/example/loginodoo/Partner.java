package com.example.loginodoo;

import java.util.Map;

public class Partner {
    private Integer id;
    private String name;
    private String street;
    private String street2;
    private String city;
    private String state;
    private Integer stateId;
    private String country;
    private Integer countryId;
    private String phone;
    private String mobile;
    private String fax;
    private String email;
    private String website;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getStreet2() {
        return street2;
    }
    public void setStreet2(String street2) {
        this.street2 = street2;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public Integer getStateId() {
        return stateId;
    }
    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public Integer getCountryId() {
        return countryId;
    }
    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public void setData(Map<String,Object> classObj){
        setId((Integer) classObj.get("id"));
        setName(OdooUtility.getString(classObj, "display_name"));
        setStreet(OdooUtility.getString(classObj, "street"));
        setStreet2(OdooUtility.getString(classObj, "street2"));
        setCity(OdooUtility.getString(classObj, "city"));
        M2OField state_id = OdooUtility.getMany2One(classObj, "state_id");
        setStateId(state_id.id);
        setState(state_id.value);
        M2OField country_id = OdooUtility.getMany2One(classObj,
                "country_id");
        setCountryId(country_id.id);
        setCountry(country_id.value);
        setPhone(OdooUtility.getString(classObj, "phone"));
        setMobile(OdooUtility.getString(classObj, "mobile"));
        setFax(OdooUtility.getString(classObj, "fax"));
        setEmail(OdooUtility.getString(classObj, "email"));
        setWebsite(OdooUtility.getString(classObj, "website"));
    }
}

