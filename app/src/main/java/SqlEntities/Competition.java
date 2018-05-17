package SqlEntities;

import java.util.Date;

/**
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Competition {
    private String competitionId;
    private String name;
    private boolean active;
    private boolean isPublic;
    private Date startingDate;
    private Date finichingDate;
    private int totalPoints;
    private Rally rally;

    public String getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(String competitionId) {
        this.competitionId = competitionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getFinichingDate() {
        return finichingDate;
    }

    public void setFinichingDate(Date finichingDate) {
        this.finichingDate = finichingDate;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Rally getRally() {
        return rally;
    }

    public void setRally(Rally rally) {
        this.rally = rally;
    }

    public Competition(String competitionId, String name, boolean active, boolean isPublic, Date startingDate, Date finichingDate, int totalPoints) {
        this.competitionId = competitionId;
        this.name = name;
        this.active = active;
        this.isPublic = isPublic;
        this.startingDate = startingDate;
        this.finichingDate = finichingDate;
        this.totalPoints = totalPoints;
    }

    public Competition() {
    }
}
