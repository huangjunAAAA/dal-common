package com.boring.dal.test.model;


import com.boring.dal.config.DalCached;

import javax.persistence.*;

@Entity
@DalCached
@Table(name = "country")
public class TCountry {

    @Id
    @GeneratedValue
    @Column(name = "country_id")
    private Integer id;

    @Column(name = "country")
    private String country;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
