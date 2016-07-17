package ws.cpcs.adsiuba.jpaui.model.edit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.cpcs.adsiuba.jpaui.model.Ident;
import ws.cpcs.adsiuba.jpaui.model.descr.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

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

    @Transactional(readOnly = true)
    public <T extends Ident> T findById(EntityDescriptor<T> descriptor, String id) {
        Property<?> idProp = descriptor.getProperty("Id");
        return em.find(descriptor.getType(),
                editors.convertTo(idProp, id));
    }

    public <T extends Ident> T create(EntityDescriptor<T> descriptor) {
        try {
            return descriptor.getType().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Failed to create "+ descriptor.getType(), e);
        }
    }

    @Transactional
    public <T extends Ident> T process(Function<EntityService,T> body) {
        return em.merge(body.apply(this));
    }

    @Transactional(readOnly = true)
    public <T extends Ident> Page<T> find(EntityDescriptor<T> descriptor, Pageable page) {
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

    public <T extends Ident> T fillValues(EntityDescriptor<T> descriptor, T obj, Map<String,String> params) {
        params.forEach((param, value) -> {
            Property property = descriptor.getProperty(param);
            property.write(obj, editors.convertTo(property, value));
        });
        return obj;
    }

    public Map<String, String> readProperties(EntityDescriptor<?> descriptor, Ident item, String view) {
        return descriptor.getProperties(view).stream()
                .collect(toMap(
                        Property::getName,
                        p -> editors.convertFrom((Property) p, p.read(item)),
                        (a, b) -> b,
                        LinkedHashMap::new));
    }
}










