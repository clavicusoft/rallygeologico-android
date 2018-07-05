package com.rallygeologico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import SqlEntities.Competition;
import SqlEntities.Rally;
import SqlEntities.Site;
import SqlEntities.User;

/**
 * Clase para parsear los datos obtenidos del grafo de Facebook
 */
public class JSONParser {

    /**
     *  Parsea el nombre del usuario de Facebook
     * @param obj Objeto en formato JSON
     * @return String con el nombre de usuario
     */
    public static String getName(JSONObject obj) {
        String s1 = "";
        try {
            s1 = obj.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s1;
    }

    /**
     * Recibe un json y devuelve un usuario con su informacion y para agregarlo a la base
     * @param obj json con la info del usuario
     * @return user con su informacion asignada
     */
    public static User getUser(JSONObject obj) {
        User user = new User();
        try {
            user.setLogged(true);
            user.setUserId(obj.getString("id"));
            user.setUsername(obj.getString("username"));
            user.setFirstName(obj.getString("first_name"));
            user.setLastName(obj.getString("last_name"));
            user.setEmail(obj.getString("email"));
            user.setPhotoUrl(obj.getString("photo_url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Recibe un json y devuelve un rally con su informacion y para agregarlo a la base
     * @param obj json con la info del rally
     * @return rally con su informacion asignada
     */
    public static Rally getRally(JSONObject obj) {
        Rally rally = new Rally();
        int valor;
        try {
            JSONObject rallyJson = (JSONObject) obj.get("rally");
            valor = Integer.parseInt(rallyJson.getString("id"));
            rally.setRallyId(valor);
            rally.setName(rallyJson.getString("name"));
            rally.setDescription(rallyJson.getString("description"));
            rally.setDownloaded(false);
            rally.setImageURL(rallyJson.getString("image_url"));
            valor = Integer.parseInt(rallyJson.getString("points_awarded"));
            rally.setPointsAwarded(valor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rally;
    }

    public static Competition getCompetition(JSONObject obj) {
        Competition competition = new Competition();
        Rally rally = new Rally();
        int valor;
        try {
            // Obtener la informacion de la competencia del usuario
            competition.setCompetitionId(obj.getString("id"));
            String active = obj.getString("is_active");
            if (active.equalsIgnoreCase("1")) {
                competition.setActive(true);
            } else {
                competition.setActive(false);
            }
            competition.setName(obj.getString("name"));
            String dateStr = obj.getString("starting_date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            competition.setStartingDate(date);
            dateStr = obj.getString("finishing_date");
            date = null;
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            competition.setFinichingDate(date);

            // Obtener la informacion del rally de la competencia
            JSONObject rallyJson = (JSONObject) obj.get("rally");
            valor = Integer.parseInt(rallyJson.getString("id"));
            rally.setRallyId(valor);
            rally.setName(rallyJson.getString("name"));
            rally.setDescription(rallyJson.getString("description"));
            rally.setDownloaded(false);
            rally.setImageURL(rallyJson.getString("image_url"));
            valor = Integer.parseInt(rallyJson.getString("points_awarded"));
            rally.setPointsAwarded(valor);
            competition.setRally(rally);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return competition;
    }

    /**
     * Recibe un json de un rally con todos sus sitios y devuelve una lista para agregarlos a la bd local
     * @param obj Objeto json a parsear
     * @return la lista de los sitios asociados a un rally
     */
    public static LinkedList<Site> getSitesFromRally(JSONObject obj) {
        LinkedList<Site> listaSitios = new LinkedList<Site>();
        Site sitio;
        int valor;
        int cantidad = 0;
        String pos = "" + cantidad;
        try {
            JSONArray sitesJson = (JSONArray) obj.getJSONArray("site");
            JSONObject specificSiteJson = (JSONObject) sitesJson.get(cantidad);
            String especial;
            while(specificSiteJson != null){
                sitio = new Site();
                valor = Integer.parseInt(specificSiteJson.getString("id"));
                sitio.setSiteId(valor);
                sitio.setSiteName(specificSiteJson.getString("name"));
                sitio.setSiteDescription(specificSiteJson.getString("description"));
                sitio.setLatitud(specificSiteJson.getString("latitude"));
                sitio.setLongitud(specificSiteJson.getString("longitude"));
                valor = Integer.parseInt(specificSiteJson.getString("points"));
                sitio.setSiteVisitedPoints(valor);
                sitio.setSitePointsAwarded(0);

                especial = specificSiteJson.getString("is_easter_egg");
                if (especial.equalsIgnoreCase("0")) {
                    sitio.setStatus(1);
                } else {
                    sitio.setStatus(4);
                }
                sitio.setStatus(1);
                listaSitios.add(sitio);

                cantidad++;
                pos = "" + cantidad;
                specificSiteJson = (JSONObject) sitesJson.get(cantidad);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaSitios;
    }

    /**
     * Obtiene el email del login de facebook
     * @param obj objeto json devuelto por el grafo de faceboook
     * @return string con el email
     */
    public static String getEmail(JSONObject obj) {
        String s1 = "";
        try {
            s1 = obj.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s1;
    }

    /**
     * Parsea el id de Facebook del usuario
     * @param obj Objeto en formato JSON
     * @return String con el id
     */
    public static String getId(JSONObject obj) {
        String s1 = "";
        try {
            s1 = obj.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s1;
    }

    /**
     * Parsea la ciudad de origen de Facebook del usuario
     * @param obj Objeto en formato JSON
     * @return String con la ciudad de origen
     */
    public static String getHometown(JSONObject obj) {
        String s1 = "";
        try {
            JSONObject ubicacion = (JSONObject) obj.get("hometown");
            s1 = ubicacion.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s1;
    }

}