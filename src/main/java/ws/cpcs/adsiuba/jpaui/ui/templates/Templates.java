package ws.cpcs.adsiuba.jpaui.ui.templates;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class Templates {

    @Autowired
    private List<HelperService> helperServices;

    private Handlebars handlebars;

    public Templates() {
        this.handlebars = new Handlebars(new ClassPathTemplateLoader() {
            @Override
            public String resolve(String uri) {
                return getPrefix() + uri.replaceAll("\\.","/").replaceAll(":",".") +
                        (uri.contains(":") ? "" : getSuffix());
            }
        });
    }

    @PostConstruct
    public void init() {
        helperServices.forEach(handlebars::registerHelpers);
    }

    public String render(Class base, String name, Object ctx) throws IOException {
        return handlebars.compile(base.getCanonicalName() +"."+ name).apply(ctx);
    }
}
