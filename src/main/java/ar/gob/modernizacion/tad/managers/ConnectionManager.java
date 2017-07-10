package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by martinm on 24/05/17.
 */
public class ConnectionManager {

    public static String USER = "mmargonari";
    public static String PASSWORD = "orl174A    ";

    /*
    public static Connection connect() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("JDBC driver faltante o no coincidente");
        }

        Connection connection = null;

        try {
            System.out.println("Trying to connect: " + USER);
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@exa01-scan3.gde.gob.ar:1521/gedBBDD1", USER,PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("No se pudo conectar con la base");
        }

        return connection;
    }
*/
    public static Connection connect(User user) throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("JDBC driver faltante o no coincidente");
        }

        Connection connection = null;

        try {
            System.out.println("Trying to connect: " + user.getUsername());
                connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@exa01-scan2.gde.gob.ar:1521/gedBBDD1", user.getUsername(), user.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("No se pudo conectar con la base");
        }

        return connection;
    }

    public static void disconnect(Connection connection) throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Hubo un error al intentar desconectar de la base");
        }
    }
}
