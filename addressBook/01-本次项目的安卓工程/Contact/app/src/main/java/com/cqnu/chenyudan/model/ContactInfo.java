package com.cqnu.chenyudan.model;

/**
 * 联系人信息模型
 */
public class ContactInfo {
    private String name;
    private String phone;
    private String email;
    private String street;
    private String city;
    private String nickname;
    private String company;
    private String weixin;


    public ContactInfo() {
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", nickname='" + nickname + '\'' +
                ", company='" + company + '\'' +
                ", weixin='" + weixin + '\'' +
                '}';
    }

    /*构造方法*/
    public ContactInfo(String name, String phone, String email, String street, String city, String nickname, String company, String weixin) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.street = street;
        this.city = city;
        this.nickname = nickname;
        this.company = company;
        this.weixin = weixin;
    }

    /*get和set方法*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }
}
