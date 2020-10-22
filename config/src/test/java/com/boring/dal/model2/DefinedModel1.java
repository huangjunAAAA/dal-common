package com.boring.dal.model2;

import javax.persistence.*;


@Entity
@Table(name = "d_table1")
public class DefinedModel1 {

    private static final long serialVersionUID = -6675929159347159519L;
    @Id
    @GeneratedValue()
    @Column(name = "id")
    private Long id;
    @Column(name = "rcode")
    private String rcode;
    @Column(name = "rname")
    private String rname;
    @Column(name = "app")
    private Integer app;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRcode() {
        return rcode;
    }

    public void setRcode(String rcode) {
        this.rcode = rcode;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public Integer getApp() {
        return app;
    }

    public void setApp(Integer app) {
        this.app = app;
    }
}
