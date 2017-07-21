package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.dao.UserDAO;
import ar.gob.modernizacion.tad.managers.*;
import ar.gob.modernizacion.tad.model.KeyManager;
import ar.gob.modernizacion.tad.model.User;
import ar.gob.modernizacion.tad.model.Encrypter;
import ar.gob.modernizacion.tad.model.constants.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MMargonari on 07/06/2017.
 */

@Controller
public class ConnectionController {

    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String index(Model model) {
        model.addAttribute(new User());
        return "index";
    }

    @RequestMapping(path ="/login", method = RequestMethod.GET)
    public String login(Model model,
                        @RequestParam(value="error", required = false, defaultValue = "false") boolean error) {

        model.addAttribute(new User());
        model.addAttribute("error",error);
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String validateUser( @RequestParam(value="username", required = true) String username,
                                @RequestParam (value="encryptedPassword", required = true) String password,
                                @RequestParam (value="salt", required = true) String salt,
                                @RequestParam (value="iv", required = true) String iv,
                                RedirectAttributes ra, Model model) {

        String decryptedPassword = null;
        try {
            decryptedPassword = Encrypter.decrypt(password, salt, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = new User(username,decryptedPassword,salt,iv);

        boolean isConnected = userDAO.testConnection(user);

        if (! isConnected) {
            model.addAttribute("error",true);
            return "login";
        }

        KeyManager.putKeys(user);
        user.encryptPassword();
        ra.addFlashAttribute("user", user);
        return "redirect:/home";
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public String getNewForm(@ModelAttribute User user, Model model) {
        model.addAttribute("title","Trámites a Distancia 2 (TAD 2)");
        model.addAttribute(user);
        return "home";
    }


}
