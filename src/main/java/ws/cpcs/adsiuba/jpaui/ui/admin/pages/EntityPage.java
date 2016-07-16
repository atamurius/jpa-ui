package ws.cpcs.adsiuba.jpaui.ui.admin.pages;

import ws.cpcs.adsiuba.jpaui.model.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.model.WithId;
import ws.cpcs.adsiuba.jpaui.model.WithName;
import ws.cpcs.adsiuba.jpaui.ui.admin.BreadCrumb;
import ws.cpcs.adsiuba.jpaui.ui.admin.Page;

import java.io.Serializable;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EntityPage {
    @SuppressWarnings("unchecked")
    public static Page edit(EntityDescriptor descriptor, Serializable id) {
        Object item = id == null ? null : descriptor.getRepository().findOne(id);
        return Page.create(
                EntityPage.class,
                id == null ?
                    new BreadCrumb("/"+ descriptor.getId() +"/+", "Create "+ descriptor.getName()) :
                    new BreadCrumb("/"+ descriptor.getId() +"/"+ id, titleOf(descriptor, item, id)),
                ListPage.list(descriptor),
                model -> {
                    model.addAttribute("currentEntity", descriptor);
                    model.addAttribute("item", item);
                    model.addAttribute("id", id == null ? "+" : id);
                    return "edit";
                });
    }

    private static String titleOf(EntityDescriptor descriptor, Object item, Object id) {
        String name = null;
        if (item instanceof WithName) {
            name = ((WithName) item).getDisplayName();
        }
        return defaultIfBlank(name, descriptor.getName() +" "+ id);
    }

    @SuppressWarnings("unchecked")
    public static void save(EntityDescriptor<WithId<?>> descriptor, String id, Map<String, String> params) {
        descriptor.update(repo -> "+".equals(id) ? descriptor.createNew() : repo.findOne(id), params);
    }
}
