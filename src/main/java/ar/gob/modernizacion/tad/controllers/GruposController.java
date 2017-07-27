package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.dao.*;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.Grupo;
import ar.gob.modernizacion.tad.model.GrupoDocumentoTipoDocumento;
import ar.gob.modernizacion.tad.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMargonari on 06/06/2017.
 */

@Controller
@RequestMapping("/grupos")
public class GruposController {

    @Autowired
    private GrupoDAO grupoDAO;

    @Autowired
    private TramiteDAO tramiteDAO;

    @Autowired
    private TipoTramiteGrupoDocumentoDAO tipoTramiteGrupoDocumentoDAO;

    @Autowired
    private GrupoDocumentoTipoDocumentoDAO grupoDocumentoTipoDocumentoDAO;

    @Autowired
    private DocumentoDAO documentoDAO;

    @RequestMapping(path = "/tramites", method = RequestMethod.GET)
    public String showGruposTramites(Model model,
                                     @RequestParam(value="username") String username,
                                     @RequestParam(value = "password") String password) {

        User user = new User(username, password);

        model.addAttribute("tramites", tramiteDAO.list(user));
        user.encryptPassword();
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
    public String getGruposTramite(@PathVariable("id") int id, Model model) {
        User user = (User) model.asMap().get("user");
        user.decryptPassword();

        List<Integer> gruposId = tipoTramiteGrupoDocumentoDAO.list(id, user);

        List<Grupo> grupos = grupoDAO.list(user);
        List<Grupo> datosGruposRelacionados = new ArrayList<>();
        String grupos_relacionados = "";
        for (int grupoId: gruposId) {
            grupos_relacionados += grupoId + ",";
            datosGruposRelacionados.add(grupoDAO.get(grupoId, user));
        }

        if (grupos_relacionados.length() > 0)
            grupos_relacionados = grupos_relacionados.substring(0, grupos_relacionados.length() - 1);

        model.addAttribute("tramite", tramiteDAO.get(id, user));
        model.addAttribute("grupos", grupos);
        model.addAttribute("grupos_relacionados",grupos_relacionados);
        model.addAttribute("datosGruposRelacionados", datosGruposRelacionados);
        user.encryptPassword();
        model.addAttribute(user);

        return "grupos_tramite_configuracion";
    }

    @RequestMapping(path = "/tramites/tramite", method = RequestMethod.POST)
    public String updateGruposTramite(@RequestParam("tramite_id") int id, Model model,
                                      @RequestParam("grupos_insert") String gruposInsert,
                                      @RequestParam("grupos_delete") String gruposDelete,
                                      @ModelAttribute User user){

        user.decryptPassword();

        boolean success = true;
        for (String grupo: gruposDelete.split(",")) {
            if (grupo.isEmpty()) break;
            tipoTramiteGrupoDocumentoDAO.delete(Integer.valueOf(grupo), id, user);
        }

        for (String grupo: gruposInsert.split(",")) {
            if (grupo.isEmpty()) break;
            tipoTramiteGrupoDocumentoDAO.insert(Integer.valueOf(grupo), id, user);
        }

        model.addAttribute("success", success);
        user.encryptPassword();
        model.addAttribute(user);

        return "post_grupo_configuracion";
    }

    @RequestMapping(path = "/documentos", method = RequestMethod.GET)
    public String showGruposDocumentos(Model model,
                                       @RequestParam(value="username") String username,
                                       @RequestParam(value = "password") String password) {

        User user = new User(username, password);

        model.addAttribute("grupos", grupoDAO.list(user));
        user.encryptPassword();
        model.addAttribute(user);

        return "grupos_documentos";
    }

    @RequestMapping(path = "/documentos/grupo", method = RequestMethod.GET)
    public String showGruposDocumentosRedirect(Model model, @ModelAttribute User user,
                                               @RequestParam(value="selectable_grupos", required = true) int id,
                                               @RequestParam(value="grupo_nuevo", required = false) String grupoNuevo,
                                               RedirectAttributes ra) {

        int grupoId = 0;
        user.decryptPassword();
        if (grupoNuevo.compareTo("") != 0) {
            Grupo grupo = new Grupo(0,grupoNuevo);
            boolean success = true;
            try {
                grupoDAO.insert(grupo, user);
                grupoId = grupo.getId();
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
                grupoId = 0;
            }

            model.addAttribute("success", success);
            model.addAttribute("id", String.valueOf(grupoId));

            user.encryptPassword();
            model.addAttribute(user);

            return "post_grupo_nuevo";
        }
        user.encryptPassword();
        ra.addFlashAttribute(user);

        return "redirect:/grupos/documentos/grupo/"+Integer.toString(id);
    }

    @RequestMapping(path = "/documentos/grupo/{id}", method = RequestMethod.GET)
    public String getDocumentosGrupo(@PathVariable("id") int id, Model model,
                                     @ModelAttribute User user) {

        if (user.getUsername() == null)
            user = (User) model.asMap().get("user");
        user.decryptPassword();

        Grupo grupo = grupoDAO.get(id, user);
        List<Documento> documentos = documentoDAO.list(user);
        List<GrupoDocumentoTipoDocumento> grupoDocumentoTipoDocumentos = grupoDocumentoTipoDocumentoDAO.list(id, user);

        String documentos_update = "";
        int idTipoDocumento;
        Documento documento;
        for (GrupoDocumentoTipoDocumento grupoDocumentoTipoDocumento: grupoDocumentoTipoDocumentos) {
            idTipoDocumento = grupoDocumentoTipoDocumento.getIdTipoDocumento();
            documentos_update += idTipoDocumento + ",";
            documento = documentoDAO.get(idTipoDocumento, user);
            grupoDocumentoTipoDocumento.setNombreDocumento(documento.getNombre());
        }

        if (documentos_update.length() > 0)
            documentos_update = documentos_update.substring(0, documentos_update.length() - 1);

        model.addAttribute("grupo",grupo);
        model.addAttribute("documentos", documentos);
        model.addAttribute("grupos_documentos", grupoDocumentoTipoDocumentos);
        model.addAttribute("documentos_update",documentos_update);
        user.encryptPassword();
        model.addAttribute(user);

        return "grupos_documentos_configuracion";
    }

    @RequestMapping(path = "/grupo/documentos", method = RequestMethod.POST)
    public String updateDocumentosGrupo(@RequestParam("grupo_id") int id, Model model, @ModelAttribute User user,
                                        @RequestParam("documentos_update") String documentosUpdate,
                                        @RequestParam("documentos_insert") String documentosInsert,
                                        @RequestParam("documentos_delete") String documentosDelete) {

        user.decryptPassword();

        String listaDocumentosUpdate[] = documentosUpdate.split(";");
        for (String docRequerido: listaDocumentosUpdate) {
            if (docRequerido.isEmpty()) break;
            String parameters[] = docRequerido.split(",");
            GrupoDocumentoTipoDocumento documentoRequerido = new GrupoDocumentoTipoDocumento();
            documentoRequerido.setIdGrupoDocumento(id);
            documentoRequerido.setIdTipoDocumento(Integer.valueOf(parameters[0]));
            documentoRequerido.setObligatorio(Byte.valueOf(parameters[1]));
            documentoRequerido.setOrden(Byte.valueOf(parameters[2]));

            grupoDocumentoTipoDocumentoDAO.update(documentoRequerido, user);
        }

        String listaDocumentosInsert[] = documentosInsert.split(";");
        for (String docRequerido: listaDocumentosInsert) {
            if (docRequerido.isEmpty()) break;
            String parameters[] = docRequerido.split(",");
            GrupoDocumentoTipoDocumento documentoRequerido = new GrupoDocumentoTipoDocumento();
            documentoRequerido.setIdGrupoDocumento(id);
            documentoRequerido.setIdTipoDocumento(Integer.valueOf(parameters[0]));
            documentoRequerido.setObligatorio(Byte.valueOf(parameters[1]));
            documentoRequerido.setOrden(Byte.valueOf(parameters[2]));

            grupoDocumentoTipoDocumentoDAO.insert(documentoRequerido, user);
        }

        String listaDocumentosDelete[] = documentosDelete.split(",");
        for (String docRequerido: listaDocumentosDelete) {
            if (docRequerido.isEmpty()) break;

            int idTipoDocumento = Integer.valueOf(docRequerido);
            grupoDocumentoTipoDocumentoDAO.delete(id, idTipoDocumento, user);
        }

        boolean success = true;

        model.addAttribute("success", success);
        user.encryptPassword();
        model.addAttribute(user);

        return "post_grupo_configuracion";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.GET)
    public String showGruposModificaciones(Model model,
                                             @RequestParam(value="username") String username,
                                             @RequestParam(value = "password") String password) {

        User user = new User(username,password);

        model.addAttribute("grupos", grupoDAO.list(user));
        user.encryptPassword();
        model.addAttribute(user);

        return "grupos_modificaciones";
    }

    @RequestMapping(path = "/modificaciones/grupo", method = RequestMethod.GET)
    public String modificar(@RequestParam(value="selectable_grupos", required = true) int id, Model model,
                            @ModelAttribute User user, RedirectAttributes ra) {
        ra.addFlashAttribute("user", user);

        return "redirect:/grupos/modificaciones/grupo/"+Integer.toString(id);
    }

    @RequestMapping(path = "/modificaciones/grupo/{id}", method = RequestMethod.GET)
    public String getTramiteModificacion(@PathVariable("id") int id, Model model) {
        User user = (User) model.asMap().get("user");
        user.decryptPassword();
        Grupo grupo = grupoDAO.get(id,user);

        model.addAttribute("grupo",grupo);
        user.encryptPassword();
        model.addAttribute(user);

        return "grupo_modificar";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.POST)
    public String modify(Model model, @ModelAttribute User user,
                         @RequestParam (value="id", required = true) int id,
                         @RequestParam (value="descripcion", required = true) String descripcion) {

        user.decryptPassword();

        Grupo grupo = new Grupo();
        grupo.setId(id);
        grupo.setDescripcion(descripcion);

        boolean success = true;
        try {
            grupoDAO.update(grupo, user);
        } catch (DataAccessException e) {
            success = false;
            e.printStackTrace();
        }

        model.addAttribute("success", success);
        user.encryptPassword();
        model.addAttribute(user);

        return "post_modificacion";
    }


}
