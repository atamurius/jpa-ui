package ws.cpcs.adsiuba.jpaui.ui.templates;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.cpcs.adsiuba.jpaui.model.UIEntity;
import ws.cpcs.adsiuba.jpaui.model.descr.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;
import ws.cpcs.adsiuba.jpaui.model.edit.EntityService;
import ws.cpcs.adsiuba.jpaui.model.edit.PropertyEditor;
import ws.cpcs.adsiuba.jpaui.model.edit.PropertyEditorService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class PropertyEditorHelper implements HelperService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private PropertyEditorService editors;

    @Autowired
    private Templates templates;

    public String property(String name, Options opts) {
        UIEntity<?> entity = findEntity(opts);
        if (entity == null) return null;
        EntityDescriptor<?> descriptor = getEntityDescriptor(opts);
        return editors.read(descriptor.getProperty(name), entity);
    }

    private EntityDescriptor<?> getEntityDescriptor(Options opts) {
        EntityDescriptor<?> descr = opts.hash("type", opts.get("currentEntity"));
        if (descr == null) {
            throw new IllegalStateException("Cannot find current EntityDescriptor, set 'type' param");
        } else {
            return descr;
        }
    }

    private UIEntity findEntity(Options opts) {
        UIEntity item = null;
        if (opts.hash.containsKey("object")) {
            return opts.hash("object");
        } else {
            Context ctx = opts.context;
            while (item == null && ctx != null) {
                if (ctx.model() instanceof UIEntity) {
                    item = (UIEntity) ctx.model();
                }
                ctx = ctx.parent();
            }
            if (item == null) {
                throw new IllegalStateException("Cannot find entity in contexts, please use 'object' param");
            }
            return item;
        }
    }

    public String showProperty(String name, Options opts) throws IOException {
        return renderProperty("show", name, opts);
    }

    public String editProperty(String name, Options opts) throws IOException {
        return renderProperty("edit", name, opts);
    }

    private String renderProperty(String action, String name, Options opts) throws IOException {
        UIEntity entity = findEntity(opts);
        EntityDescriptor<?> descriptor = getEntityDescriptor(opts);
        Property property = descriptor.getProperty(name);
        PropertyEditor editor = editors.editorFor(property);
        Map<String,Object> params = new HashMap<>();
        Object value = entity == null ? null : property.read(entity);
        String template = editor.render(action, property, value, params);
        return templates.render(editor.getTemplateBase(), template, Context.newContext(opts.context, params));
    }

    public String describeEntity(UIEntity<?> entity, Options opts) {
        if (entity == null) {
            return null;
        } else {
            return entityService.getDescriptorOf(entity).getDescription((UIEntity) entity);
        }
    }
}
