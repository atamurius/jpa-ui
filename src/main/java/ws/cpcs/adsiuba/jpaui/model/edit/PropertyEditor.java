package ws.cpcs.adsiuba.jpaui.model.edit;

import ws.cpcs.adsiuba.jpaui.model.descr.Property;

import java.util.Map;

/**
 * Used for convert types from and to property type,
 * specifies editor and view templates
 */
public interface PropertyEditor<T> {

    boolean supports(Property property);

    String convertFrom(Property<T> property, T value);

    T convertTo(Property<T> property, String value);

    default Class getTemplateBase() {
        return getClass();
    }

    default String render(String action, Property<T> property, T value, Map<String,Object> ctx) {
        ctx.put("value", convertFrom(property, value));
        ctx.put("property", property);
        return action;
    }
}
