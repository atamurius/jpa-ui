package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ws.cpcs.adsiuba.jpaui.model.UIEntity;
import ws.cpcs.adsiuba.jpaui.model.descr.EntityDescriptor;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        model.addAttribute("items", entityService.find(descriptor, page));
        return render("list", model);
    }

    @RequestMapping(value = "/{entity}.json", method = RequestMethod.GET)
    @ResponseBody
    public Page listJson(Model model,
                         @PathVariable String entity,
                         @PageableDefault(20) Pageable page)
            throws IOException {

        EntityDescriptor descriptor = descriptorById(entity);
        return entityService.find(descriptor, page);
    }

    @RequestMapping(value = "/{entity}.meta", method = RequestMethod.GET)
    @ResponseBody
    public EntityDescriptor entityMetaJson(Model model,
                                           @PathVariable String entity)
            throws IOException {

        return descriptorById(entity);
    }

    @SuppressWarnings("unchecked")
    protected BreadCrumb editPage(EntityDescriptor descriptor, UIEntity item) {
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
        UIEntity item = "+".equals(id) ? null : entityService.findById(descriptor, id);
        model.addAttribute("path", asList(indexPage, listPage(descriptor), editPage(descriptor, item)));
        model.addAttribute("currentEntity", descriptor);
        model.addAttribute("item", item);
        model.addAttribute("id", "+".equals(id) ? "" : id);
        return render("edit", model);
    }

    @RequestMapping(value = {"/{entity}/{id}","/{entity}/","/{entity}"}, method = RequestMethod.POST)
    public ResponseEntity save(Model model,
                       @PathVariable String entity,
                       @PathVariable Optional<String> id,
                       @RequestParam Map<String,String> params)
            throws Exception {

        try {
            EntityDescriptor descriptor = descriptorById(entity);
            UIEntity result = entityService.process(repo -> {
                UIEntity item = "+".equals(id.orElse("+")) ? repo.create(descriptor)
                        : repo.findById(descriptor, id.get());
                return repo.fillValues(item, params);
            });
            model.asMap().clear();
            return ResponseEntity.status("+".equals(id.orElse("+")) ? HttpStatus.CREATED : HttpStatus.OK)
                    .header("Location", base + "/" + entity +"/"+ result.getId())
                    .body(null);
        } catch (Exception e) {
            return unwind(e, ConstraintViolationException.class)
                    .map(c -> ResponseEntity.badRequest()
                        .body(c.getConstraintViolations().stream().map(ValidationError::new)
                            .collect(Collectors.toList())))
                    .orElseThrow(() -> e);
        }
    }

    private <T extends Throwable> Optional<T> unwind(Throwable t, Class<T> expected) {
        while (t != null) {
            if (expected.isAssignableFrom(t.getClass()))
                return Optional.of((T) t);
            t = t.getCause();
        }
        return Optional.empty();
    }

    @RequestMapping(value = "/{entity}/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteRest(@PathVariable String entity, @PathVariable String id) {
        entityService.deleteById(descriptorById(entity), id);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/{entity}/{id}/delete", method = RequestMethod.GET)
    public String delete(Model model, @PathVariable String entity, @PathVariable String id) {
        entityService.deleteById(descriptorById(entity), id);
        model.asMap().clear();
        return "redirect:"+ base +"/"+ entity;
    }
}
