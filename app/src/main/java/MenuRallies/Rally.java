package MenuRallies;

/**
 * Created by pjmq2 on 25/04/2018.
 */

public class Rally {
    private int rallyId;
    private String name;
    private String description;
    private int pointsAwarded;
    private String imageURL;
    private String memoryUsage;
    private boolean isDownloaded;

    public Rally(int rallyId, String name, String description, int pointsAwarded, String imageURL, String memoryUsage, boolean isDownloaded) {
        this.rallyId = rallyId;
        this.name = name;
        this.description = description;
        this.pointsAwarded = pointsAwarded;
        this.imageURL = imageURL;
        this.memoryUsage = memoryUsage;
        this.isDownloaded = isDownloaded;
    }

    public int getRallyId() {
        return rallyId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getMemoryUsage() {
        return memoryUsage;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setMemoryUsage(String memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }
}
