package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.managers.ConnectionManager;
import ar.gob.modernizacion.tad.managers.DocumentoManager;
import ar.gob.modernizacion.tad.managers.EtiquetaManager;
import ar.gob.modernizacion.tad.managers.TramiteManager;
import ar.gob.modernizacion.tad.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by martinm on 19/05/17.
 */

@Controller
@RequestMapping("/tramites")
public class TramitesController {

    private static HashMap<String, List<Tag>> etiquetas;

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String getNewForm(Model model) {
        if (etiquetas == null) {
            List<Tag> tags = null;
            try {
                tags = EtiquetaManager.getEtiquetas();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            etiquetas = new HashMap<>();
            etiquetas.put("\"Organismo\"",new ArrayList<>());
            etiquetas.put("\"Tema\"",new ArrayList<>());
            etiquetas.put("\"Categoría\"",new ArrayList<>());

            for (Tag tag: tags) {
                etiquetas.get(tag.getCategoria()).add(tag);
            }
        }

        model.addAttribute("tags_organismo", etiquetas.get("\"Organismo\""));
        model.addAttribute("tags_tema",  etiquetas.get("\"Tema\""));
        model.addAttribute("tags_categoria",  etiquetas.get("\"Categoría\""));

        return "tramite_nuevo";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(
        @RequestParam (value="descripcion", required=true) String descripcion,
        @RequestParam (value="trata", required=true) String trata,
        @RequestParam (value="usuario", required = true) String usuario,
        @RequestParam (value="reparticion", required = true) String reparticion,
        @RequestParam (value="sector", required = true) String sector,
        @RequestParam (value="nombre", required = true) String nombre,
        @RequestParam (value="text_selected_tags", required = true) String text_selected_tags,
        @RequestParam (value="descripcion_html", required = true) String descripcion_html,
        @RequestParam (value="tiene_pago", required = true) String tiene_pago,
        @RequestParam (value="id_sir", required = false, defaultValue = "") String id_sir,
        @RequestParam (value="tiene_prevalidacion", required = true) String tiene_prevalidacion) {

        ArrayList<String> tags = new ArrayList<>();
        tags.add("Ministerio de Agroindustria");
        tags.add("Actualizaciones");

        String id_tramite_configuracion = "1";
        String pago = "0";

        if (tiene_pago.contentEquals("SI")) {
            id_tramite_configuracion = "2";
            pago ="1";
        }

        String prevalidacion = "0";
        if (tiene_prevalidacion.contentEquals("SI")) {
            prevalidacion = "1";
        }

/*
        if (connection != null) {
            try {
                TramiteManager.insertTramite(connection, descripcion, id_tramite_configuracion, trata, usuario, reparticion, sector, nombre, tags, pago, id_sir, descripcion_html, prevalidacion);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ConnectionManager.disconnect(connection);
        }
*/
        return "redirect:/";
    }

    @RequestMapping("/documento_nuevo")
    public String documento_nuevo() {
        return "documento_nuevo";
    }

    @RequestMapping("/post_documento_nuevo")
    public void post_documento_nuevo(
            @RequestParam (value="acronimo_gedo", required=true) String acronimo_gedo,
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
