package com.sun.spittr.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import static com.sun.spittr.util.Md5Util.md5;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Spitter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String passwordHash;

    @Column(nullable = false)
    String avatarUrl;

    @Column
    String aboutMe;

    @OneToMany(mappedBy = "spitter")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<Spittle> spittles;

    @ManyToMany
    @JoinTable(
            name = "follow",
            joinColumns = @JoinColumn(name = "user1_id"),
            inverseJoinColumns = @JoinColumn(name = "user2_id")
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<Spitter> follows;

    @ManyToMany(mappedBy = "follows")
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<Spitter> followers;

    public Spitter(String username, String password, String aboutMe) {
        this(username, password, username, aboutMe);
    }

    public Set<Spitter> getFollows() {
        return follows;
    }

    public void setFollows(Set<Spitter> follows) {
        this.follows = follows;
    }

    public Set<Spitter> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<Spitter> followers) {
        this.followers = followers;
    }

    public List<Spittle> getSpittles() {
        return spittles;
    }

    public void setSpittles(List<Spittle> spittles) {
        this.spittles = spittles;
    }

    public Spitter() {
    }

    public Spitter(String username, String password) {
        this(username, password, null, null);
        this.avatarUrl = autoGenerateAvatarUrl(username);
    }

    public void follow(Spitter spitter) {
        follows.add(spitter);
    }
    public void unfollow(Spitter spitter) {
        follows.remove(spitter);
    }
    public boolean isFollowing(Spitter spitter) {
        return follows.contains(spitter);
    }
    private String autoGenerateAvatarUrl(String username) {
        return username;
    }

    public Spitter(String username, String password, String avatarUrl, String aboutMe) {
        this.username = username;
        this.passwordHash = new BCryptPasswordEncoder().encode(password);
        this.avatarUrl = avatarUrl;
        this.aboutMe = aboutMe;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAvatarUrl() {

        /*
        * todo
        * if data base avatar url is empty, then return the gravatar url img
        * else return the database avatar url
         */

        String digest = md5(getUsername()); //"5EB63BBBE01EEED093CB22BB8F5ACDC3";
        int size = 128;
        return String.format("https://www.gravatar.com/avatar/%s?d=identicon&s=%d", digest, size);
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    @Override
    public String toString() {
        return "Spitter{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", spittles=" + spittles +
                ", follows count=" + follows.size() +
                ", followers count=" + followers.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spitter spitter = (Spitter) o;
        return id == spitter.id &&
                Objects.equals(username, spitter.username) &&
                Objects.equals(passwordHash, spitter.passwordHash) &&
                Objects.equals(avatarUrl, spitter.avatarUrl) &&
                Objects.equals(aboutMe, spitter.aboutMe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, passwordHash, avatarUrl, aboutMe);
    }
}
