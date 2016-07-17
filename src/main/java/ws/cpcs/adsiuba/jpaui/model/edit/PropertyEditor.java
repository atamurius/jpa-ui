package ws.cpcs.adsiuba.jpaui.model.edit;

import ws.cpcs.adsiuba.jpaui.model.descr.Property;

/**
 * Used for convert types from and to property type,
 * specifies editor and view templates
 */
public interface PropertyEditor<T> {

    boolean supports(Property property);

    String convertFrom(Property<T> property, T value);

    T convertTo(Property<T> property, String value);
}
