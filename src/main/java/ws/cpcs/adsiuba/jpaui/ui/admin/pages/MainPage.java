package ws.cpcs.adsiuba.jpaui.ui.admin.pages;

import ws.cpcs.adsiuba.jpaui.ui.admin.BreadCrumb;
import ws.cpcs.adsiuba.jpaui.ui.admin.Page;

/**
 * Main screen (empty so far)
 */
public class MainPage {

    public static Page index() {
        return Page.create(
                MainPage.class,
                new BreadCrumb("/", "Administrative interface"),
                model -> "template");
    }
}
