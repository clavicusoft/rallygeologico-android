package SqlEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que describe un Termino
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Term {
    private int termId;
    private String termName;
    private String termDescription;
    private List<Multimedia> termMultimediaList;

    /**
     * Devuelve el identificador del termino
     * @return el identificador del termino
     */
    public int getTermId() {
        return termId;
    }

    /**
     * Asigna el identificador del termino
     * @param termId el identificador del termino
     */
    public void setTermId(int termId) {
        this.termId = termId;
    }

    /**
     * Devuelve el nombre del termino
     * @return el nombre del termino
     */
    public String getTermName() {
        return termName;
    }

    /**
     * Asigna el nombre del termino
     * @param termName el nombre del termino
     */
    public void setTermName(String termName) {
        this.termName = termName;
    }

    /**
     * Devuelve la descripcion del termino
     * @return la descripcion del termino
     */
    public String getTermDescription() {
        return termDescription;
    }

    /**
     * Asigna la descripcion del termino
     * @param termDescription la descripcion del termino
     */
    public void setTermDescription(String termDescription) {
        this.termDescription = termDescription;
    }

    /**
     * Devuelve la lista de multimedias asociadas al termino
     * @return
     */
    public List<Multimedia> getTermMultimediaList() {
        return termMultimediaList;
    }

    /**
     * Asignala la lista de multimedias asociadas al termino
     * @param termMultimediaList
     */
    public void setTermMultimediaList(List<Multimedia> termMultimediaList) {
        this.termMultimediaList = termMultimediaList;
    }

    /**
     * Agrega una multimedia a la lista de multimedias asociadas al termino
     * @param multimediaTemp
     */
    public void addMultimedia(Multimedia multimediaTemp){
        this.termMultimediaList.add(multimediaTemp);
    }

    /**
     * Constructor de la clase con parametros
     * @param termId
     * @param termName
     * @param termDescription
     */
    public Term(int termId, String termName, String termDescription) {
        this.termId = termId;
        this.termName = termName;
        this.termDescription = termDescription;
        this.termMultimediaList = new ArrayList<Multimedia>();
    }

    /**
     * Constructor simple de la clase, crea un termino vacio
     */
    public Term() {
        this.termMultimediaList = new ArrayList<Multimedia>();
    }
}
