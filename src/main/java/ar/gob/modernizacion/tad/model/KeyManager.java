package ar.gob.modernizacion.tad.model;

import java.util.HashMap;

/**
 * Created by MMargonari on 13/07/2017.
 */
public class KeyManager {

    private static final HashMap<String, String[]> userKeys = new HashMap();

    public static String getSalt(User user) {
        return userKeys.get(user.getUsername())[0];
    }

    public static String getIv(User user) {
        return userKeys.get(user.getUsername())[1];
    }

    public static void putKeys(User user) {
        String keys[] = new String[2];
        keys[0] = user.getSalt();
        keys[1] = user.getIv();

        userKeys.put(user.getUsername(), keys);
    }
}
