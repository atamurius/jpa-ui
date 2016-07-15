package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.ui.Model;

/**
 * Main screen (empty so far)
 */
public class MainScreen extends Screen {

    protected BreadCrumb getBreadCrumb(String base) {
        return new BreadCrumb(base +"/", "Administrative interface");
    }

    protected void perform(Model model) {

    }
}
