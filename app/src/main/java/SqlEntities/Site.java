package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que describe un Sitio
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Site {
    private int siteId;
    private String siteName;
    private String siteDescription;
    private String latitud;
    private String longitud;
    private int status;
    private int siteVisitedPoints;
    private int sitePointsAwarded;
    private List<Term> termList;
    private List<Activity> activityList;

    /**
     * Devuelve el identificador del sitio
     * @return el identificador del sitio
     */
    public int getSiteId() {
        return siteId;
    }

    /**
     * Asigna el identificador del sitio
     * @param siteId el identificador del sitio
     */
    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    /**
     * Devuelve el nombre del sitio
     * @return el nombre del sitio
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * Asigna el nombre del sitio
     * @param siteName el nombre del sitio
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * Devuelve la descripcion del sitio
     * @return la descripcion del sitio
     */
    public String getSiteDescription() {
        return siteDescription;
    }

    /**
     * Asigna la descripcion del sitio
     * @param siteDescription la descripcion del sitio
     */
    public void setSiteDescription(String siteDescription) {
        this.siteDescription = siteDescription;
    }

    /**
     * Devuelve la latitud del sitio
     * @return la latitud del sitio
     */
    public String getLatitud() {
        return latitud;
    }

    /**
     * Asigna la latitud del sitio
     * @param latitud del sitio
     */
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    /**
     * Devuelve la longitud del sitio
     * @return la longitud del sitio
     */
    public String getLongitud() {
        return longitud;
    }

    /**
     * Asigna la longitud del sitio
     * @param longitud del sitio
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    /**
     * Devuelve el status de un sitio
     * @return el status de un sitio
     */
    public int getStatus() {
        return status;
    }

    /**
     * Asigna el status de un sitio
     * @param status el status de un sitio
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Devuelve el total de puntos de un sitio
     * @return el total de puntos de un sitio
     */
    public int getSiteVisitedPoints() {
        return siteVisitedPoints;
    }

    /**
     * Asigna el total de puntos de un sitio
     * @param siteVisitedPoints el total de puntos de un sitio
     */
    public void setSiteVisitedPoints(int siteVisitedPoints) {
        this.siteVisitedPoints = siteVisitedPoints;
    }

    /**
     * Devuelve los puntos obtenidos en el sitio
     * @return los puntos obtenidos en el sitio
     */
    public int getSitePointsAwarded() {
        return sitePointsAwarded;
    }

    /**
     * Asigna los puntos obtenidos en el sitio
     * @param sitePointsAwarded los puntos obtenidos en el sitio
     */
    public void setSitePointsAwarded(int sitePointsAwarded) {
        this.sitePointsAwarded = sitePointsAwarded;
    }

    /**
     * Devuelve la lista de terminos asociados al sitio
     * @return la lista de terminos asociados al sitio
     */
    public List<Term> getTermList() {
        return termList;
    }

    /**
     * Asigna la lista de terminos asociados al sitio
     * @param termList lista de terminos asociados al sitio
     */
    public void setTermList(List<Term> termList) {
        this.termList = termList;
    }

    /**
     * Permite agregar un termino a la lista de terminos del sitio
     * @param termTemp
     */
    public void addTerm(Term termTemp) {
        this.termList.add(termTemp);
    }

    /**
     * Devuelve la lista de actividades asociadas al sitio
     * @return
     */
    public List<Activity> getActivityList() {
        return activityList;
    }

    /**
     * Asigna la lista de actividades asociadas al sitio
     * @param activityList
     */
    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    /**
     * Asigna una actividad a la lista de actividades asociadas al sitio
     * @param activityTemp
     */
    public void addActivity(Activity activityTemp) {
        this.activityList.add(activityTemp);
    }

    /**
     * Constructor de la clase con parametros
     * @param siteId identificador del sitio
     * @param siteName el nombre del sitio
     * @param siteDescription la descripcion del sitio
     * @param latitud del sitio
     * @param longitud del sitio
     * @param status status de un sitio
     * @param siteVisitedPoints total de puntos de un sitio
     * @param sitePointsAwarded puntos obtenidos en el sitio
     */
    public Site(int siteId, String siteName, String siteDescription, String latitud, String longitud, int status, int siteVisitedPoints, int sitePointsAwarded) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.siteDescription = siteDescription;
        this.latitud = latitud;
        this.longitud = longitud;
        this.status = status;
        this.siteVisitedPoints = siteVisitedPoints;
        this.sitePointsAwarded = sitePointsAwarded;
        this.termList = new ArrayList<Term>();
        this.activityList = new ArrayList<Activity>();
    }

    /**
     * Constructor simple de la clase, crea un sitio vacio
     */
    public Site() {
        this.termList = new ArrayList<Term>();
        this.activityList = new ArrayList<Activity>();
    }
}