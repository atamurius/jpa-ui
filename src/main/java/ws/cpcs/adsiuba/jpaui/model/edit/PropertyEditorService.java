package ws.cpcs.adsiuba.jpaui.model.edit;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.cpcs.adsiuba.jpaui.model.UIEntity;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;

import java.util.List;
import java.util.function.Function;

/**
 * PropertyEditor registry
 */
@Service
public class PropertyEditorService {

    @Autowired
    private List<PropertyEditor<?>> editors;

    @SuppressWarnings("unchecked")
    public <T> PropertyEditor<T> editorFor(Property<T> property) {
        return (PropertyEditor) editors.stream()
                .filter(e -> e.supports(property))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No editor found for "+ property));
    }

    public <T> T convertTo(Property<T> prop, String value) {
        return editorFor(prop).convertTo(prop, value);
    }

    public void write(Property prop, UIEntity entity, String value) {
        prop.write(entity, editorFor(prop).convertTo(prop, value));
    }

    public <T> String convertFrom(Property<T> prop, T value) {
        return editorFor(prop).convertFrom(prop, value);
    }

    public String read(Property prop, UIEntity entity) {
        return editorFor(prop).convertFrom(prop, prop.read(entity));
    }

    public static String property(String name, Options opts) {
        Object context = opts.hash("object");
        Context ctx = opts.context;
        while (context == null && ctx != null) {
            context = ctx.model() instanceof Function ? ctx.model() : null;
            ctx = ctx.parent();
        }
        if (context == null) {
            throw new IllegalArgumentException("Unresolved context, please set 'object' param");
        }
        return ((Function<String,String>) context).apply(name);
    }
}
