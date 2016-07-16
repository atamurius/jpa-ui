package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ws.cpcs.adsiuba.jpaui.model.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.ui.UndefinedEntityException;
import ws.cpcs.adsiuba.jpaui.ui.admin.pages.EntityPage;
import ws.cpcs.adsiuba.jpaui.ui.admin.pages.ListPage;
import ws.cpcs.adsiuba.jpaui.ui.admin.pages.MainPage;
import ws.cpcs.adsiuba.jpaui.ui.templates.Templates;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.IOException;

import java.util.*;

/**
 * Abstract base for controllers
 */
public abstract class UIController {

    @Autowired
    Templates templates;

    @Autowired(required = false)
    private List<EntityDescriptor> descriptors;

    private String base;

    private BreadCrumb defaultPage;

    @Autowired
    private ServletContext context;

    @Autowired
    private BreadCrumbs breadCrumbs;

    @PostConstruct
    public void init() {
        RequestMapping requestMapping = getClass().getAnnotation(RequestMapping.class);
        base = requestMapping == null ? ""
                : requestMapping.path().length > 0 ? requestMapping.path()[0]
                : requestMapping.value()[0];
        defaultPage = MainPage.index().getBreadCrumb().withBase(base);
    }

    public BreadCrumb getDefaultPage() {
        return defaultPage;
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

    protected ResponseEntity<String> render(Model model, Page page) throws IOException {
        breadCrumbs.navigate(page);
        model.addAttribute("path", breadCrumbs.getBreadCrumbs());
        return ResponseEntity.ok(page.render(templates, model));
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> index(Model model) throws IOException {
        return render(model, MainPage.index());
    }

    @RequestMapping(value = "/{entity}", method = RequestMethod.GET)
    public ResponseEntity<String> list(
            @PathVariable String entity,
            Model model,
            @PageableDefault(20) Pageable page)
            throws IOException {
        return render(model, ListPage.list(entityDescriptorById(entity)));
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"/{entity}/{id:.+}/edit","/{entity}/{id:\\+}"}, method = RequestMethod.GET)
    public ResponseEntity<String> edit(
            @PathVariable String entity,
            @PathVariable String id, Model model)
            throws IOException {
        return render(model, EntityPage.edit(entityDescriptorById(entity), "+".equals(id) ? null : id));
    }
}
