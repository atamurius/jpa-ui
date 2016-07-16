package ws.cpcs.adsiuba.jpaui.ui.admin.pages;

import ws.cpcs.adsiuba.jpaui.model.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.model.WithName;
import ws.cpcs.adsiuba.jpaui.ui.admin.BreadCrumb;
import ws.cpcs.adsiuba.jpaui.ui.admin.Page;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

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
}
