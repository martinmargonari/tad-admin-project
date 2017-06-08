package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.*;
import ar.gob.modernizacion.tad.model.constants.Messages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;

/**
 * Created by MMargonari on 07/06/2017.
 */

@Controller
public class ConnectionController {

    @RequestMapping(path ="/login", method = RequestMethod.GET)
    public String login(Model model,
                        @RequestParam(value="error", required = false, defaultValue = "0") String error) {

        System.out.println(error);
        model.addAttribute("error",error);

        return "login";
    }

    @RequestMapping(path = "/connect", method = RequestMethod.POST)
    public String getNewForm(Model model,
                             @RequestParam(value="usuario", required = true) String usuario,
                             @RequestParam (value="password", required = true) String password) {

        ConnectionManager.USER = usuario;
        ConnectionManager.PASSWORD = password;

        try {
            ConnectionManager.connect();
        } catch (SQLException e) {
            model.addAttribute("error", "1");
            return "login";

        }

        try {
            TramiteManager.loadTramites();
            DocumentoManager.loadDocumentos();
            EtiquetaManager.loadEtiquetas();
            GruposManager.loadGrupos();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/home";
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public String getNewForm(Model model) {
        return "home";
    }


}
