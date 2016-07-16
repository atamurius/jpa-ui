package ws.cpcs.adsiuba.jpaui.ui.admin.pages;

import ws.cpcs.adsiuba.jpaui.model.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.ui.admin.BreadCrumb;
import ws.cpcs.adsiuba.jpaui.ui.admin.Page;

public class ListPage {

    public static Page list(EntityDescriptor descriptor) {
        return Page.create(
                ListPage.class,
                new BreadCrumb("/"+ descriptor.getId(), descriptor.getPluralName()),
                MainPage.index(),
                model -> {
                    model.addAttribute("currentEntity", descriptor);
                    model.addAttribute("properties", descriptor.getProperties("list"));
                    model.addAttribute("items", descriptor.getRepository().findAll());
                    return "template";
                });
    }
}
