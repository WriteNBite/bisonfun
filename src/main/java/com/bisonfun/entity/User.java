package com.bisonfun.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "text")
    private String aniToken;

    private Timestamp tokenExpires;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAnime> anime = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserMovie> movies = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTv> userTvs = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<UserAnime> getAnime() {
        return anime;
    }

    public void setAnime(Set<UserAnime> anime) {
        this.anime = anime;
    }

    public Set<UserMovie> getMovies() {
        return movies;
    }

    public void setMovies(Set<UserMovie> movies) {
        this.movies = movies;
    }

    public Set<UserTv> getUserTvs() {
        return userTvs;
    }

    public void setUserTvs(Set<UserTv> userTvs) {
        this.userTvs = userTvs;
    }

    public String getAniToken() {
        return aniToken;
    }

    public void setAniToken(String aniToken) {
        this.aniToken = aniToken;
    }

    public Timestamp getTokenExpires() {
        return tokenExpires;
    }

    public void setTokenExpires(Timestamp tokenExpires) {
        this.tokenExpires = tokenExpires;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
