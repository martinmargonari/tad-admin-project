package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.DocumentoManager;
import ar.gob.modernizacion.tad.managers.GruposManager;
import ar.gob.modernizacion.tad.managers.RelacionesManager;
import ar.gob.modernizacion.tad.managers.TramiteManager;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.Grupo;
import ar.gob.modernizacion.tad.model.Tramite;
import ar.gob.modernizacion.tad.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showGruposTramites(Model model,
                                     @RequestParam(value="username") String username,
                                     @RequestParam(value = "password") String password) {
        User user = null;
        try {
            user = new User(username, Encrypter.decrypt(password));
            TramiteManager.loadTramites(user);
            GruposManager.loadGrupos(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("tramites", Application.tramites);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "grupos_tramites";
    }

    @RequestMapping(path = "/tramites/tramite", method = RequestMethod.GET)
    public String showGruposTramitesRedirect(@RequestParam(value="selectable_tramites", required = true) int id, Model model,
                                             @ModelAttribute User user, RedirectAttributes ra) {
        ra.addFlashAttribute(user);

        return "redirect:/grupos/tramites/tramite/"+Integer.toString(id);
    }

    @RequestMapping(path = "/tramites/tramite/{id}", method = RequestMethod.GET)
    public String getGruposTramite(@PathVariable("id") int id, Model model, @ModelAttribute User user) {
        Tramite tramite = Application.tramites.get(id);
        ArrayList<Integer> gruposId = null;
        user.setPassword(Encrypter.decrypt(user.getPassword()));
        try {
            gruposId = GruposManager.getGruposTramite(id, user);
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
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "grupos_tramite_configuracion";
    }

    @RequestMapping(path = "/tramites/tramite", method = RequestMethod.POST)
    public String updateGruposTramite(@RequestParam("tramite_id") int id, Model model,
                                      @RequestParam("grupos_relacionados") String gruposRelacionados,
                                      @RequestParam("grupos_configuracion") String gruposConfigurados,
                                      @ModelAttribute User user){

        String listaGruposRelacionados[] = gruposRelacionados.split(",");
        for (String docId: listaGruposRelacionados) {
            if (docId.compareTo("") != 0) {
                Grupo grupo = Application.grupos.get(Integer.parseInt(docId));
                grupo.setRelacionado((byte)0);
            }
        }

        boolean success = true;
        user.setPassword(Encrypter.decrypt(user.getPassword()));
        try {
            GruposManager.updateGruposTramite(id,gruposConfigurados, user);
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }

        model.addAttribute("success",success);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "post_grupo_configuracion";
    }

    @RequestMapping(path = "/documentos", method = RequestMethod.GET)
    public String showGruposDocumentos(Model model,
                                       @RequestParam(value="username") String username,
                                       @RequestParam(value = "password") String password) {
        User user = null;
        try {
            user = new User(username, Encrypter.decrypt(password));
            GruposManager.loadGrupos(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("grupos", Application.grupos);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "grupos_documentos";
    }

    @RequestMapping(path = "/documentos/grupo", method = RequestMethod.GET)
    public String showGruposDocumentosRedirect(Model model, @ModelAttribute User user,
                                               @RequestParam(value="selectable_grupos", required = true) int id,
                                               @RequestParam(value="grupo_nuevo", required = false) String grupoNuevo,
                                               RedirectAttributes ra) {

        int grupoId = 0;
        System.out.println("GRUPO: " + grupoNuevo);
        if (grupoNuevo.compareTo("") != 0) {
            user.setPassword(Encrypter.decrypt(user.getPassword()));
            Grupo grupo = new Grupo(0,grupoNuevo);
            boolean success = true;
            try {
                GruposManager.addNewGrupo(grupo, user);
                grupoId = grupo.getId();
            } catch (SQLException e) {
                e.printStackTrace();
                success = false;
                grupoId = 0;
            }

            model.addAttribute("success", success);
            model.addAttribute("id", String.valueOf(grupoId));
            System.out.println("Nuevo ID: " + String.valueOf(grupoId));
            user.setPassword(Encrypter.encrypt(user.getPassword()));
            model.addAttribute(user);

            return "post_grupo_nuevo";
        }
        ra.addFlashAttribute(user);

        return "redirect:/grupos/documentos/grupo/"+Integer.toString(id);
    }

    @RequestMapping(path = "/documentos/grupo/{id}", method = RequestMethod.GET)
    public String getDocumentosGrupo(@PathVariable("id") int id, Model model, @ModelAttribute User user) {
        Grupo grupo = Application.grupos.get(id);
        ArrayList<Integer> documentosId = null;
        user.setPassword(Encrypter.decrypt(user.getPassword()));
        try {
            DocumentoManager.loadDocumentos(user);
            Iterator it = Application.documentos.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                Documento documento = (Documento) pair.getValue();
                documento.setRelacionado((byte)0);
            }

            documentosId = GruposManager.getDocumentosGrupo(id, user);
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
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "grupos_documentos_configuracion";
    }

    @RequestMapping(path = "/grupo/documentos", method = RequestMethod.POST)
    public String updateDocumentosGrupo(@RequestParam("grupo_id") int id, Model model, @ModelAttribute User user,
                                        @RequestParam("documentos_relacionados") String documentosRelacionados,
                                        @RequestParam("documentos_configuracion") String documentosConfigurados) {

        user.setPassword(Encrypter.decrypt(user.getPassword()));

        String listaDocumentosRelacionados[] = documentosRelacionados.split(",");
        for (String docId: listaDocumentosRelacionados) {
            if (docId.compareTo("") != 0) {
                Documento documento = Application.documentos.get(Integer.parseInt(docId));
                documento.setRelacionado((byte)0);
                documento.relacion = null;
            }
        }

        boolean success = true;
        try {
            GruposManager.updateDocumentosGrupo(id,documentosConfigurados, user);
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }

        model.addAttribute("success", success);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);


        return "post_grupo_configuracion";
    }





}
