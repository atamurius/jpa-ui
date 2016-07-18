package ws.cpcs.adsiuba.jpaui.model.edit.basic;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ws.cpcs.adsiuba.jpaui.model.UIDisplayProperty;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;
import ws.cpcs.adsiuba.jpaui.model.edit.PropertyEditor;

import java.util.Map;

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

    @Override
    public String render(String action, Property<String> property, String value, Map<String, Object> ctx) {
        if ("edit".equals(action)) {
            ctx.put("type", "text");
        } else if ("show".equals(action)) {
            ctx.put("link", property.isAnnotated(UIDisplayProperty.class));
        }
        return PropertyEditor.super.render(action, property, value, ctx);
    }
}
