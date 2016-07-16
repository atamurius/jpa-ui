package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.ui.Model;
import ws.cpcs.adsiuba.jpaui.ui.templates.Templates;

import java.io.IOException;
import java.util.function.Function;

/**
 * Admin screen
 */
public interface Page {

    BreadCrumb getBreadCrumb();

    Page getDefaultParent();

    String render(Templates ts, Model model) throws IOException;

    static Page create(Class base, BreadCrumb crumb, Page parent, Function<Model,String> render) {
        return new Page() {
            public BreadCrumb getBreadCrumb() {
                return crumb;
            }
            public Page getDefaultParent() {
                return parent;
            }
            public String render(Templates ts, Model model) throws IOException {
                return ts.render(base, render.apply(model), model.asMap());
            }
        };
    }

    static Page create(Class base, BreadCrumb crumb, Function<Model,String> render) {
        return create(base, crumb, null, render);
    }
}
