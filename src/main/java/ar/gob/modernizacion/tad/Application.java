package ar.gob.modernizacion.tad;

import ar.gob.modernizacion.tad.managers.ConnectionManager;
import ar.gob.modernizacion.tad.managers.TramiteManager;
import ar.gob.modernizacion.tad.model.Tramite;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by martinm on 19/05/17.
 */

@SpringBootApplication
public class Application {

    public static ArrayList<Tramite> tramites;

    public static void main(String[] args) {

        ConnectionManager.USER = "mmargonari";
        ConnectionManager.PASSWORD = "orl174A";

        tramites = new ArrayList<>();
        try {
            TramiteManager.loadTramites();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SpringApplication.run(Application.class, args);
    }
}