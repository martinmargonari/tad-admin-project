package ar.gob.modernizacion.tad;

import ar.gob.modernizacion.tad.managers.*;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.Grupo;
import ar.gob.modernizacion.tad.model.Tag;
import ar.gob.modernizacion.tad.model.Tramite;
import oracle.sql.DATE;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.SyncFailedException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by martinm on 19/05/17.
 */

@SpringBootApplication
public class Application {

    public static HashMap<Integer,Tramite> tramites;
    public static HashMap<Integer,Documento> documentos;
    public static HashMap<String,List<Tag>> etiquetas;
    public static ArrayList<Grupo> grupos;

    public static String tratasExistentes;
    public static String acronimosTads;

    public static void main(String[] args) {

        ConnectionManager.USER = "mmargonari";
        ConnectionManager.PASSWORD = "orl174A";

        tramites = new HashMap<>();
        documentos = new HashMap<>();
        etiquetas = new HashMap<>();

        tratasExistentes = "";
        acronimosTads = "";

        try {
            TramiteManager.loadTramites();
            DocumentoManager.loadDocumentos();
            //EtiquetaManager.loadEtiquetas();
            GruposManager.loadGrupos();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SpringApplication.run(Application.class, args);
    }
}