package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.dao.DocumentoDAO;
import ar.gob.modernizacion.tad.managers.DocumentoManager;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

/**
 * Created by MMargonari on 26/05/2017.
 */

@Controller
@RequestMapping("/documentos")
public class DocumentosController {

    @Autowired
    private DocumentoDAO documentoDAO;

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String getNewForm(Model model,
                             @RequestParam(value="username") String username,
                             @RequestParam(value = "password") String password) {

        User user = new User(username,password);

        model.addAttribute("acronimos_tads", documentoDAO.getAcronimosTad(user));
        user.encryptPassword();
        model.addAttribute(user);

        return "documento/documento_nuevo";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(@ModelAttribute User user,
            @RequestParam(value="acronimo_gedo", required=true) String acronimo_gedo,
            @RequestParam (value="acronimo_tad", required=true) String acronimo_tad,
            @RequestParam (value="nombre", required = true) String nombre,
            @RequestParam (value="descripcion", required = true) String descripcion,
            @RequestParam (value="es_embebido", required = true) String es_embebido,
            @RequestParam (value="firma_con_token", required = true) String firma_con_token,
            @RequestParam (value="es_firma_conjunta", required = true) String es_firma_conjunta,
            Model model) {

        byte embebido = 0;
        if (es_embebido.contentEquals("SI")) {
            embebido = 1;
        }

        byte firmaConToken = 0;
        if (firma_con_token.contentEquals("SI")) {
            firmaConToken = 1;
        }

        byte esFirmaConjunta = 0;
        if (es_firma_conjunta.contentEquals("SI")) {
            esFirmaConjunta = 1;
        }

        user.decryptPassword();

        Documento documento = new Documento(0,acronimo_gedo,acronimo_tad,nombre,descripcion,embebido,firmaConToken,esFirmaConjunta,user.getUsername());

        boolean success = true;
        try {
            documentoDAO.insert(documento,user);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        model.addAttribute("success", success);
        model.addAttribute("id", String.valueOf(documento.getId()));
        user.encryptPassword();
        model.addAttribute(user);

        return "post_documento_nuevo";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.GET)
    public String showDocumentosModificaciones(Model model,
                                               @RequestParam(value="username") String username,
                                               @RequestParam(value = "password") String password) {

        User user = new User(username,password);

        model.addAttribute("documentos", documentoDAO.list(user));
        user.encryptPassword();
        model.addAttribute(user);

        return "documento/documentos_modificaciones";
    }

    @RequestMapping(path = "/modificaciones/documento", method = RequestMethod.GET)
    public String modificar(@RequestParam(value="selectable_documentos", required = true) int id, Model model,
                            @ModelAttribute User user, RedirectAttributes ra) {
        ra.addFlashAttribute(user);

        return "redirect:/documentos/modificaciones/documento/"+Integer.toString(id);
    }

    @RequestMapping(path = "/modificaciones/documento/{id}", method = RequestMethod.GET)
    public String getDocumentoModificacion(@PathVariable("id") int id, Model model) {

        User user = (User) model.asMap().get("user");
        user.decryptPassword();

        Documento documento = documentoDAO.get(id, user);
        model.addAttribute("documento",documento);
        model.addAttribute("acronimos_tads", documentoDAO.getAcronimosTad(user));
        user.encryptPassword();
        model.addAttribute(user);

        return "documento/documento_modificar";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.POST)
    public String modify(Model model, @ModelAttribute User user,
                         @RequestParam (value="id", required = true) int id,
                         @RequestParam(value="acronimo_gedo", required=true) String acronimo_gedo,
                         @RequestParam (value="acronimo_tad", required=true) String acronimo_tad,
                         @RequestParam (value="nombre", required = true) String nombre,
                         @RequestParam (value="descripcion", required = true) String descripcion,
                         @RequestParam (value="es_embebido", required = true) String es_embebido,
                         @RequestParam (value="firma_con_token", required = true) String firma_con_token,
                         @RequestParam (value="es_firma_conjunta", required = true) String es_firma_conjunta) {

        user.decryptPassword();

        Documento documento = new Documento();
        documento.setId(id);
        documento.setAcronimoGedo(acronimo_gedo);
        documento.setAcronimoTad(acronimo_tad);
        documento.setNombre(nombre);
        documento.setDescripcion(descripcion);

        byte embebido = 0;
        if (es_embebido.contentEquals("SI")) {
            embebido = 1;
        }
        documento.setEsEmbebido(embebido);

        byte firmaConToken = 0;
        if (firma_con_token.contentEquals("SI")) {
            firmaConToken = 1;
        }
        documento.setFirmaConToken(firmaConToken);

        byte esFirmaConjunta = 0;
        if (es_firma_conjunta.contentEquals("SI")) {
            esFirmaConjunta = 1;
        }
        documento.setEsFirmaConjunta(esFirmaConjunta);

        boolean success = true;
        try {
            documentoDAO.update(documento, user);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        model.addAttribute("success", success);
        user.encryptPassword();
        model.addAttribute(user);

        return "post_modificacion";
    }

}
