package ws.cpcs.adsiuba.jpaui.model.edit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;

import java.util.List;

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

    public <T> String convertFrom(Property<T> prop, T value) {
        return editorFor(prop).convertFrom(prop, value);
    }
}
