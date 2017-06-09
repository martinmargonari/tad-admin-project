package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.*;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.Tag;
import ar.gob.modernizacion.tad.model.Tramite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.sql.Array;
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
        try {
            TramiteManager.loadTramites();
            EtiquetaManager.loadEtiquetas();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("tags", Application.etiquetas);
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

        return "redirect:/home";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.GET)
    public String showTramitesModificaciones(Model model) {

        try {
            TramiteManager.loadTramites();
            EtiquetaManager.loadEtiquetas();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
        String etiquetas = tramite.getEtiquetas();
        int i = etiquetas.indexOf("[") + 1; int f = etiquetas.indexOf("]");
        etiquetas = etiquetas.substring(i,f);
        model.addAttribute("tramite",tramite);
        model.addAttribute("etiquetas",etiquetas);
        model.addAttribute("tags", Application.etiquetas);
        model.addAttribute("tratas_existentes", Application.tratasExistentes);

        return "tramite_modificar";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.POST)
    public String modify(Model model,
            @RequestParam (value="id", required = true) int id,
            @RequestParam (value="descripcion", required=true) String descripcion,
            @RequestParam (value="usuario_modificador", required = true) String usuario_modificador,
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
            @RequestParam (value="tiene_prevalidacion", required = true) String tiene_prevalidacion,
            @RequestParam (value="visible", required = true) String visible_text) {

        Tramite tramite = Application.tramites.get(id);
        tramite.setDescripcion(descripcion);
        tramite.setTrata(trata);
        tramite.setUsuarioIniciador(usuario_iniciador);
        tramite.setReparticion(reparticion);
        tramite.setSector(sector);
        tramite.setNombre(nombre);
        tramite.setDescripcionHtml(descripcion_html);

        String tags="{\"tags\":[";
        tags += text_selected_tags;
        tags += "]}";
        tramite.setEtiquetas(tags);

        byte id_tramite_configuracion = 1;
        byte pago = 0;
        if (tiene_pago.contentEquals("SI")) {
            id_tramite_configuracion = 2;
            pago = 1;
        }
        tramite.setPago(pago);
        tramite.setIdTipoTramiteSir(id_sir);
        tramite.setIdTramiteConfiguracion(id_tramite_configuracion);

        byte prevalidacion = 0;
        if (tiene_prevalidacion.contentEquals("SI")) {
            prevalidacion = 1;
        }
        tramite.setPrevalidacion(prevalidacion);

        byte obligatorio = 0;
        if (obligatorio_interviniente.contentEquals("SI")) {
            obligatorio = 1;
        }
        tramite.setObligatorioInterviniente(obligatorio);

        byte visible = 0;
        if (visible_text.contentEquals("SI")) {
            visible = 1;
        }
        tramite.setVisible(visible);

        try {
            TramiteManager.updateTramite(tramite, usuario_modificador);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/home";
    }

    @RequestMapping(path = "/relaciones", method = RequestMethod.GET)
    public String showTramitesRelaciones(Model model) {

        try {
            TramiteManager.loadTramites();
            DocumentoManager.loadDocumentos();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("tramites", Application.tramites);

        return "tramites_relaciones";
    }

    @RequestMapping(path = "/relaciones/tramite", method = RequestMethod.GET)
    public String relacionar(@RequestParam(value="selectable_tramites", required = true) int id, Model model) {

        return "redirect:/hometramites/relaciones/tramite/"+Integer.toString(id);
    }

    @RequestMapping(path = "/relaciones/tramite/{id}", method = RequestMethod.GET)
    public String getTramiteRelacion(@PathVariable("id") int id, Model model) {
        Tramite tramite = Application.tramites.get(id);
        ArrayList<Integer> docsId = null;
        try {
            docsId = RelacionesManager.getDocumentosRelacionados(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String documentos_relacionados = ",";
        for (int doc: docsId) {
            Documento documento = Application.documentos.get(doc);
            documento.setRelacionado((byte)1);
            documentos_relacionados += doc + ",";
        }

        model.addAttribute("tramite",tramite);
        model.addAttribute("documentos",Application.documentos);
        model.addAttribute("documentos_relacionados",documentos_relacionados);

        return "tramite_relaciones_configuracion";
    }

    @RequestMapping(path = "/relaciones/tramite", method = RequestMethod.POST)
    public String addTramiteRelacion(@RequestParam("tramite_id") int id, Model model,
                                     @RequestParam("usuario") String usuario,
                                     @RequestParam("documentos_relacionados") String docsRelacionados,
                                     @RequestParam("documentos_insert") String docsAgregados,
                                     @RequestParam("documentos_delete") String docsQuitados){

        String listaDocsRelacionados[] = docsRelacionados.split(",");
        String listaDocsAgregados[] = docsAgregados.split(",");
        String listaDocsQuitados[] = docsQuitados.split(",");

        ArrayList<Integer> docsInsert = new ArrayList<>();
        ArrayList<Integer> docsDelete = new ArrayList<>();

        for (String docId: listaDocsRelacionados) {
            if (docId.compareTo("") != 0) {
                Documento documento = Application.documentos.get(Integer.parseInt(docId));
                documento.setRelacionado((byte)0);
            }
        }

        for (String doc: listaDocsAgregados) {
            if (doc.compareTo("") != 0)
                docsInsert.add(Integer.parseInt(doc));
        }

        for (String doc: listaDocsQuitados) {
            if (doc.compareTo("") != 0)
                docsDelete.add(Integer.parseInt(doc));
        }

        try {
            RelacionesManager.updateRelaciones(id, docsInsert, docsDelete,(byte)1,(byte)1,(byte)1,usuario);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return "redirect:/home";
    }
}
