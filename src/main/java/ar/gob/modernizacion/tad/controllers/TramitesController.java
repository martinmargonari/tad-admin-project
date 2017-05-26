package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.ConnectionManager;
import ar.gob.modernizacion.tad.managers.DocumentoManager;
import ar.gob.modernizacion.tad.managers.EtiquetaManager;
import ar.gob.modernizacion.tad.managers.TramiteManager;
import ar.gob.modernizacion.tad.model.Tag;
import ar.gob.modernizacion.tad.model.Tramite;
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

        String tags="{\"tags\":[";
        String [] tags_read = text_selected_tags.split("\\\\r?\\\\n ");
        for (String tag: tags_read) {
            tags = tags + tag.replace("\n", "").replace("\r", "");
            tags = tags + ",";
        }
        // Remove last comma
        tags=tags.substring(0,tags.length()-1);

        // Close tags
        tags=tags+"]}";

        System.out.println(tags);
        char id_tramite_configuracion = '1';
        char pago = '0';

        if (tiene_pago.contentEquals("SI")) {
            id_tramite_configuracion = '2';
            pago = '1';
        }

        char prevalidacion = '0';
        if (tiene_prevalidacion.contentEquals("SI")) {
            prevalidacion = '1';
        }
        /*
        Tramite tramite = new Tramite(0,descripcion,id_tramite_configuracion,trata,usuario,reparticion,sector,nombre,tags,pago,id_sir,descripcion_html,prevalidacion,'1');

        try {
            TramiteManager.insertTramite(tramite);
        } catch (SQLException e) {
            e.printStackTrace();
        }
*/
        return "redirect:/";
    }

    @RequestMapping("/relaciones")
    public String showTramitesRelaciones(Model model) {
        model.addAttribute("tramites", Application.tramites);

        return "tramites_relaciones";
    }

    @RequestMapping("/relaciones/tramite")
    public String addRelacion(@RequestParam(value="selectable_tramites", required = true) String descripcion, Model model) {
        System.out.println(descripcion);

        return "redirect:/";
    }
}
