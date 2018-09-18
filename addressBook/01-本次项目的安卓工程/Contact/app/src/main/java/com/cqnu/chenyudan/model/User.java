package com.cqnu.chenyudan.model;

/**
 * 账户模型类
 */
public class User {
    private String number;
    private String password;

    public User(){

    }

    /*构造方法*/
    public User(String number, String password) {
        this.number = number;
        this.password = password;
    }


    /*get和set方法*/
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
