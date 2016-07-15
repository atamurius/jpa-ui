package ws.cpcs.adsiuba.jpaui.ui.templates;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

import java.io.IOException;

public class Templates {

    private Handlebars handlebars;

    public Templates() {
        this.handlebars = new Handlebars(new ClassPathTemplateLoader() {
            @Override
            public String resolve(String uri) {
                return getPrefix() + uri.replaceAll("\\.","/") + getSuffix();
            }
        });
        handlebars.registerHelpers(CommonHelpers.class);
    }

    public String render(Class base, String name, Object ctx) throws IOException {
        return handlebars.compile(base.getCanonicalName() +"."+ name).apply(ctx);
    }
}
