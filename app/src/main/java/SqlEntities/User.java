package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que describe un usuario
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class User {
    private int userId;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String photoUrl;
    private boolean isLogged;
    private List<Competition> competitions;

    /**
     * Devuelve el identificador del usuario dentro de la apliacion
     * @return el identificador del usuario dentro de la apliacion
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Asigna un identificador para el usuario dentro de la apliacion
     * @param userId el identificador del usuario dentro de la apliacion
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Devuelve la contraseña del usuario
     * @return la contraseña del usuario
     */
    public String getPassword() {
        return password;
    }

    /**
     * Asigna la contraseña para el usuario
     * @param password la contraseña para el usuario
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Devuelve el Username
     * @return el Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Asigna el Username
     * @param username el Username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Devuelve el nombre de usuario
     * @return el nombre de usuario
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Asigna el nombre del usuario
     * @param firstName el nombre del usuario
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Devuelve el apellido del usuario
     * @return el apellido del usuario
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Asigna el apellido del usuario
     * @param lastName el apellido del usuario
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Devuelve el email del usuario
     * @return el email del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * Asigna el email del usuario
     * @param email el email del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve el URL donde esta almacenada la imagen de usuario
     * @return el URL donde esta almacenada la imagen de usuario
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Asigna el URL donde esta almacenada la imagen de usuario
     * @param photoUrl el URL donde esta almacenada la imagen de usuario
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Devuelve una variable para saber si el usuario se encuentra activo en el dispositivo
     * @return variable para saber si el usuario se encuentra activo en el dispositivo
     */
    public boolean isLogged() {
        return isLogged;
    }

    /**
     * Asina valor a la variable para saber si el usuario se encuentra activo en el dispositivo
     * @param logged valor de la variable para saber si el usuario se encuentra activo en el dispositivo
     */
    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    /**
     * Devuelve la lista de competencias en las que participa el usuario
     * @return lista de competencias en las que participa el usuario
     */
    public List<Competition> getCompetitions() {
        return competitions;
    }

    /**
     * Asigna una lista de competencias en las que participa el usuario
     * @param competitions lista de competencias en las que participa el usuario
     */
    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }

    /**
     * Agrega una nueva competencia a la lista del usuario
     * @param competition competencia que desea agregar a la lista del usuario
     */
    public void addCompetition(Competition competition){
        this.competitions.add(competition);
    }

    /**
     * Constructor de la clase con parametros
     * @param userId Identificador del usuario dentro de la apliacion
     * @param password contraseña del usuario
     * @param username Username
     * @param firstName nombre de usuario
     * @param lastName apellido del usuario
     * @param email email del usuario
     * @param photoUrl URL donde esta almacenada la imagen de usuario
     * @param isLogged variable para saber si el usuario se encuentra activo en el dispositivo
     */
    public User(int userId, String password,  String username, String firstName, String lastName, String email, String photoUrl, boolean isLogged) {
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.isLogged = isLogged;
        this.competitions = new ArrayList<Competition>();
    }

    /**
     * Constructor simple de la clase, crea un usuario vacio
     */
    public User() {
        this.competitions = new ArrayList<Competition>();
    }
}
