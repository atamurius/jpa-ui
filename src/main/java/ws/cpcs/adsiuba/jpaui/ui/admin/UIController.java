package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ws.cpcs.adsiuba.jpaui.model.Ident;
import ws.cpcs.adsiuba.jpaui.model.descr.EntityDescriptor;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Abstract base for controllers
 */
public abstract class UIController extends UIControllerBase {

    @Override
    @PostConstruct
    public void init() {
        super.init();
        templateBase = UIController.class;
    }

    protected BreadCrumb indexPage = new BreadCrumb("/", "Administrative interface");

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> index(Model model)
            throws IOException {

        model.addAttribute("path", singletonList(indexPage));
        return render("index", model);
    }

    protected BreadCrumb listPage(EntityDescriptor descriptor) {
        return new BreadCrumb("/"+ descriptor.getId(), descriptor.getPluralName());
    }

    @RequestMapping(value = "/{entity}", method = RequestMethod.GET)
    public ResponseEntity<String> list(Model model,
            @PathVariable String entity,
            @PageableDefault(20) Pageable page)
            throws IOException {

        EntityDescriptor<?> descriptor = descriptorById(entity);
        model.addAttribute("path", asList(indexPage, listPage(descriptor)));
        model.addAttribute("currentEntity", descriptor);
        model.addAttribute("properties", descriptor.getProperties("list"));
        model.addAttribute("items",
                entityService.find(descriptor, page).map(e ->
                        entityService.readProperties(descriptor, e, "list")));
        return render("list", model);
    }

    @SuppressWarnings("unchecked")
    protected BreadCrumb editPage(EntityDescriptor descriptor, Ident item) {
        return new BreadCrumb("/"+ descriptor.getId() +"/"+ (item == null ? "+" : item.getId()),
                item == null ? "New "+ descriptor.getName()
                    : descriptor.getDescription(item));
    }

    @RequestMapping(value = "/{entity}/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> edit(Model model,
            @PathVariable String entity,
            @PathVariable String id)
            throws IOException {

        EntityDescriptor descriptor = descriptorById(entity);
        Ident item = "+".equals(id) ? null : entityService.findById(descriptor, id);
        model.addAttribute("path", asList(indexPage, listPage(descriptor), editPage(descriptor, item)));
        model.addAttribute("currentEntity", descriptor);
        model.addAttribute("currentEntity", descriptor);
        model.addAttribute("item", item == null ? "" : entityService.readProperties(descriptor, item, "default"));
        model.addAttribute("id", id);
        return render("edit", model);
    }

    @RequestMapping(value = "/{entity}/{id}", method = RequestMethod.POST)
    public String save(Model model,
            @PathVariable String entity,
            @PathVariable String id,
            @RequestParam Map<String,String> params)
            throws IOException {

        EntityDescriptor descriptor = descriptorById(entity);
        entityService.process(repo -> {
            Ident item = "+".equals(id) ? repo.create(descriptor)
                    : repo.findById(descriptor, id);
            return repo.fillValues(descriptor, item, params);
        });
        model.asMap().clear();
        return "redirect:"+ base +"/"+ entity;
    }

}
