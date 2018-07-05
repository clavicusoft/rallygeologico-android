package SqlEntities;

import java.util.Date;

/**
 * Clase que describe una competencia
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Competition {
    private int competitionId;
    private String name;
    private boolean is_active;
    private boolean is_public;
    private Date startingDate;
    private Date finishingDate;
    private int totalPoints;
    private String description;
    private Rally rally;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Devuelve el identificador de la competencia
     * @return el identificador de la competencia
     */
    public int getCompetitionId() {
        return competitionId;
    }

    /**
     *  Asigna el identificador de la competencia
     * @param competitionId el identificador de la competencia
     */
    public void setCompetitionId(int competitionId) {
        this.competitionId = competitionId;
    }

    /**
     * Devuelve el nombre de la competencia
     * @return el nombre de la competencia
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve el nombre de la competencia
     * @param name el nombre de la competencia
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Develve si la conmpetencia esta activa
     * @return si la conmpetencia esta activa
     */
    public boolean isActive() {
        return is_active;
    }

    /**
     * Permite asignar si la conmpetencia esta activa
     * @param active si la conmpetencia esta activa
     */
    public void setActive(boolean active) {
        this.is_active = active;
    }

    /**
     * Devuelve variable que marca si la competencia es publica
     * @return si la competencia es publica
     */
    public boolean isPublic() {
        return is_public;
    }

    /**
     *Permite asignar si la competencia es publica
     * @param aPublic valor de la variable que marca si la competencia es publica
     */
    public void setPublic(boolean aPublic) {
        is_public = aPublic;
    }

    /**
     * Devuelve la fecha de inicio de la competencia
     * @return la fecha de inicio de la competencia
     */
    public Date getStartingDate() {
        return startingDate;
    }

    /**
     * Permite asignar la fecha de inicio de la competencia
     * @param startingDate la fecha de inicio de la competencia
     */
    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    /**
     * Devuelve la fecha de final de la competencia
     * @return la fecha de final de la competencia
     */
    public Date getFinichingDate() {
        return finishingDate;
    }

    /**
     * Permite asignar la fecha de final de la competencia
     * @param finichingDate la fecha de final de la competencia
     */
    public void setFinichingDate(Date finichingDate) {
        this.finishingDate = finichingDate;
    }

    /**
     * Devuelve el total de puntos de la competencia
     * @return el total de puntos de la competencia
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * Permite asignar el total de puntos de la competencia
     * @param totalPoints el total de puntos de la competencia
     */
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * Devuelve el rally al que pertenece la competencia
     * @return el rally al que pertenece la competencia
     */
    public Rally getRally() {
        return rally;
    }

    /**
     * Permite asignar el rally al que pertenece la competencia
     * @param rally rally al que pertenece la competencia
     */
    public void setRally(Rally rally) {
        this.rally = rally;
    }

    /**
     * Constructor de la clase con parametros
     * @param competitionId  identificador de la competencia
     * @param name nombre de la competencia
     * @param active variable que marca si la competencia esta activa
     * @param isPublic variable que marca si la competencia es publica
     * @param startingDate la fecha de inicio de la competencia
     * @param finichingDate la fecha de final de la competencia
     * @param totalPoints el total de puntos de la competencia
     */
    public Competition(int competitionId, String name, boolean active, boolean isPublic, Date startingDate, Date finichingDate, int totalPoints) {
        this.competitionId = competitionId;
        this.name = name;
        this.is_active = active;
        this.is_public = isPublic;
        this.startingDate = startingDate;
        this.finishingDate = finichingDate;
        this.totalPoints = totalPoints;
    }

    /**
     * Constructor simple de la clase, crea una competencia vacio
     */
    public Competition() {
    }
}
