package ar.gob.modernizacion.tad.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by MMargonari on 25/05/2017.
 */
@Controller
@RequestMapping("etiquetas")
public class EtiquetasController {

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String getNewForm(Model model) {

        return "new";

    }
}
