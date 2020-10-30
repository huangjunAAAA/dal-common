package com.rpc.test.model;

import com.boring.dal.config.DalCached;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DalCached
@Table(name = "city")
public class TCity implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "city_id")
    private Integer id;

    @Column(name = "city")
    private String city;

    @Column(name = "country_id")
    private Integer countryId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
