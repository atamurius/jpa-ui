package ws.cpcs.adsiuba.jpaui.model.edit.basic;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;
import ws.cpcs.adsiuba.jpaui.model.edit.PropertyEditor;

@Service
@Order(100)
public class NumericPropertyEditor implements PropertyEditor<Number> {
    @Override
    public boolean supports(Property property) {
        return Number.class.isAssignableFrom(property.getType());
    }

    @Override
    public String convertFrom(Property<Number> property, Number value) {
        return String.valueOf(value);
    }

    @Override
    public Number convertTo(Property<Number> property, String value) {
        Class<Number> type = property.getType();
        if (type.equals(Integer.class)) {
            return Integer.valueOf(value);
        }
        if (type.equals(Long.class)) {
            return Long.valueOf(value);
        }
        throw new IllegalArgumentException("Unsupported type "+ type);
    }
}
