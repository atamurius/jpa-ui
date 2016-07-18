package ws.cpcs.adsiuba.jpaui.model.edit.basic;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;
import ws.cpcs.adsiuba.jpaui.model.edit.PropertyEditor;

@Service
@Order(100)
public class BoolPropertyEditor implements PropertyEditor<Boolean> {
    @Override
    public boolean supports(Property property) {
        return property.getType().equals(Boolean.class) || property.getType().equals(Boolean.TYPE);
    }

    @Override
    public String convertFrom(Property<Boolean> property, Boolean value) {
        return value == null || ! value ? "false" : "true";
    }

    @Override
    public Boolean convertTo(Property<Boolean> property, String value) {
        return "true".equals(value);
    }
}
