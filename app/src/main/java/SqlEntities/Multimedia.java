package SqlEntities;

/**
 * Clase que describe un multimedia
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class Multimedia {
    private int multimediaType;
    private int multimediaId;
    private String multimediaURL;

    /**
     * Devuelve el tipo de multimedia
     * @return el tipo de multimedia
     */
    public int getMultimediaType() {
        return multimediaType;
    }

    /**
     * Asigna el tipo de multimedia
     * @param multimediaType el tipo de multimedia
     */
    public void setMultimediaType(int multimediaType) {
        this.multimediaType = multimediaType;
    }

    /**
     * Devuelve el identificador del multimedia
     * @return el identificador del multimedia
     */
    public int getMultimediaId() {
        return multimediaId;
    }

    /**
     * Asignar el identificador del multimedia
     * @param multimediaId el identificador del multimedia
     */
    public void setMultimediaId(int multimediaId) {
        this.multimediaId = multimediaId;
    }

    /**
     * Devuelve el URL con el archivo multimedia
     * @return el URL con el archivo multimedia
     */
    public String getMultimediaURL() {
        return multimediaURL;
    }

    /**
     * Asigna el URL con el archivo multimedia
     * @param multimediaURL el URL con el archivo multimedia
     */
    public void setMultimediaURL(String multimediaURL) {
        this.multimediaURL = multimediaURL;
    }

    /**
     * Constructor de la clase con parametros
     * @param multimediaType tipo de multimedia
     * @param multimediaId identificador del multimedia
     * @param multimediaURL URL con el archivo multimedia
     */
    public Multimedia(int multimediaType, int multimediaId, String multimediaURL) {
        this.multimediaType = multimediaType;
        this.multimediaId = multimediaId;
        this.multimediaURL = multimediaURL;
    }

    /**
     * Constructor simple de la clase, crea un multimedia vacio
     */
    public Multimedia() {
    }
}
