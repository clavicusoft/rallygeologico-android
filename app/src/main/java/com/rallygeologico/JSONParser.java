package com.rallygeologico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

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

    public static LinkedList<Site> getSitesFromRally(JSONObject obj) {
        LinkedList<Site> listaSitios = new LinkedList<Site>();
        Site sitio;
        int valor;
        int cantidad = 0;
        String pos = "" + cantidad;
        try {
            JSONArray sitesJson = (JSONArray) obj.getJSONArray("site");
            JSONObject specificSiteJson = (JSONObject) sitesJson.get(cantidad);
            while(specificSiteJson != null){
                sitio = new Site();
                valor = Integer.parseInt(specificSiteJson.getString("id"));
                sitio.setSiteId(valor);
                sitio.setSiteName(specificSiteJson.getString("name"));
                sitio.setSiteDescription(specificSiteJson.getString("description"));
                sitio.setLatitud(specificSiteJson.getString("latitude"));
                sitio.setLongitud(specificSiteJson.getString("longitude"));
                valor = Integer.parseInt(specificSiteJson.getString("points"));
                sitio.setSiteTotalPoints(valor);
                sitio.setSitePointsAwarded(0);
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

   /* public static ArrayList<String> getFavAthletes(JSONObject obj) {
        favAthletes.clear();
        try {
            JSONArray arr = obj.getJSONArray("favorite_athletes");
            String s;
            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);
                s = obj.getString("name");
                favAthletes.add(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return favAthletes;
    }

    public static ArrayList<String> getFavTeams(JSONObject obj) {
        favTeams.clear();
        try {
            JSONArray arr = obj.getJSONArray("favorite_teams");
            String s;
            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);
                s = obj.getString("name");
                favTeams.add(s);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return favTeams;
    }*/
}