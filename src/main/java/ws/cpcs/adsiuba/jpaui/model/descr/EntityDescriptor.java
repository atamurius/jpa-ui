package ws.cpcs.adsiuba.jpaui.model.descr;

import ws.cpcs.adsiuba.jpaui.model.UIDisplayProperty;
import ws.cpcs.adsiuba.jpaui.model.UIEntity;
import ws.cpcs.adsiuba.jpaui.model.UIPropertiesOrder;

import javax.persistence.Entity;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static ws.cpcs.adsiuba.jpaui.model.descr.NameUtils.dashSeparated;
import static ws.cpcs.adsiuba.jpaui.model.descr.NameUtils.plural;
import static ws.cpcs.adsiuba.jpaui.model.descr.NameUtils.spaceSeparated;

/**
 * Describes entity for UI
 */
public class EntityDescriptor<T extends UIEntity> {

    private Class<T> entityClass;
    private String name;
    private String pluralName;
    private String id;
    private String iconId;
    private Map<String,List<Property<?>>> views = new HashMap<>();
    private Map<String,Property<?>> properties;

    private EntityDescriptor() { }

    public static class Builder {
        private EntityDescriptor<?> descriptor = new EntityDescriptor<>();

        @SuppressWarnings("unchecked")
        public Builder(Class<? extends UIEntity> entity) {
            requireNonNull(entity);
            requireNonNull(entity.getAnnotation(Entity.class), "Entity class should be annotated with @Entity");
            descriptor.entityClass = (Class) entity;
            descriptor.iconId = "file";
        }

        public Builder withName(String name) {
            descriptor.name = name;
            return this;
        }

        public Builder withPlural(String name) {
            descriptor.pluralName = name;
            return this;
        }

        public Builder withIcon(String name) {
            descriptor.iconId = name;
            return this;
        }

        public Builder withId(String name) {
            descriptor.id = name;
            return this;
        }

        public Builder withView(String name, Class<?> view) {
            if (! view.isAssignableFrom(descriptor.entityClass)) {
                throw new IllegalArgumentException(view +" is not interface of "+ descriptor.entityClass);
            }
            descriptor.views.put(name, new ArrayList<>(Property.extract(descriptor, view).values()));
            return this;
        }

        public EntityDescriptor<?> build() {
            if (descriptor.name == null) {
                descriptor.name = spaceSeparated(descriptor.entityClass.getSimpleName());
            }
            if (descriptor.pluralName == null) {
                descriptor.pluralName = plural(descriptor.name);
            }
            if (descriptor.id == null) {
                descriptor.id = dashSeparated(descriptor.pluralName);
            }
            descriptor.properties = Property.extract(descriptor, descriptor.getType());
            List<Property<?>> properties = new ArrayList<>((descriptor.properties.values()));
            properties.removeIf(p -> p.getName().equals("Id"));
            descriptor.views.put("default", properties);
            return descriptor;
        }
    }

    public Class<T> getType() {
        return entityClass;
    }

    public String getName() {
        return name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public String getIconId() {
        return iconId;
    }

    public String getId() {
        return id;
    }

    public List<Property<?>> getProperties() {
        return new ArrayList<>(properties.values());
    }

    public List<Property<?>> getProperties(String view) {
        return views.computeIfAbsent(view, v -> views.get("default"));
    }

    public Property<?> getProperty(String name) {
        return properties.computeIfAbsent(name, n -> {
            throw new IllegalArgumentException("Undefined property "+ entityClass.getCanonicalName() +"."+ name); });
    }

    public String getDescription(T item) {
        if (item == null) return null;
        Property<?> descriptor = getDescriptor();
        return descriptor != null ? String.valueOf(descriptor.read(item))
                : name +" "+ item.getId();
    }

    public Property<?> getDescriptor() {
        return properties.values().stream()
                .filter(p -> p.isAnnotated(UIDisplayProperty.class))
                .findAny()
                .orElse(null);
    }
}
