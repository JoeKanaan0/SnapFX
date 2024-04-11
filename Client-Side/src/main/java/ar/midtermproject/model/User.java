package ar.midtermproject.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class User {

    private long id;
    private String name;
    private String password;
    private String profile;

    public User() {
    }

    public User(String name, String password) {
        this(name,
             password,
                System.getProperty("user.dir") +
                        "\\src\\main\\resources\\ar\\midtermproject\\ResourceImages\\profile.jpg");
    }

    public User(String name, String password, String profile) {
        setName(name);
        setPassword(password);
        setProfile(profile);
    }

    public User(long id, String name, String password, String profile) {
        setId(id);
        setName(name);
        setPassword(password);
        setProfile(profile);
    }

    public static User FromJson(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        User serverUser = null;

        try {
            serverUser = objectMapper.readValue(jsonResponse, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverUser;
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
