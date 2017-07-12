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

    public DataSource dataSource(User user) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@exa01-scan3.gde.gob.ar:1521/gedBBDD1");
        dataSource.setUsername(user.getUsername());
        dataSource.setPassword(user.getPassword());

        return dataSource;
    }
}
