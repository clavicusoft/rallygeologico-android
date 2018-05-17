package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Activity {
    private int activityId;
    private int activityPoints;
    private int getActivityType;
    private int activityStatus;
    private List<Multimedia> activityMultimediaList;

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getActivityPoints() {
        return activityPoints;
    }

    public void setActivityPoints(int activityPoints) {
        this.activityPoints = activityPoints;
    }

    public int getGetActivityType() {
        return getActivityType;
    }

    public void setGetActivityType(int getActivityType) {
        this.getActivityType = getActivityType;
    }

    public int getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(int activityStatus) {
        this.activityStatus = activityStatus;
    }

    public List<Multimedia> getActivityMultimediaList() {
        return activityMultimediaList;
    }

    public void setActivityMultimediaList(List<Multimedia> activityMultimediaList) {
        this.activityMultimediaList = activityMultimediaList;
    }

    public void addMultimedia(Multimedia multimediaTemp){
        this.activityMultimediaList.add(multimediaTemp);
    }

    public Activity(int activityId, int activityPoints, int getActivityType) {
        this.activityId = activityId;
        this.activityPoints = activityPoints;
        this.getActivityType = getActivityType;
        this.activityMultimediaList = new ArrayList<Multimedia>();
    }

    public Activity() {
        this.activityMultimediaList = new ArrayList<Multimedia>();
    }
}
