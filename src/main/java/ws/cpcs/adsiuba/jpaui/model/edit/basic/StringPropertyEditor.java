package ws.cpcs.adsiuba.jpaui.model.edit.basic;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;
import ws.cpcs.adsiuba.jpaui.model.edit.PropertyEditor;

@Service
@Order(100)
public class StringPropertyEditor implements PropertyEditor<String> {
    @Override
    public boolean supports(Property property) {
        return property.getType().equals(String.class);
    }

    @Override
    public String convertFrom(Property<String> property, String value) {
        return value;
    }

    @Override
    public String convertTo(Property<String> property, String value) {
        return value;
    }
}
