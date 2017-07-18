package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Created by MMargonari on 06/07/2017.
 */
public abstract class GeneralDAO {

    protected JdbcTemplate jdbcTemplate;
    protected String table;
    protected ArrayList<String> columns;

    private static final String JDBC_DRIVER =  "oracle.jdbc.driver.OracleDriver";
    private static final String JDBC_URL = "jdbc:oracle:thin:@exa01-scan3.gde.gob.ar:1521/gedBBDD1";

    protected DataSource dataSource(User user) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(JDBC_DRIVER);
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(user.getUsername());
        dataSource.setPassword(user.getPassword());

        return dataSource;
    }

    private String removeLastComma(String value) {
        return value.substring(0, value.length() - 1);
    }

    private String getParameters() {
        String parameters;
        String values;
        StringBuilder parametersBuilder = new StringBuilder("(");
        StringBuilder valuesBuilder = new StringBuilder(" VALUES (");
        for (String column: columns) {
            parametersBuilder = parametersBuilder.append(column).append(",");
            valuesBuilder.append("?,");
        }
        values = valuesBuilder.toString();
        parameters = parametersBuilder.toString();

        parameters = removeLastComma(parameters);
        parameters += ")";
        values = values.substring(0, values.length() - 1);
        values += ")";

        return (parameters + values);
    }

    protected void makeInsert(ArrayList<Object> columnsValues) {
        String sql = "INSERT INTO " + this.table + this.getParameters();
        jdbcTemplate.update(sql, columnsValues);
    }

    protected String getSelectStatement() {
        return getSelectStatement(null);
    }

    protected String getSelectStatement(String condition) {
        String sql;
        StringBuilder sqlBuilder = new StringBuilder("SELECT ");

        String parameters;
        StringBuilder parametersBuilder = new StringBuilder();
        for (String column: columns) {
            parametersBuilder = parametersBuilder.append(column).append(",");
        }

        parameters = parametersBuilder.toString();
        parameters = removeLastComma(parameters);
        sqlBuilder.append(parameters);
        sqlBuilder.append(" FROM ");
        sqlBuilder.append(table);

        if (condition != null) {
            sqlBuilder.append(" WHERE ").append(condition);
        }

        sql = sqlBuilder.toString();

        return sql;
    }
}
