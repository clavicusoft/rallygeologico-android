package SqlEntities;

import java.util.ArrayList;
import java.util.List;

import SqlDatabase.LocalDB;

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
    private int pointsForVisit;
    private boolean is_visited;
    private boolean is_easter_egg;
    private List<Term> termList;
    private List<Activity> activityList;
    private int status;

    public int getStatus() {
        if(!is_visited && !is_easter_egg)
            status = 1;
        else if(is_visited && !is_easter_egg)
            status = 2;
        else if(is_visited && is_easter_egg)
            status = 3;
        else if(!is_visited && is_easter_egg)
            status = 4;

        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        switch (status){
            case 1:
                is_visited = false;
                is_easter_egg = false;
                break;
            case 2:
                is_visited = true;
                is_easter_egg = false;
                break;
            case 3:
                is_visited = true;
                is_easter_egg = true;
                break;
            case 4:
                is_visited = false;
                is_easter_egg = true;
                break;
        }
    }

    public int getPointsForVisit() {
        return pointsForVisit;
    }

    public void setPointsForVisit(int pointsForVisit) {
        this.pointsForVisit = pointsForVisit;
    }

    public boolean is_visited() {
        return is_visited;
    }

    public void set_visited(boolean is_visited) {
        this.is_visited = is_visited;
    }

    public boolean is_easter_egg() {
        return is_easter_egg;
    }

    public void setIs_easter_egg(boolean is_easter_egg) {
        this.is_easter_egg = is_easter_egg;
    }

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
     * @param is_visited status de un sitio
     * @param is_easter_egg verifica si es un sitio especial
     * @param pointsForVisit puntos obtenidos en el sitio
     */
    public Site(int siteId, String siteName, String siteDescription, String latitud, String longitud, int pointsForVisit, boolean is_visited, boolean is_easter_egg, List<Term> termList, List<Activity> activityList) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.siteDescription = siteDescription;
        this.latitud = latitud;
        this.longitud = longitud;
        this.pointsForVisit = pointsForVisit;
        this.is_visited = is_visited;
        this.is_easter_egg = is_easter_egg;
        this.termList = termList;
        this.activityList = activityList;
    }

    /**
     * Constructor simple de la clase, crea un sitio vacio
     */
    public Site() {
        this.termList = new ArrayList<Term>();
        this.activityList = new ArrayList<Activity>();
    }
}