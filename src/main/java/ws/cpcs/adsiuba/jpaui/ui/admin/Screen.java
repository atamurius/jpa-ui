package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.ui.Model;
import ws.cpcs.adsiuba.jpaui.ui.templates.Templates;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Admin screen
 */
public abstract class Screen {

    protected String template = "template";

    protected Screen getParent() {
        return null;
    }

    protected abstract BreadCrumb getBreadCrumb(String base);

    protected abstract void perform(Model model);

    public String render(UIController uiController, Templates templates, Model model) throws IOException {
        perform(model);
        List<BreadCrumb> bcs = new LinkedList<>();
        Screen screen = this;
        String base = uiController.getBase();
        while (screen != null) {
            bcs.add(0, screen.getBreadCrumb(base));
            screen = screen.getParent();
        }
        model.addAttribute("path", bcs);
        return templates.render(getClass(), template, model.asMap());
    }
}
