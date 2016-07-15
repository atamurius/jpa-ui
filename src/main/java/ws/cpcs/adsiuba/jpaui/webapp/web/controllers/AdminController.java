package ws.cpcs.adsiuba.jpaui.webapp.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ws.cpcs.adsiuba.jpaui.ui.admin.UIController;

@Controller
@RequestMapping("/admin")
public class AdminController extends UIController {

    @RequestMapping("/test")
    @ResponseBody String test() {
        return "success";
    }
}
