package com.bih.nic.model;

import java.io.Serializable;

public class Category implements Serializable {
    private String id;
    private String tariffId;


    public String getTariffId() {
        return tariffId;
    }

    public void setTariffId(String tariffId) {
        this.tariffId = tariffId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
