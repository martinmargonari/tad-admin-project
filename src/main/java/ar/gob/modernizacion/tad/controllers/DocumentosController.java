package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.DocumentoManager;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.User;
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

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String getNewForm(Model model,
                             @RequestParam(value="username") String username,
                             @RequestParam(value = "password") String password) {
        User user = null;
        try {
            user = new User(username,Encrypter.decrypt(password));
            DocumentoManager.loadDocumentos(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("acronimos_tads", Application.acronimosTads);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "documento_nuevo";
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
            @RequestParam (value="usuario_creacion", required = true) String usuario_creacion, Model model) {

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

        Documento documento = new Documento(0,acronimo_gedo,acronimo_tad,nombre,descripcion,embebido,firmaConToken,esFirmaConjunta,usuario_creacion);

        user.setPassword(Encrypter.decrypt(user.getPassword()));
        boolean success = true;
        try {
            DocumentoManager.insertDocumento(documento, user);
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }

        model.addAttribute("success", success);
        model.addAttribute("id", String.valueOf(documento.getId()));
        System.out.println("Nuevo ID: " + String.valueOf(documento.getId()));
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "post_documento_nuevo";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.GET)
    public String showDocumentosModificaciones(Model model,
                                               @RequestParam(value="username") String username,
                                               @RequestParam(value = "password") String password) {
        User user = null;
        try {
            user = new User(username, Encrypter.decrypt(password));
            DocumentoManager.loadDocumentos(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("documentos", Application.documentos);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "documentos_modificaciones";
    }

    @RequestMapping(path = "/modificaciones/documento", method = RequestMethod.GET)
    public String modificar(@RequestParam(value="selectable_documentos", required = true) int id, Model model,
                            @ModelAttribute User user, RedirectAttributes ra) {
        ra.addFlashAttribute(user);

        return "redirect:/documentos/modificaciones/documento/"+Integer.toString(id);
    }

    @RequestMapping(path = "/modificaciones/documento/{id}", method = RequestMethod.GET)
    public String getDocumentoModificacion(@PathVariable("id") int id, Model model, @ModelAttribute User user) {

        Documento documento = Application.documentos.get(id);
        model.addAttribute("documento",documento);
        model.addAttribute("acronimos_tads", Application.acronimosTads);
        model.addAttribute(user);

        return "documento_modificar";
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
                         @RequestParam (value="es_firma_conjunta", required = true) String es_firma_conjunta,
                         @RequestParam (value="usuario_modificacion", required = true) String usuario_modificacion) {

        Documento documento = Application.documentos.get(id);
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
        user.setPassword(Encrypter.decrypt(user.getPassword()));
        try {
            DocumentoManager.updateDocumento(documento, usuario_modificacion, user);
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }

        model.addAttribute("success", success);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "post_modificacion";
    }

}
