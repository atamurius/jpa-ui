package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ws.cpcs.adsiuba.jpaui.model.descr.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.model.edit.EntityService;
import ws.cpcs.adsiuba.jpaui.ui.UndefinedEntityException;
import ws.cpcs.adsiuba.jpaui.ui.templates.Templates;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.List;

/**
 * Abstract base for controllers
 */
public abstract class UIControllerBase {

    @Autowired
    protected Templates templates;

    @Autowired(required = false)
    private List<EntityDescriptor> descriptors;

    protected String base;

    @Autowired
    private ServletContext context;

    @Autowired
    protected EntityService entityService;

    protected Class templateBase;

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

    protected EntityDescriptor descriptorById(String id) {
        return descriptors.stream()
                .filter(d -> d.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new UndefinedEntityException(id));
    }

    protected ResponseEntity<String> render(String template, Model model) throws IOException {
        return ResponseEntity.ok(templates.render(templateBase, template, model.asMap()));
    }
}
