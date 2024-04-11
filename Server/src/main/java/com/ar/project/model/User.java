package com.ar.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username", nullable = false, unique = true)
    //@Size(min = 2, message = "user name should have at least 2 characters")
    private String name;

    @Column(name = "password", nullable = false)
    //@Size(min = 8, message = "password should have at least 8 characters")
    private String password;

    @Column(name = "profile")
    private String profile;

    @OneToMany(mappedBy = "user")
    private List<Image> images;

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    private List<Chat> sentChats;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver")
    private List<Chat> receivedChats;

    public User() {
        this.profile = "default.jpg";
    }

    public User(String name, String password, String profile) {

        System.out.println(name);
        System.out.println(password);
        System.out.println(profile);

        this.name = name;
        this.password = password;
        this.profile = (profile.equals("") ? "default.jpg" : profile);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
