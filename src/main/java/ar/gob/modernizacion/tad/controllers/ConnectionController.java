package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.*;
import ar.gob.modernizacion.tad.model.User;
import ar.gob.modernizacion.tad.model.Encrypter;
import ar.gob.modernizacion.tad.model.constants.Messages;
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

    @RequestMapping(path ="/login", method = RequestMethod.GET)
    public String login(Model model,
                        @RequestParam(value="error", required = false, defaultValue = "false") boolean error) {

        System.out.println(error);
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

        boolean isConnected = false;
        if (! isConnected) {
            model.addAttribute("error",true);
            return "login";
        }
        /*
        String keys[] = new String [2];
        keys[0] = salt;
        keys[1] = iv;
        userKeys.put(username, keys);
*/
        User user = new User(username,decryptedPassword);
        try {
            user.setPassword(Encrypter.encrypt(user.getPassword(),salt,iv));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ra.addFlashAttribute("user", user);

        return "redirect:/home";
    }

    /*
    @RequestMapping(path = "/connect", method = RequestMethod.POST)
    public String getNewForm(Model model, RedirectAttributes ra,
                             @RequestParam(value="usuario", required = true) String usuario,
                             @RequestParam (value="password", required = true) String password) {

        User user = new User(usuario,password);

        try {
            ConnectionManager.connect(user);
        } catch (SQLException e) {
            model.addAttribute("error", "1");
            return "login";
        }

        user.setPassword(Encrypter.encrypt(user.getPassword()));

        ra.addFlashAttribute("user",user);

        return "redirect:/home";
    }*/

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public String getNewForm(@ModelAttribute User user, Model model) {
        model.addAttribute("title","Trámites a Distancia 2 (TAD 2)");

        System.out.println("PASS HOME: " + user.getPassword());

        Map<String,String> map = new HashMap<String,String>();
        map.put("username",user.getUsername());
        map.put("password",user.getPassword());

        model.addAttribute(user);

        return "home";
    }


}
