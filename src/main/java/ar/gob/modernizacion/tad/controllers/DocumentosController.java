package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.managers.ConnectionManager;
import ar.gob.modernizacion.tad.managers.DocumentoManager;
import ar.gob.modernizacion.tad.managers.EtiquetaManager;
import ar.gob.modernizacion.tad.model.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MMargonari on 26/05/2017.
 */

@Controller
@RequestMapping("/documentos")
public class DocumentosController {

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String documento_nuevo() {
        return "documento_nuevo";
    }

    @RequestMapping(method = RequestMethod.POST)
    public void post_documento_nuevo(
            @RequestParam(value="acronimo_gedo", required=true) String acronimo_gedo,
            @RequestParam (value="acronimo_tad", required=true) String acronimo_tad,
            @RequestParam (value="nombre", required = true) String nombre,
            @RequestParam (value="descripcion", required = true) String descripcion,
            @RequestParam (value="es_embebido", required = true) String es_embebido) {

        Connection connection = ConnectionManager.connect();

        String embebido = "0";

        if (es_embebido.contentEquals("SI")) {
            embebido = "1";
        }

        if (connection != null) {
            try {
                DocumentoManager.insertDocumento(connection,acronimo_gedo,acronimo_tad,nombre,descripcion,embebido);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ConnectionManager.disconnect(connection);
        }



    }
}
