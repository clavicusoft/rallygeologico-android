package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Site {
    private int siteId;
    private String siteName;
    private String siteDescription;
    private String latitud;
    private String longitud;
    private int status;
    private int siteTotalPoints;
    private int sitePointsAwarded;
    private List<Term> termList;
    private List<Activity> activityList;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteDescription() {
        return siteDescription;
    }

    public void setSiteDescription(String siteDescription) {
        this.siteDescription = siteDescription;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSiteTotalPoints() {
        return siteTotalPoints;
    }

    public void setSiteTotalPoints(int siteTotalPoints) {
        this.siteTotalPoints = siteTotalPoints;
    }

    public int getSitePointsAwarded() {
        return sitePointsAwarded;
    }

    public void setSitePointsAwarded(int sitePointsAwarded) {
        this.sitePointsAwarded = sitePointsAwarded;
    }

    public List<Term> getTermList() {
        return termList;
    }

    public void setTermList(List<Term> termList) {
        this.termList = termList;
    }

    public void addTerm(Term termTemp) {
        this.termList.add(termTemp);
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    public void addActivity(Activity activityTemp) {
        this.activityList.add(activityTemp);
    }

    public Site(int siteId, String siteName, String siteDescription, String latitud, String longitud, int status, int siteTotalPoints, int sitePointsAwarded) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.siteDescription = siteDescription;
        this.latitud = latitud;
        this.longitud = longitud;
        this.status = status;
        this.siteTotalPoints = siteTotalPoints;
        this.sitePointsAwarded = sitePointsAwarded;
        this.termList = new ArrayList<Term>();
        this.activityList = new ArrayList<Activity>();
    }

    public Site() {
        this.termList = new ArrayList<Term>();
        this.activityList = new ArrayList<Activity>();
    }
}