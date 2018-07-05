package SqlEntities;

/**
 * Created by pjmq2 on 04/07/2018.
 */

public class OpcionesDB {
    private int optionsId;
    private String optionsText;
    private boolean is_correct;
    private int ActivitiId;

    public int getOptionsId() {
        return optionsId;
    }

    public void setOptionsId(int optionsId) {
        this.optionsId = optionsId;
    }

    public String getOptionsText() {
        return optionsText;
    }

    public void setOptionsText(String optionsText) {
        this.optionsText = optionsText;
    }

    public boolean is_correct() {
        return is_correct;
    }

    public void setIs_correct(boolean is_correct) {
        this.is_correct = is_correct;
    }

    public int getActivitiId() {
        return ActivitiId;
    }

    public void setActivitiId(int activitiId) {
        ActivitiId = activitiId;
    }

    public OpcionesDB(int optionsId, String optionsText, boolean is_correct, int activitiId) {
        this.optionsId = optionsId;
        this.optionsText = optionsText;
        this.is_correct = is_correct;
        ActivitiId = activitiId;
    }

    public OpcionesDB() {
    }
}
