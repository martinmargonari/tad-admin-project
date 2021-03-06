package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.model.Tramite;
import ar.gob.modernizacion.tad.model.User;
import ar.gob.modernizacion.tad.model.constants.DBTables;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by martinm on 24/05/17.
 */
public class TramiteManager {

    private static String ID="ID";
    private static String DESCRIPCION="DESCRIPCION";
    private static String ID_TRAMITE_CONFIGURACION="ID_TRAMITE_CONFIGURACION";
    private static String ID_TRAMITE_TEMPLATE="ID_TRAMITE_TEMPLATE";
    private static String USUARIO_CREACION="USUARIO_CREACION";
    private static String TRATA_EE="TRATA_EE";
    private static String USUARIO_INICIADOR_EE="USUARIO_INICIADOR_EE";
    private static String REPARTICION_INICIADORA_EE="REPARTICION_INICIADORA_EE";
    private static String SECTOR_INICIADOR_EE="SECTOR_INICIADOR_EE";
    private static String NOMBRE="NOMBRE";
    private static String ETIQUETAS="ETIQUETAS";
    private static String PAGO="PAGO";
    private static String ID_TIPO_TRAMITE_SIR="ID_TIPO_TRAMITE_SIR";
    private static String DESCRIPCION_HTML="DESCRIPCION_HTML";
    private static String DESCRIPCION_CORTA="DESCRIPCION_CORTA";
    private static String OBLIGATORIO_INTERVINIENTE="OBLIGATORIO_INTERVINIENTE";
    private static String ID_ESTADO_INICIAL="ID_ESTADO_INICIAL";
    private static String VISIBLE="VISIBLE";
    private static String PREVALIDACION="PREVALIDACION";
    private static String FECHA_ALTA="FECHA_ALTA";
    private static String USUARIO_MODIFICACION="USUARIO_MODIFICACION";
    private static String FECHA_MODIFICACION="FECHA_MODIFICACION";
    private static String ID_TIPO_TRAMITE="ID_TIPO_TRAMITE";
    private static String CUIT="CUIT";

    private static boolean LOADED=false;

    public static void loadTramites(User user) throws SQLException {
        if (LOADED) return;

        Connection connection = ConnectionManager.connect(user);

        String query = "SELECT " +
                ID+","+DESCRIPCION+","+ID_TRAMITE_CONFIGURACION+","+ID_TRAMITE_TEMPLATE+","+USUARIO_CREACION+","+
                TRATA_EE+","+USUARIO_INICIADOR_EE+","+REPARTICION_INICIADORA_EE+","+SECTOR_INICIADOR_EE+","+NOMBRE+","+
                ETIQUETAS+","+PAGO+","+ID_TIPO_TRAMITE_SIR+","+DESCRIPCION_HTML+","+DESCRIPCION_CORTA+","+OBLIGATORIO_INTERVINIENTE+","+
                ID_ESTADO_INICIAL+","+VISIBLE+","+PREVALIDACION+" "
                + "FROM " + DBTables.TAD_TIPO_TRAMITE
                + " ORDER BY "+ ID;

        Statement stmt = null;
        Tramite tramite = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                byte pago = 0;
                byte obligatorio = 0;
                byte prevalidacion = 0;
                byte visible = 0;
                if (rs.getString(PAGO) != null) pago = Byte.valueOf(rs.getString(PAGO));
                if (rs.getString(OBLIGATORIO_INTERVINIENTE) != null) obligatorio = Byte.valueOf(rs.getString(OBLIGATORIO_INTERVINIENTE));
                if (rs.getString(PREVALIDACION) != null) prevalidacion = Byte.valueOf(rs.getString(PREVALIDACION));
                if (rs.getString(VISIBLE) != null) visible = Byte.valueOf(rs.getString(VISIBLE));
                tramite = new Tramite(
                        Integer.valueOf(rs.getString(ID)),
                        rs.getString(DESCRIPCION),
                        rs.getByte(ID_TRAMITE_CONFIGURACION),
                        rs.getString(USUARIO_CREACION),
                        rs.getString(TRATA_EE),
                        rs.getString(USUARIO_INICIADOR_EE),
                        rs.getString(REPARTICION_INICIADORA_EE),
                        rs.getString(SECTOR_INICIADOR_EE),
                        rs.getString(NOMBRE),
                        rs.getString(ETIQUETAS),
                        pago,
                        rs.getString(ID_TIPO_TRAMITE_SIR),
                        rs.getString(DESCRIPCION_HTML),
                        rs.getString(DESCRIPCION_CORTA),
                        obligatorio,
                        prevalidacion,
                        visible);
                Application.tramites.put(tramite.getId(),tramite);
                Application.tratasExistentes += rs.getString(TRATA_EE)+",";
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        ConnectionManager.disconnect(connection);

        System.out.println("LOADED  ");

        LOADED = true;

    }

