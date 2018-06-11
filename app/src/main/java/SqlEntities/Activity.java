package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que describe una actividad
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Activity {
    private int activityId;
    private int activityPoints;
    private int getActivityType;
    private int activityStatus;
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

    /**
     * Retorna el status de la actividad
     * @return el status de la actividad
     */
    public int getActivityStatus() {
        return activityStatus;
    }

    /**
     *Asigna el status de la actividad
     * @param activityStatus el status de la actividad
     */
    public void setActivityStatus(int activityStatus) {
        this.activityStatus = activityStatus;
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
