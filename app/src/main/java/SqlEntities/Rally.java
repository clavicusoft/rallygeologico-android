package SqlEntities;

import java.util.ArrayList;
import java.util.List;

import SqlEntities.Site;

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
    private List<Site> sites;


    /**
     * Devuelve el identificador de este rally especifico
     * @return Codigo que identifica ese rally especifico
     */
    public int getRallyId() {
        return rallyId;
    }

    /**
     * Clase que describe un rally
     * @param id del rally
     */
    public void setRallyId(int id){
        this.rallyId = id;
    }

    /**
     * Devuelve el nombre que tiene el Rally
     * @return Nombre que tiene el Rally
     */
    public String getName() {
        return name;
    }

    /**
     * Asigna el nombre que tiene el Rally
     * @param name Nombre del rally
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * devuelve una  descripcion basica del rally y en que consiste
     * @return descripcion basica del rally y en que consiste
     */
    public String getDescription() {
        return description;
    }

    /**
     * Asigna una descripcion basica del rally y en que consiste
     * @param description basica del rally y en que consiste
     */
    public void setDescription(String description){
        this.description = description;
    }

    /**
     * Devuelve los puntos que se pueden obtener con el rally
     * @return Puntos que se pueden obtener con el rally
     */
    public int getPointsAwarded() {
        return pointsAwarded;
    }

    /**
     * Asigna los puntos que se pueden obtener con el rally
     * @param pointsAwarded Puntos que se pueden obtener con el rally
     */
    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    /**
     * Devuelve el URL que direcciona a donde esta guardada la imagen de portada del rally
     * @return URL que direcciona a donde esta guardada la imagen de portada del rally
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Asigna la URL que direcciona a donde esta guardada la imagen de portada del rally
     * @param imageURL URL que direcciona a donde esta guardada la imagen de portada del rally
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * Devuelve el tamano total que ocupa el rally descargado
                     * @return Tamano total que ocupa el rally descargado
     */
    public String getMemoryUsage() {
        return memoryUsage;
    }

    /**
     * permite asignar la memoria total que esta ocupando el rally
     * @param memoryUsage actualiza la memoria total que esta ocupando el rally
     */
    public void setMemoryUsage(String memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    /**
     * Devuelve un variable que nos dice si el rally se encuentra descargado en el dispositivo
     * @return Nos dice si el rally se encuentra descargado en el dispositivo
     */
    public boolean getIsDownloaded() {
        return isDownloaded;
    }

    /**
     * Permite asignar un valor a la variable que nos dice si el rally se encuentra descargado en el dispositivo
     * @param downloaded Nos dice si el rally se encuentra descargado en el dispositivo
     */
    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    /**
     * Devuelve la lista de sitios que pertenecen al Rally
     * @return la lista de sitios que pertenecen al Rally
     */
    public List<Site> getSites() {
        return sites;
    }

    /**
     * Permite asignar una lista de sitios que pertenecen al Rally
     * @param sites Lista de sitios que pertenecen a dicho rally
     */
    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    /**
     * Permite agregar un sitio a la lista interna de sitios
     * @param siteTemp sitio para agregar a la lista
     */
    public void addSite(Site siteTemp){
        this.sites.add(siteTemp);
    }

    /**
     * Constructor de la clase con parametros
     * @param rallyId Codigo que identifica ese rally especifico
     * @param name Nombre que tiene el Rally
     * @param description Descripcion basica del rally y en que consiste
     * @param pointsAwarded Puntos que se pueden obtener con el rally
     * @param imageURL URL que direcciona a donde esta guardada la imagen de portada del rally
     * @param isDownloaded Nos dice si el rally se encuentra descargado en el dispositivo
     * @param memoryUsage Tamano total que ocupa el rally descargado
     */
    public Rally(int rallyId, String name, String description, int pointsAwarded, String imageURL, String memoryUsage, boolean isDownloaded) {
        this.rallyId = rallyId;
        this.name = name;
        this.description = description;
        this.pointsAwarded = pointsAwarded;
        this.imageURL = imageURL;
        this.memoryUsage = memoryUsage;
        this.isDownloaded = isDownloaded;
        this.sites = new ArrayList<Site>();
    }

    /**
     * Constructor simple de la clase, crea un rally vacio
     */
    public Rally() {
        this.sites = new ArrayList<Site>();
    }
}
