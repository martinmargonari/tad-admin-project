package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.DocumentoManager;
import ar.gob.modernizacion.tad.model.Documento;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;

/**
 * Created by MMargonari on 26/05/2017.
 */

@Controller
@RequestMapping("/documentos")
public class DocumentosController {

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String getNewForm(Model model) {
        model.addAttribute("acronimos_tads", Application.acronimosTads);

        return "documento_nuevo";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(
            @RequestParam(value="acronimo_gedo", required=true) String acronimo_gedo,
            @RequestParam (value="acronimo_tad", required=true) String acronimo_tad,
            @RequestParam (value="nombre", required = true) String nombre,
            @RequestParam (value="descripcion", required = true) String descripcion,
            @RequestParam (value="es_embebido", required = true) String es_embebido,
            @RequestParam (value="usuario_creacion", required = true) String usuario_creacion) {

        byte embebido = 0;
        if (es_embebido.contentEquals("SI")) {
            embebido = 1;
        }

        Documento documento = new Documento(0,acronimo_gedo,acronimo_tad,nombre,descripcion,embebido,usuario_creacion);

        try {
            DocumentoManager.insertDocumento(documento);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/home";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.GET)
    public String showDocumentosModificaciones(Model model) {
        model.addAttribute("documentos", Application.documentos);

        return "documentos_modificaciones";
    }

    @RequestMapping(path = "/modificaciones/documento", method = RequestMethod.GET)
    public String modificar(@RequestParam(value="selectable_documentos", required = true) int id, Model model) {

        return "redirect:/documentos/modificaciones/documento/"+Integer.toString(id);
    }

    @RequestMapping(path = "/modificaciones/documento/{id}", method = RequestMethod.GET)
    public String getDocumentoModificacion(@PathVariable("id") int id, Model model) {

        Documento documento = Application.documentos.get(id);
        model.addAttribute("documento",documento);
        model.addAttribute("acronimos_tads", Application.acronimosTads);

        return "documento_modificar";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.POST)
    public String modify(Model model,
                         @RequestParam (value="id", required = true) int id,
                         @RequestParam(value="acronimo_gedo", required=true) String acronimo_gedo,
                         @RequestParam (value="acronimo_tad", required=true) String acronimo_tad,
                         @RequestParam (value="nombre", required = true) String nombre,
                         @RequestParam (value="descripcion", required = true) String descripcion,
                         @RequestParam (value="es_embebido", required = true) String es_embebido,
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

        try {
            DocumentoManager.updateDocumento(documento, usuario_modificacion);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/home";
    }

}
