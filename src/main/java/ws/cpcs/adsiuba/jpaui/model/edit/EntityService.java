package ws.cpcs.adsiuba.jpaui.model.edit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.cpcs.adsiuba.jpaui.model.UIEntity;
import ws.cpcs.adsiuba.jpaui.model.descr.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Common methods on entities
 */
@Service
public class EntityService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PropertyEditorService editors;

    private Map<Class,EntityDescriptor<?>> descriptors;

    @Autowired
    public void registerDescriptors(List<EntityDescriptor<?>> descriptors) {
        this.descriptors = descriptors.stream().collect(toMap(EntityDescriptor::getType, identity()));
    }

    public <T extends UIEntity> EntityDescriptor<T> getDescriptorOf(Class<T> type) {
        return Optional.ofNullable((EntityDescriptor) descriptors.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unregistered entity "+ type));
    }

    public <T extends UIEntity> EntityDescriptor<T> getDescriptorOf(T entity) {
        return getDescriptorOf((Class) entity.getClass());
    }

    public String getEntityId(UIEntity<?> entity) {
        EntityDescriptor<?> descriptor = getDescriptorOf(requireNonNull(entity));
        return editors.convertFrom((Property) descriptor.getProperty("Id"), entity.getId());
    }

    @Transactional(readOnly = true)
    public <T extends UIEntity> T findById(EntityDescriptor<T> descriptor, String id) {
        Property<?> idProp = descriptor.getProperty("Id");
        return em.find(descriptor.getType(),
                editors.convertTo(idProp, id));
    }

    public <T extends UIEntity> T create(EntityDescriptor<T> descriptor) {
        try {
            return descriptor.getType().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Failed to create "+ descriptor.getType(), e);
        }
    }

    @Transactional
    public <T extends UIEntity> T process(Function<EntityService,T> body) {
        return em.merge(body.apply(this));
    }

    @Transactional(readOnly = true)
    public <T extends UIEntity> Page<T> find(EntityDescriptor<T> descriptor, Pageable page) {
        Class<T> type = descriptor.getType();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> c = cb.createQuery(Long.class);
        long total = em.createQuery(c.select(cb.count(c.from(type)))).getSingleResult();

        CriteriaQuery<T> q = cb.createQuery(type);
        return new PageImpl<>(
                em.createQuery(
                    q.select(q.from(type)))
                    .setFirstResult(page.getOffset())
                    .setMaxResults(page.getPageSize())
                    .getResultList(),
                page,
                total);
    }

    public <T extends UIEntity> T fillValues(T obj, Map<String,String> params) {
        EntityDescriptor<T> descriptor = getDescriptorOf(requireNonNull(obj));
        params.forEach((param, value) ->
            editors.write(descriptor.getProperty(param), obj, value));
        return obj;
    }

    public Map<String, String> readProperties(UIEntity<?> item) {
        EntityDescriptor<?> descriptor = getDescriptorOf(requireNonNull(item));
        return descriptor.getProperties().stream()
                .collect(toMap(
                        Property::getName,
                        p -> editors.read(p, item),
                        (a, b) -> b,
                        LinkedHashMap::new));
    }

    @Transactional
    public void deleteById(EntityDescriptor descriptor, String id) {
        Property<?> idProp = descriptor.getProperty("Id");
        em.remove(em.getReference(descriptor.getType(), editors.convertTo(idProp, id)));
    }
}










