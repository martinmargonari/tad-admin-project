package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.EtiquetaManager;
import ar.gob.modernizacion.tad.model.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by MMargonari on 25/05/2017.
 */
@Controller
@RequestMapping("etiquetas")
public class EtiquetasController {

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String getNewForm(Model model) {

        String etiquetasExistentes="";
        Iterator it = Application.etiquetas.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            for(Tag etiqueta: (ArrayList<Tag>)pair.getValue()) {
                etiquetasExistentes += etiqueta.getTag() + ",";
            }
        }

        model.addAttribute("tags", Application.etiquetas);
        model.addAttribute("etiquetas_existentes",etiquetasExistentes);

        return "etiqueta_nuevo";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(Model model,
                      @RequestParam(value="etiqueta", required=true) String etiqueta,
                      @RequestParam(value="selected_category", required=true) String categoria) {

        Tag tag = new Tag(etiqueta,categoria);
        boolean success = true;
        try {
            EtiquetaManager.insertEtiqueta(tag);
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        model.addAttribute("success", success);
        System.out.println("Nueva etieuta: " + tag.getTag());

        return "post_etiqueta_nuevo";
    }

}
