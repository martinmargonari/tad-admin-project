package ar.gob.modernizacion.tad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;

/**
 * Created by martinm on 19/05/17.
 */

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
/*
        System.out.println("-------- Oracle JDBC Connection Testing ------");

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("Oracle JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@exa01-scan3.gde.gob.ar:1521/gedBBDD1", "mmargonari", "orl174A");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }

        Statement stmt = null;
        String query = "select ID, DESCRIPCION from TAD2_GED.TAD_TIPO_TRAMITE";
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("ID: "+ rs.getString("ID") + " DESCRIPCION: " + rs.getString("DESCRIPCION"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


*/

        SpringApplication.run(Application.class, args);
    }
}
