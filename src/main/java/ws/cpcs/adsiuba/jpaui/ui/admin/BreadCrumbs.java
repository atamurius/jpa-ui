package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

public class BreadCrumbs {

    private List<BreadCrumb> breadCrumbs = new LinkedList<>();

    @Autowired
    private UIController controller;

    public void navigate(Page page) {
        BreadCrumb breadCrumb = page.getBreadCrumb().withBase(controller.getBase());
        int idx = breadCrumbs.indexOf(breadCrumb);
        if (idx == -1) {
            breadCrumbs = new LinkedList<>();
            addBreadCrumb(page, breadCrumb);
            BreadCrumb defaultBC = controller.getDefaultPage();
            if (! breadCrumbs.contains(defaultBC))
                breadCrumbs.add(0, defaultBC);
        } else {
            breadCrumbs = new LinkedList<>(breadCrumbs);
            while (breadCrumbs.size() > idx) {
                breadCrumbs.remove(breadCrumbs.size() - 1);
            }
            breadCrumbs.add(breadCrumb);
        }
    }

    private void addBreadCrumb(Page page, BreadCrumb breadCrumb) {
        Page parent = page.getDefaultParent();
        if (parent != null) {
            addBreadCrumb(parent, parent.getBreadCrumb().withBase(controller.getBase()));
        }
        breadCrumbs.add(breadCrumb);
    }

    public List<BreadCrumb> getBreadCrumbs() {
        return breadCrumbs;
    }
}
