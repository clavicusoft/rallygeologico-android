package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class User {
    private String userId;
    private String facebookId;
    private String googleId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String photoUrl;
    private boolean isLogged;
    private List<Competition> competitions;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }

    public void addCompetition(Competition competition){
        this.competitions.add(competition);
    }

    public User(String userId, String facebookId, String googleId, String username, String firstName, String lastName, String email, String photoUrl, boolean isLogged) {
        this.userId = userId;
        this.facebookId = facebookId;
        this.googleId = googleId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.isLogged = isLogged;
        this.competitions = new ArrayList<Competition>();
    }

    public User() {
        this.competitions = new ArrayList<Competition>();
    }
}
