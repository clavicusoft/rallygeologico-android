package SqlEntities;

/**
 * Created by pjmq2 on 28/06/2018.
 */

public class CompetitionStatistics {
    private int competitionStatisticsId ;
    private int userId;
    private int competitionId;
    private int points;

    public int getCompetitionStatisticsId() {
        return competitionStatisticsId;
    }

    public void setCompetitionStatisticsId(int competitionStatisticsId) {
        this.competitionStatisticsId = competitionStatisticsId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(int competitionId) {
        this.competitionId = competitionId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public CompetitionStatistics(int competitionStatisticsId, int userId, int competitionId, int points) {
        this.competitionStatisticsId = competitionStatisticsId;
        this.userId = userId;
        this.competitionId = competitionId;
        this.points = points;
    }
}
