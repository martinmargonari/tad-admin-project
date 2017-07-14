package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Created by MMargonari on 06/07/2017.
 */
public abstract class GeneralDAO {

    protected JdbcTemplate jdbcTemplate;

    private static final String JDBC_DRIVER =  "oracle.jdbc.driver.OracleDriver";
    private static final String JDBC_URL = "jdbc:oracle:thin:@exa01-scan3.gde.gob.ar:1521/gedBBDD1";

    public DataSource dataSource(User user) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(JDBC_DRIVER);
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(user.getUsername());
        dataSource.setPassword(user.getPassword());

        return dataSource;
    }
}
