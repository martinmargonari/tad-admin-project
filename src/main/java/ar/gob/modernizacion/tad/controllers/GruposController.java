package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.GruposManager;
import ar.gob.modernizacion.tad.managers.RelacionesManager;
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

/**
 * Created by MMargonari on 06/06/2017.
 */

@Controller
@RequestMapping("/grupos")
public class GruposController {

    @RequestMapping(path = "/tramites", method = RequestMethod.GET)
    public String showGruposTramites(Model model) {
        try {
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
        String grupos_relacionados = ",";
        for (int grupoId: gruposId) {
            Grupo grupo = Application.grupos.get(grupoId);
            grupo.setRelacionado((byte)1);
            grupos_relacionados += grupoId + ",";
        }

        model.addAttribute("tramite",tramite);
        model.addAttribute("grupos",Application.grupos);
        model.addAttribute("grupos_relacionados",grupos_relacionados);

        return "grupos_tramite_configuracion";
    }

    @RequestMapping(path = "/tramites/tramite", method = RequestMethod.POST)
    public String updateGruposTramite(@RequestParam("tramite_id") int id, Model model,
                                      @RequestParam("grupos_relacionados") String gruposRelacionados,
                                      @RequestParam("grupos_insert") String gruposAgregados, 
                                      @RequestParam("grupos_delete") String gruposQuitados){


        String listaGruposRelacionados[] = gruposRelacionados.split(",");
        String listaGruposAgregados[] = gruposAgregados.split(",");
        String listaGruposQuitados[] = gruposQuitados.split(",");

        ArrayList<Integer> gruposInsert = new ArrayList<>();
        ArrayList<Integer> gruposDelete = new ArrayList<>();

        for (String grupoId: listaGruposRelacionados) {
            if (grupoId.compareTo("") != 0) {
                Grupo grupo = Application.grupos.get(Integer.parseInt(grupoId));
                grupo.setRelacionado((byte)0);
            }
        }

        for (String doc: listaGruposAgregados) {
            if (doc.compareTo("") != 0)
                gruposInsert.add(Integer.parseInt(doc));
        }

        for (String doc: listaGruposQuitados) {
            if (doc.compareTo("") != 0)
                gruposDelete.add(Integer.parseInt(doc));
        }

        try {
            GruposManager.updateGruposTramite(id,gruposInsert,gruposDelete);
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

        int grupoId = id;

        if (grupoNuevo.compareTo("") != 0) {
            Grupo grupo = new Grupo(0,grupoNuevo);
            try {
                GruposManager.addNewGrupo(grupo);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            grupoId = grupo.getId();
        }

        return "redirect:/grupos/documentos/grupo/"+Integer.toString(grupoId);
    }

    @RequestMapping(path = "/documentos/grupo/{id}", method = RequestMethod.GET)
    public String getDocumentosGrupo(@PathVariable("id") int id, Model model) {
        Grupo grupo = Application.grupos.get(id);
        ArrayList<Integer> documentosId = null;
        try {
            documentosId = GruposManager.getDocumentosGrupo(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String documentos_relacionados = ",";
        for (int docId: documentosId) {
            Documento documento = Application.documentos.get(docId);
            documento.setRelacionado((byte)1);
            documentos_relacionados += docId + ",";
        }

        model.addAttribute("grupo",grupo);
        model.addAttribute("documentos",Application.documentos);
        model.addAttribute("documentos_relacionados",documentos_relacionados);

        return "grupos_documentos_configuracion";
    }

    @RequestMapping(path = "/grupo/documentos", method = RequestMethod.POST)
    public String updateDocumentosGrupo(@RequestParam("grupo_id") int id, Model model,
                                        @RequestParam("documentos_relacionados") String documentosRelacionados,
                                        @RequestParam("documentos_insert") String documentosAgregados,
                                        @RequestParam("documentos_delete") String documentosQuitados){

        String listaDocumentosRelacionados[] = documentosRelacionados.split(",");
        String listaDocumentosAgregados[] = documentosAgregados.split(",");
        String listaDocumentosQuitados[] = documentosQuitados.split(",");

        ArrayList<Integer> documentosInsert = new ArrayList<>();
        ArrayList<Integer> documentosDelete = new ArrayList<>();

        for (String docId: listaDocumentosRelacionados) {
            if (docId.compareTo("") != 0) {
                Documento documento = Application.documentos.get(Integer.parseInt(docId));
                documento.setRelacionado((byte)0);
            }
        }

        for (String doc: listaDocumentosAgregados) {
            if (doc.compareTo("") != 0)
                documentosInsert.add(Integer.parseInt(doc));
        }

        for (String doc: listaDocumentosQuitados) {
            if (doc.compareTo("") != 0)
                documentosDelete.add(Integer.parseInt(doc));
        }

        try {
            GruposManager.updateDocumentosGrupo(id,documentosInsert,documentosDelete);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/home";
    }





}
