package ws.cpcs.adsiuba.jpaui.model.edit.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ws.cpcs.adsiuba.jpaui.model.UIEntity;
import ws.cpcs.adsiuba.jpaui.model.descr.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;
import ws.cpcs.adsiuba.jpaui.model.edit.EntityService;
import ws.cpcs.adsiuba.jpaui.model.edit.PropertyEditor;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Order(100)
public class RefPropertyEditor implements PropertyEditor<UIEntity<?>> {

    @Autowired
    private EntityService entityService;

    @Override
    public boolean supports(Property property) {
        return UIEntity.class.isAssignableFrom(property.getType());
    }

    @Override
    public String convertFrom(Property<UIEntity<?>> property, UIEntity<?> value) {
        return value == null ? null : entityService.getEntityId(value);
    }

    @Override
    public UIEntity<?> convertTo(Property<UIEntity<?>> property, String value) {
        return isBlank(value) ? null :
                entityService.findById(
                    entityService.getDescriptorOf(property.getType()),value);
    }

    @Override
    public String render(String action, Property<UIEntity<?>> property, UIEntity<?> value, Map<String, Object> ctx) {
        String result = PropertyEditor.super.render(action, property, value, ctx);
        ctx.put("value", value);
        EntityDescriptor<UIEntity<?>> descriptor = entityService.getDescriptorOf(property.getType());
        ctx.put("type", descriptor);
        return result;
    }
}
