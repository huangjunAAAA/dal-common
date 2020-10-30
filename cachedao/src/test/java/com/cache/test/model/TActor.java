package com.cache.test.model;

import com.boring.dal.config.DalCached;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DalCached
@Table(name = "actor")
public class TActor implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "actor_id")
    private Integer id;

    @Column(name = "first_name")
    private String fname;

    @Column(name = "last_name")
    private String lname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }
}
