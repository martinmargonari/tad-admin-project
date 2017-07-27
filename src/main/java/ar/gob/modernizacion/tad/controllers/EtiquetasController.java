package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.dao.EtiquetaDAO;
import ar.gob.modernizacion.tad.model.Tag;
import ar.gob.modernizacion.tad.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by MMargonari on 25/05/2017.
 */
@Controller
@RequestMapping("etiquetas")
public class EtiquetasController {

    @Autowired
    private EtiquetaDAO etiquetaDAO;

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String getNewForm(Model model,
                             @RequestParam(value="username") String username,
                             @RequestParam(value = "password") String password) {

        User user = new User(username, password);



        String etiquetasExistentes = "";
        HashMap<String, List<Tag>> etiquetasSelected = etiquetaDAO.list(user);
        Iterator it = etiquetasSelected.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            for(Tag etiqueta: (ArrayList<Tag>)pair.getValue()) {
                etiquetasExistentes += etiqueta.getTag() + ",";
            }
        }

        model.addAttribute("tags", etiquetasSelected);
        model.addAttribute("etiquetas_existentes",etiquetasExistentes);
        user.encryptPassword();
        model.addAttribute(user);

        return "etiqueta_nuevo";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute User user,
                      @RequestParam(value="etiqueta", required=true) String etiqueta,
                      @RequestParam(value="selected_categoria", required=true) String categoria) {

        Tag tag = new Tag(etiqueta,categoria);
        boolean success = true;
        user.decryptPassword();
        try {
            etiquetaDAO.insert(tag, user);
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }

        model.addAttribute("success", success);
        user.encryptPassword();
        model.addAttribute(user);

        return "post_etiqueta_nuevo";
    }

}
