package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que describe una actividad
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Activity {
    private int activityId;
    private String activityName;
    private String activityDescription;
    private int activityPoints;
    private int getActivityType;
    private boolean is_visited;
    private List<Multimedia> activityMultimediaList;

    /**
     * Retorna el Id de la actividad
     * @return el Id de la actividad
     */
    public int getActivityId() {
        return activityId;
    }

    /**
     * Se le asigan un Id a la actividad
     * @param activityId Id de la actividad
     */
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    /**
     * Retorna el nombre de la actividad
     * @return el nombre de la actividad
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * Se le asigan un nombre a la actividad
     * @param activityName nombre de la actividad
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     *
     * @return
     */
    public String getActivityDescription() {
        return activityDescription;
    }

    /**
     *
     * @param activityDescription
     */
    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    /**
     * Devuelve los puntos que vale la actividad
     * @return los puntos que vale la actividad
     */
    public int getActivityPoints() {
        return activityPoints;
    }

    /**
     * Se asignan los puntos que vale la actividad
     * @param activityPoints puntos que vale la actividad
     */
    public void setActivityPoints(int activityPoints) {
        this.activityPoints = activityPoints;
    }

    /**
     * retorna el tipo de actividad
     * @return  el tipo de actividad
     */
    public int getGetActivityType() {
        return getActivityType;
    }

    /**
     * Se asigna el tipo de actividad
     * @param getActivityType el tipo de actividad
     */
    public void setGetActivityType(int getActivityType) {
        this.getActivityType = getActivityType;
    }

    public boolean is_visited() {
        return is_visited;
    }

    public void setIs_visited(boolean is_visited) {
        this.is_visited = is_visited;
    }

    /**
     * Retorna una lista con los multimedia asociados a la actividad
     * @return lista con los multimedia asociados a la actividad
     */
    public List<Multimedia> getActivityMultimediaList() {
        return activityMultimediaList;
    }

    /**
     * Asigna la lista de multimedia asociados a la actividad
     * @param activityMultimediaList lista de multimedia asociados a la actividad
     */
    public void setActivityMultimediaList(List<Multimedia> activityMultimediaList) {
        this.activityMultimediaList = activityMultimediaList;
    }

    /**
     * Permite agregar un multimedia a la lista asociada a la actividad
     * @param multimediaTemp multimedia que se desea agregar a la lista asociada a la actividad
     */
    public void addMultimedia(Multimedia multimediaTemp){
        this.activityMultimediaList.add(multimediaTemp);
    }

    public boolean isIs_visited() {
        return is_visited;
    }

    /**
     * Constructor de la clase con parametros
     * @param activityId Identificador de la actividad
     * @param activityPoints Cantidad de puntos de la actividad
     * @param getActivityType Tipo de actividad
     */
    public Activity(int activityId, int activityPoints, int getActivityType) {
        this.activityId = activityId;
        this.activityPoints = activityPoints;
        this.getActivityType = getActivityType;
        this.activityMultimediaList = new ArrayList<Multimedia>();
    }

    /**
     * Constructor simple de la clase, crea una activity vacio
     */
    public Activity() {
        this.activityMultimediaList = new ArrayList<Multimedia>();
    }
}
