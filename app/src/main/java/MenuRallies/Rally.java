package MenuRallies;

/**
 * Clase para manejar los datos de un Rally
 * Created by Pablo Madrigal on 20/04/2018.
 */

public class Rally {
    private int rallyId;
    private String name;
    private String description;
    private int pointsAwarded;
    private String imageURL;
    private String memoryUsage;
    private boolean isDownloaded;

    /**
     * Constructor that passes in the sports data and the context
     * @param rallyId Codigo que identifica ese rally especifico
     * @param name Nombre que tiene el Rally
     * @param description Descripcion basica del rally y en que consiste
     * @param pointsAwarded Puntos que se pueden obtener con el rally
     * @param imageURL URL que direcciona a donde esta guardada la imagen de portada del rally
     * @param isDownloaded Nos dice si el rally se encuentra descargado en el dispositivo
     * @param memoryUsage Tamaño total que ocupa el rally descargado
     */
    public Rally(int rallyId, String name, String description, int pointsAwarded, String imageURL, String memoryUsage, boolean isDownloaded) {
        this.rallyId = rallyId;
        this.name = name;
        this.description = description;
        this.pointsAwarded = pointsAwarded;
        this.imageURL = imageURL;
        this.memoryUsage = memoryUsage;
        this.isDownloaded = isDownloaded;
    }

    /**
     *
     * @return Codigo que identifica ese rally especifico
     */
    public int getRallyId() {
        return rallyId;
    }

    /**
     *
     * @return Nombre que tiene el Rally
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return descripcion basica del rally y en que consiste
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return Puntos que se pueden obtener con el rally
     */
    public int getPointsAwarded() {
        return pointsAwarded;
    }

    /**
     *
     * @return URL que direcciona a donde esta guardada la imagen de portada del rally
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     *
     * @return Tamaño total que ocupa el rally descargado
     */
    public String getMemoryUsage() {
        return memoryUsage;
    }

    /**
     *
     * @return Nos dice si el rally se encuentra descargado en el dispositivo
     */
    public boolean isDownloaded() {
        return isDownloaded;
    }

    /**
     *
     * @param memoryUsage actualiza la memoria total que esta ocupando el rally
     */
    public void setMemoryUsage(String memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    /**
     *
     * @param downloaded Actualiza si el rally esta descargado o no
     */
    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }
}
