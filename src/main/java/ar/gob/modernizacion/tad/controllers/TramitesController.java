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

import javax.websocket.server.PathParam;
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
        model.addAttribute("tags_organismo", etiquetas.get("\"Organismo\""));
        model.addAttribute("tags_tema",  etiquetas.get("\"Temática\""));
        model.addAttribute("tags_categoria",  etiquetas.get("\"Categoría\""));
        model.addAttribute("tratas_existentes", Application.tratasExistentes);

        return "tramite_nuevo";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(
        @RequestParam (value="descripcion", required=true) String descripcion,
        @RequestParam (value="usuario_creacion", required = true) String usuario_creacion,
        @RequestParam (value="trata", required=true) String trata,
        @RequestParam (value="usuario_iniciador", required = true) String usuario_iniciador,
        @RequestParam (value="reparticion", required = true) String reparticion,
        @RequestParam (value="sector", required = true) String sector,
        @RequestParam (value="nombre", required = true) String nombre,
        @RequestParam (value="text_selected_tags", required = true) String text_selected_tags,
        @RequestParam (value="descripcion_html", required = true) String descripcion_html,
        @RequestParam (value="tiene_pago", required = true) String tiene_pago,
        @RequestParam (value="id_sir", required = false, defaultValue = "") String id_sir,
        @RequestParam (value="obligatorio_interviniente", required = true) String obligatorio_interviniente,
        @RequestParam (value="tiene_prevalidacion", required = true) String tiene_prevalidacion) {

        String tags="{\"tags\":[";
        tags += text_selected_tags;
        tags += "]}";

        byte id_tramite_configuracion = 1;
        byte pago = 0;

        if (tiene_pago.contentEquals("SI")) {
            id_tramite_configuracion = 2;
            pago = 1;
        }

        byte prevalidacion = 0;
        if (tiene_prevalidacion.contentEquals("SI")) {
            prevalidacion = 1;
        }

        byte obligatorio = 0;
        if (obligatorio_interviniente.contentEquals("SI")) {
            obligatorio = 1;
        }

        byte visible = 1;

        Tramite tramite = new Tramite(0,descripcion,id_tramite_configuracion,usuario_creacion,trata,usuario_iniciador,reparticion,sector,nombre,tags,pago,id_sir,descripcion_html,obligatorio,prevalidacion,visible);

        try {
            TramiteManager.insertTramite(tramite);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.GET)
    public String showTramitesModificaciones(Model model) {
        model.addAttribute("tramites", Application.tramites);

        return "tramites_modificaciones";
    }

    @RequestMapping(path = "/modificaciones/tramite", method = RequestMethod.GET)
    public String modificar(@RequestParam(value="selectable_tramites", required = true) int id, Model model) {

        return "redirect:/tramites/modificaciones/tramite/"+Integer.toString(id);
    }

    @RequestMapping(path = "/modificaciones/tramite/{id}", method = RequestMethod.GET)
    public String getTramiteModificacion(@PathVariable("id") int id, Model model) {

        Tramite tramite = Application.tramites.get(id);
        model.addAttribute("tramite",tramite);
        model.addAttribute("tags_organismo", etiquetas.get("\"Organismo\""));
        model.addAttribute("tags_tema",  etiquetas.get("\"Temática\""));
        model.addAttribute("tags_categoria",  etiquetas.get("\"Categoría\""));

        return "tramite_modificar";
    }

    @RequestMapping(path = "/relaciones/tramite", method = RequestMethod.GET)
    public String addRelacion(@RequestParam(value="selectable_tramites", required = true) int id, Model model) {

        return "redirect:/tramites/relaciones/tramite/"+Integer.toString(id);
    }

    @RequestMapping(path = "/relaciones/tramite/{tramiteId}", method = RequestMethod.GET)
    public String getTramiteRelacion(@PathVariable("tramiteId") String tramiteId, Model model) {
        int idTramite = Integer.valueOf(tramiteId);
        Tramite tramite = Application.tramites.get(tramiteId);

        model.addAttribute("documentos",Application.documentos);

        return "tramite_relaciones_configuracion";
    }
}
