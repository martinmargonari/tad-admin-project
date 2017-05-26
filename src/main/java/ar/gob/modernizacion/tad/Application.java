package ar.gob.modernizacion.tad;

import ar.gob.modernizacion.tad.managers.ConnectionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;

/**
 * Created by martinm on 19/05/17.
 */

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        ConnectionManager.USER = "mmargonari";
        ConnectionManager.PASSWORD = "orl174A";

        SpringApplication.run(Application.class, args);
    }
}
