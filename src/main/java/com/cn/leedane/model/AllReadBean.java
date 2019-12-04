package com.cn.leedane.model;

import java.io.Serializable;

/**
 * @author LeeDane
 * 2019年09月24日 15:24
 * Version 1.0
 */
public class AllReadBean implements Serializable {

    public AllReadBean(){}

    public AllReadBean(int number){
        this.number = number;
    }
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