    public static synchronized void insertTramite(Tramite tramite, User user) throws SQLException {

        Connection connection = ConnectionManager.connect(user);

        String queryMaxID = "select MAX(ID) from " + DBTables.TAD_TIPO_TRAMITE;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryMaxID);
            if (rs.next()) {
                tramite.setId(rs.getInt(1) + 1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        java.util.Date date = new java.util.Date(Calendar.getInstance().getTime().getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yy");
        String fechaAlta = formatDate.format(date).toUpperCase();


        String insertQuery = "INSERT INTO " + DBTables.TAD_TIPO_TRAMITE +
                "("+ID+","+DESCRIPCION+","+ID_TRAMITE_CONFIGURACION+","+ID_TRAMITE_TEMPLATE+","+USUARIO_CREACION+","+
                FECHA_ALTA+","+TRATA_EE+","+USUARIO_INICIADOR_EE+","+REPARTICION_INICIADORA_EE+","+SECTOR_INICIADOR_EE+","+NOMBRE+","+
                ETIQUETAS+","+PAGO+","+ID_TIPO_TRAMITE_SIR+","+DESCRIPCION_HTML+","+DESCRIPCION_CORTA+","+OBLIGATORIO_INTERVINIENTE+","+
                ID_ESTADO_INICIAL+","+VISIBLE+","+PREVALIDACION+") "
                +"VALUES"+
                 "(" +Integer.toString(tramite.getId())+","+formatSQLString(tramite.getDescripcion())+","+tramite.getIdTramiteConfiguracion()+
                ",1,"+formatSQLString(tramite.getUsuarioCreacion())+","+formatSQLString(fechaAlta)+","+formatSQLString(tramite.getTrata())+
                ","+formatSQLString(tramite.getUsuarioIniciador())+","+formatSQLString(tramite.getReparticion())+ ","+
                formatSQLString(tramite.getSector())+","+formatSQLString(tramite.getNombre())+","+
                formatSQLString(tramite.getEtiquetas())+","+tramite.getPago()+","+formatSQLString(tramite.getIdTipoTramiteSir())+","+
                formatSQLString(tramite.getDescripcionHtml())+","+formatSQLString(tramite.getDescripcionCorta())+","+tramite.getObligatorioInterviniente()+",0,"+tramite.getVisible()+","+tramite.getPrevalidacion()+")";

        Statement insertStatement = null;

        System.out.println(insertQuery);

        try {
            insertStatement = connection.createStatement();
            insertStatement.executeUpdate(insertQuery);
            insertStatement.close();
        } catch (SQLException e) {
            if (insertStatement != null)
                insertStatement.close();
            ConnectionManager.disconnect(connection);
            throw new SQLException(e);
        }

        Application.tramites.put(tramite.getId(),tramite);

        ConnectionManager.disconnect(connection);
    }

    public static void updateTramite(Tramite tramite, String usuario, User user) throws SQLException {

        Connection connection = ConnectionManager.connect(user);

        java.util.Date date = new java.util.Date(Calendar.getInstance().getTime().getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yy");
        String fechaModificacion = formatDate.format(date).toUpperCase();


        String updateQuery = "UPDATE " + DBTables.TAD_TIPO_TRAMITE + " SET " +
                DESCRIPCION+"="+formatSQLString(tramite.getDescripcion())+","+
                ID_TRAMITE_CONFIGURACION+"="+tramite.getIdTramiteConfiguracion()+","+
                USUARIO_MODIFICACION+"="+formatSQLString(usuario)+","+
                FECHA_MODIFICACION+"="+formatSQLString(fechaModificacion)+","+
                TRATA_EE+"="+formatSQLString(tramite.getTrata())+","+
                USUARIO_INICIADOR_EE+"="+formatSQLString(tramite.getUsuarioIniciador())+","+
                REPARTICION_INICIADORA_EE+"="+formatSQLString(tramite.getReparticion())+","+
                SECTOR_INICIADOR_EE+"="+formatSQLString(tramite.getSector())+","+
                NOMBRE+"="+formatSQLString(tramite.getNombre())+","+
                ETIQUETAS+"="+formatSQLString(tramite.getEtiquetas())+","+
                PAGO+"="+tramite.getPago()+","+
                ID_TIPO_TRAMITE_SIR+"="+formatSQLString(tramite.getIdTipoTramiteSir())+","+
                DESCRIPCION_HTML+"="+formatSQLString(tramite.getDescripcionHtml())+","+
                DESCRIPCION_CORTA+"="+formatSQLString(tramite.getDescripcionCorta())+","+
                OBLIGATORIO_INTERVINIENTE+"="+tramite.getObligatorioInterviniente()+","+
                VISIBLE+"="+tramite.getVisible()+","+
                PREVALIDACION+"="+tramite.getPrevalidacion()+" "
                +"WHERE "+ ID + " = " + Integer.toString(tramite.getId());

        Statement updateStatement = null;

        System.out.println(updateQuery);

        try {
            updateStatement = connection.createStatement();
            updateStatement.executeUpdate(updateQuery);
            updateStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (updateStatement != null)
                updateStatement.close();
        }

        ConnectionManager.disconnect(connection);
    }

    public static ArrayList<String> getPrevalidaciones(int tramiteId, User user) throws SQLException {
        Connection connection = ConnectionManager.connect(user);
        ArrayList<String> cuits = new ArrayList<>();

        String query = "select " + CUIT +
                " from " + DBTables.TAD_PREVALIDACION +
                " where " + ID_TIPO_TRAMITE + "=" + tramiteId;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                cuits.add(rs.getString(CUIT));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        ConnectionManager.disconnect(connection);

        return cuits;
    }

    /**
     * @param tramiteId id del tramite
     * @param cuitsInsert string format: [CUIT]
     *                   separados por ,
     *                   Por ejemplo: 20304445557;21367778888
     */
    public static void asociarCuits(int tramiteId, String cuitsInsert, User user) throws SQLException {
        Connection connection = ConnectionManager.connect(user);
        int id = 0;

        String queryMaxID = "select MAX(ID) from " + DBTables.TAD_PREVALIDACION;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryMaxID);
            if (rs.next()) {
                id = rs.getInt(1) + 1;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        System.out.println("LISTA: " + cuitsInsert);
        String listCuits[] = cuitsInsert.split(",");

        for (String cuit: listCuits) {
            String insertQuery = "INSERT INTO " + DBTables.TAD_PREVALIDACION +
                    "(" +  ID + "," + ID_TIPO_TRAMITE + "," + CUIT + ") "
                    + "VALUES" +
                    "(" + Integer.toString(id) + "," + Integer.toString(tramiteId) + "," + cuit + ")";
            System.out.println(insertQuery);

            Statement insertStatement = null;
            try {
                insertStatement = connection.createStatement();
                insertStatement.executeUpdate(insertQuery);
                insertStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (insertStatement != null)
                    insertStatement.close();
            }
            
            id++;
        }
        
        ConnectionManager.disconnect(connection);
    }


    public static void updatePrevalidaciones(int tramiteId, String cuitsInsert, User user) throws SQLException {
        Connection connection = ConnectionManager.connect(user);

        String deleteQuery = "DELETE FROM " + DBTables.TAD_PREVALIDACION +
                " WHERE " + ID_TIPO_TRAMITE + "=" + Integer.toString(tramiteId);

        Statement deleteStatement = null;
        try {
            deleteStatement = connection.createStatement();
            deleteStatement.executeUpdate(deleteQuery);
            deleteStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (deleteStatement != null)
                deleteStatement.close();
        }

        ConnectionManager.disconnect(connection);

        asociarCuits(tramiteId, cuitsInsert, user);
    }




    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }


}
