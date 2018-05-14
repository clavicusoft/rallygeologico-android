package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Term {
    private int termId;
    private String termName;
    private String termDescription;
    private List<Multimedia> termMultimediaList;

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermDescription() {
        return termDescription;
    }

    public void setTermDescription(String termDescription) {
        this.termDescription = termDescription;
    }

    public List<Multimedia> getTermMultimediaList() {
        return termMultimediaList;
    }

    public void setTermMultimediaList(List<Multimedia> termMultimediaList) {
        this.termMultimediaList = termMultimediaList;
    }

    public void addMultimedia(Multimedia multimediaTemp){
        this.termMultimediaList.add(multimediaTemp);
    }

    public Term(int termId, String termName, String termDescription) {
        this.termId = termId;
        this.termName = termName;
        this.termDescription = termDescription;
        this.termMultimediaList = new ArrayList<Multimedia>();
    }

    public Term() {
        this.termMultimediaList = new ArrayList<Multimedia>();
    }
}
