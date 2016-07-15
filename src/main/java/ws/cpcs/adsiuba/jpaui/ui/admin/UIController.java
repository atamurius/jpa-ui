package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ws.cpcs.adsiuba.jpaui.model.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.ui.UndefinedEntityException;
import ws.cpcs.adsiuba.jpaui.ui.templates.Templates;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.IOException;
import static java.util.Arrays.*;

import java.util.*;

import static java.util.Collections.singletonList;

/**
 * Abstract base for controllers
 */
public abstract class UIController {

    @Autowired
    Templates templates;

    @Autowired(required = false)
    private List<EntityDescriptor> descriptors;

    private String base;

    @Autowired
    private ServletContext context;

    @PostConstruct
    public void init() {
        RequestMapping requestMapping = getClass().getAnnotation(RequestMapping.class);
        base = requestMapping == null ? ""
                : requestMapping.path().length > 0 ? requestMapping.path()[0]
                : requestMapping.value()[0];
    }

    @ModelAttribute("entities")
    public List<EntityDescriptor> getDescriptors() {
        return descriptors;
    }

    @ModelAttribute("base")
    public String getBase() {
        return context.getContextPath() + base;
    }

    protected EntityDescriptor entityDescriptorById(String id) {
        return descriptors.stream()
                .filter(d -> d.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new UndefinedEntityException(id));
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String index(Model model) throws IOException {
        return new MainScreen().render(this, templates, model);
    }

    @RequestMapping(value = "/{entity}", method = RequestMethod.GET)
    @ResponseBody
    public String list(@PathVariable String entity, Model model, @PageableDefault(20) Pageable page) throws IOException {
        return new ListScreen(entityDescriptorById(entity)).render(this, templates, model);
    }
//
//    @SuppressWarnings("unchecked")
//    @RequestMapping(value = "/{entity}/{id:.*}", method = RequestMethod.GET)
//    @ResponseBody
//    public String edit(@PathVariable String entity, @PathVariable String id, Model model) throws IOException {
//        EntityDescriptor descr = entityDescriptorById(entity)
//                .orElseThrow(() -> new UndefinedEntityException(entity));
//        if (! "+".equals(id)) {
//            model.addAttribute("item", descr.getRepository().findOne(id));
//        }
//        model.addAttribute("nav-item", true);
//        model.addAttribute("currentEntity", descr);
//        return template("item").apply(model);
//    }
}
