package ws.cpcs.adsiuba.jpaui.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.PostConstruct;
import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ws.cpcs.adsiuba.jpaui.model.NameUtils.dashSeparated;
import static ws.cpcs.adsiuba.jpaui.model.NameUtils.plural;
import static ws.cpcs.adsiuba.jpaui.model.NameUtils.spaceSeparated;

/**
 * Describes entity for UI
 */
public class EntityDescriptor<T extends WithId<?>> {

    private Class<T> entityClass;
    private JpaRepository<T,?> repository;
    private String name;
    private String pluralName;
    private String id;
    private String iconId = "file";
    private Map<String,PropertyEnum> props = new HashMap<>();

    @Autowired(required = false)
    private List<JpaRepository> repositories;

    public EntityDescriptor(Class<T> entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass);
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        repository = Objects.requireNonNull(repositories, "No Repositories found").stream()
                .filter(r -> entityClass.equals(findRepoEntity(r.getClass())))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find Repository for " + entityClass));
        repositories = null;
        if (name == null) {
            name = spaceSeparated(entityClass.getSimpleName());
        }
        if (pluralName == null) {
            pluralName = plural(name);
        }
        if (id == null) {
            id = dashSeparated(pluralName);
        }
        props.put("default", new PropertyEnum(entityClass));
    }

    private Class<?> findRepoEntity(Type type) {
        if (type instanceof ParameterizedType &&
                JpaRepository.class.isAssignableFrom((Class) ((ParameterizedType) type).getRawType())) {
            Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
            if (arguments != null && arguments.length > 0 && arguments[0] instanceof Class)
                return  (Class) arguments[0];
        }
        if (type instanceof Class) {
            for (Type t: ((Class) type).getGenericInterfaces()) {
                Class<?> entity = findRepoEntity(t);
                if (entity != null)
                    return entity;
            }
        }
        if (type instanceof ParameterizedType) {
            return findRepoEntity(((ParameterizedType) type).getRawType());
        }
        return null;
    }

    public EntityDescriptor<T> withName(String name) {
        this.name = name;
        return this;
    }

    public EntityDescriptor<T> withPlural(String name) {
        this.pluralName = name;
        return this;
    }

    public EntityDescriptor<T> withIcon(String name) {
        this.iconId = name;
        return this;
    }

    public EntityDescriptor<T> withId(String name) {
        this.id = name;
        return this;
    }

    @SuppressWarnings("unchecked")
    public EntityDescriptor<T> withView(String name, Class view) {
        if (! view.isAssignableFrom(entityClass)) {
            throw new IllegalArgumentException(view +" is not interface of "+ entityClass);
        }
        this.props.put(name, new PropertyEnum(view));
        return this;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public JpaRepository<T,?> getRepository() {
        return repository;
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

    public List<PropertyEnum.Property> getProperties() {
        return getProperties("default");
    }

    public List<PropertyEnum.Property> getProperties(String view) {
        return props.computeIfAbsent(view, v -> props.get("default")).getProperties();
    }
}
