package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by MMargonari on 06/07/2017.
 */
public abstract class GeneralDAO {

    protected JdbcTemplate jdbcTemplate;
    protected String table;
    protected ArrayList<String> columns;

    private static final String JDBC_DRIVER =  "oracle.jdbc.driver.OracleDriver";
    private static final String JDBC_URL = "jdbc:oracle:thin:@172.16.164.97:1521/gedBBDD1";

    protected DataSource dataSource(User user) {

        /*
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(JDBC_DRIVER);
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(user.getUsername());
        dataSource.setPassword(user.getPassword());
        */

        DataSource dataSource = new JndiDataSourceLookup().getDataSource("jdbc/tad2DS");
        UserCredentialsDataSourceAdapter dsAdapter = new UserCredentialsDataSourceAdapter();
        dsAdapter.setTargetDataSource(dataSource);
        dsAdapter.setUsername(user.getUsername());
        dsAdapter.setPassword(user.getPassword());

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

    protected String getInsertStatement() {
        return "INSERT INTO " + this.table + this.getParameters();
    }

    protected String getUpdateStatement() {
        StringBuilder parametersBuilder = new StringBuilder(" ");
        for (int i = 1; i < columns.size(); i++) {
            parametersBuilder = parametersBuilder.append(columns.get(i)).append("=?,");
        }

        String parameters = parametersBuilder.toString();
        parameters = removeLastComma(parameters);
        parameters += " WHERE " + columns.get(0) + "=?";

        return "UPDATE " + this.table + " SET " + parameters;
    }

    protected void makeInsert(ArrayList<Object> columnsValues) throws DataAccessException {
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

    protected int getMaxId(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        String sql = "SELECT MAX(ID) as maxID FROM " + table;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {

            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getInt("maxID");
                }
                return 0;
            }
        });
    }

    protected String getToday() {
        java.util.Date date = new java.util.Date(Calendar.getInstance().getTime().getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yy");
        return formatDate.format(date).toUpperCase();
    }
}
