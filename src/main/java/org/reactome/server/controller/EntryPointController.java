package org.reactome.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class EntryPointController {

    @RequestMapping(value = {"/", "/index.html"}, method = RequestMethod.GET)
    public RedirectView redirectView() {
        return new RedirectView("/swagger-ui.html");
    }

}
