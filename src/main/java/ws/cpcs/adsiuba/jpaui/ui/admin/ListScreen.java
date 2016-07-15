package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.ui.Model;
import ws.cpcs.adsiuba.jpaui.model.EntityDescriptor;

public class ListScreen extends Screen {

    private EntityDescriptor descriptor;

    public ListScreen(EntityDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    protected BreadCrumb getBreadCrumb(String base) {
        return new BreadCrumb(base +"/"+ descriptor.getId(), descriptor.getPluralName());
    }

    @Override
    protected Screen getParent() {
        return new MainScreen();
    }

    @Override
    protected void perform(Model model) {
        model.addAttribute("currentEntity", descriptor);
        model.addAttribute("items", descriptor.getRepository().findAll());
    }
}
