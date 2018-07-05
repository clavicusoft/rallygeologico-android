package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pjmq2 on 28/06/2018.
 */

public class CompetitionStatistics {
    private int competitionStatisticsId ;
    private int points;
    private List<Site> siteList;
    private List<Activity> activityList;

    public int getCompetitionStatisticsId() {
        return competitionStatisticsId;
    }

    public void setCompetitionStatisticsId(int competitionStatisticsId) {
        this.competitionStatisticsId = competitionStatisticsId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Site> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<Site> siteList) {
        this.siteList = siteList;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    public CompetitionStatistics(int competitionStatisticsId, int points, List<Site> siteList, List<Activity> activityList) {
        this.competitionStatisticsId = competitionStatisticsId;
        this.points = points;
        this.siteList = siteList;
        this.activityList = activityList;
    }

    public CompetitionStatistics() {
        this.activityList = new ArrayList<Activity>();
        this.siteList = new ArrayList<Site>();
    }
}
