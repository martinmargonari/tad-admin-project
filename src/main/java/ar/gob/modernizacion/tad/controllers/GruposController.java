package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.DocumentoManager;
import ar.gob.modernizacion.tad.managers.GruposManager;
import ar.gob.modernizacion.tad.managers.RelacionesManager;
import ar.gob.modernizacion.tad.managers.TramiteManager;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.Grupo;
import ar.gob.modernizacion.tad.model.Tramite;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by MMargonari on 06/06/2017.
 */

@Controller
@RequestMapping("/grupos")
public class GruposController {

    @RequestMapping(path = "/tramites", method = RequestMethod.GET)
    public String showGruposTramites(Model model) {
        try {
            TramiteManager.loadTramites();
            GruposManager.loadGrupos();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("tramites", Application.tramites);

        return "grupos_tramites";
    }

    @RequestMapping(path = "/tramites/tramite", method = RequestMethod.GET)
    public String showGruposTramitesRedirect(@RequestParam(value="selectable_tramites", required = true) int id, Model model) {

        return "redirect:/grupos/tramites/tramite/"+Integer.toString(id);
    }

    @RequestMapping(path = "/tramites/tramite/{id}", method = RequestMethod.GET)
    public String getGruposTramite(@PathVariable("id") int id, Model model) {
        Tramite tramite = Application.tramites.get(id);
        ArrayList<Integer> gruposId = null;
        try {
            gruposId = GruposManager.getGruposTramite(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Iterator it = Application.grupos.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Grupo grupo = (Grupo)pair.getValue();
            grupo.setRelacionado((byte)0);
        }


        String grupos_relacionados = "";
        for (int grupoId: gruposId) {
            Grupo grupo = Application.grupos.get(grupoId);
            grupo.setRelacionado((byte)1);
            grupos_relacionados += grupoId + ",";
        }

        if (grupos_relacionados.length() > 0)
            grupos_relacionados = grupos_relacionados.substring(0, grupos_relacionados.length() - 1);

        model.addAttribute("tramite",tramite);
        model.addAttribute("grupos",Application.grupos);
        model.addAttribute("grupos_relacionados",grupos_relacionados);

        return "grupos_tramite_configuracion";
    }

    @RequestMapping(path = "/tramites/tramite", method = RequestMethod.POST)
    public String updateGruposTramite(@RequestParam("tramite_id") int id, Model model,
                                      @RequestParam("grupos_relacionados") String gruposRelacionados,
                                      @RequestParam("grupos_configuracion") String gruposConfigurados){

        String listaGruposRelacionados[] = gruposRelacionados.split(",");
        for (String docId: listaGruposRelacionados) {
            if (docId.compareTo("") != 0) {
                Grupo grupo = Application.grupos.get(Integer.parseInt(docId));
                grupo.setRelacionado((byte)0);
            }
        }

        
        try {
            GruposManager.updateGruposTramite(id,gruposConfigurados);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/home";
    }

    @RequestMapping(path = "/documentos", method = RequestMethod.GET)
    public String showGruposDocumentos(Model model) {
        try {
            GruposManager.loadGrupos();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("grupos", Application.grupos);

        return "grupos_documentos";
    }

    @RequestMapping(path = "/documentos/grupo", method = RequestMethod.GET)
    public String showGruposDocumentosRedirect(Model model,
                                               @RequestParam(value="selectable_grupos", required = true) int id,
                                               @RequestParam(value="grupo_nuevo", required = false) String grupoNuevo) {

        int grupoId = 0;
        System.out.println("GRUPO: " + grupoNuevo);
        if (grupoNuevo.compareTo("") != 0) {
            Grupo grupo = new Grupo(0,grupoNuevo);
            boolean success = true;
            try {
                GruposManager.addNewGrupo(grupo);
                grupoId = grupo.getId();
            } catch (SQLException e) {
                e.printStackTrace();
                success = false;
                grupoId = 0;
            }

            model.addAttribute("success", success);
            model.addAttribute("id", String.valueOf(grupoId));
            System.out.println("Nuevo ID: " + String.valueOf(grupoId));

            return "post_grupo_nuevo";
        }

        return "redirect:/grupos/documentos/grupo/"+Integer.toString(id);
    }

    @RequestMapping(path = "/documentos/grupo/{id}", method = RequestMethod.GET)
    public String getDocumentosGrupo(@PathVariable("id") int id, Model model) {
        Grupo grupo = Application.grupos.get(id);
        ArrayList<Integer> documentosId = null;
        try {
            DocumentoManager.loadDocumentos();
            Iterator it = Application.documentos.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                Documento documento = (Documento) pair.getValue();
                documento.setRelacionado((byte)0);
            }

            documentosId = GruposManager.getDocumentosGrupo(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String documentos_relacionados = "";
        for (int docId: documentosId) {
            documentos_relacionados += docId + ",";
        }

        if (documentos_relacionados.length() > 0)
            documentos_relacionados = documentos_relacionados.substring(0,documentos_relacionados.length() - 1);

        model.addAttribute("grupo",grupo);
        model.addAttribute("documentos",Application.documentos);
        model.addAttribute("documentos_relacionados",documentos_relacionados);

        return "grupos_documentos_configuracion";
    }

    @RequestMapping(path = "/grupo/documentos", method = RequestMethod.POST)
    public String updateDocumentosGrupo(@RequestParam("grupo_id") int id, Model model,
                                        @RequestParam("documentos_relacionados") String documentosRelacionados,
                                        @RequestParam("documentos_configuracion") String documentosConfigurados) {

        String listaDocumentosRelacionados[] = documentosRelacionados.split(",");
        for (String docId: listaDocumentosRelacionados) {
            if (docId.compareTo("") != 0) {
                Documento documento = Application.documentos.get(Integer.parseInt(docId));
                documento.setRelacionado((byte)0);
                documento.relacion = null;
            }
        }

        try {
            GruposManager.updateDocumentosGrupo(id,documentosConfigurados);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/home";
    }





}
