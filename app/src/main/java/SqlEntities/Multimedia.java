package SqlEntities;

/**
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Multimedia {
    private int multimediaType;
    private int multimediaId;
    private String multimediaURL;

    public int getMultimediaType() {
        return multimediaType;
    }

    public void setMultimediaType(int multimediaType) {
        this.multimediaType = multimediaType;
    }

    public int getMultimediaId() {
        return multimediaId;
    }

    public void setMultimediaId(int multimediaId) {
        this.multimediaId = multimediaId;
    }

    public String getMultimediaURL() {
        return multimediaURL;
    }

    public void setMultimediaURL(String multimediaURL) {
        this.multimediaURL = multimediaURL;
    }

    public Multimedia(int multimediaType, int multimediaId, String multimediaURL) {
        this.multimediaType = multimediaType;
        this.multimediaId = multimediaId;
        this.multimediaURL = multimediaURL;
    }

    public Multimedia() {
    }
}
