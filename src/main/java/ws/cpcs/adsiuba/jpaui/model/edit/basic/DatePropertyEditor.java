package ws.cpcs.adsiuba.jpaui.model.edit.basic;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ws.cpcs.adsiuba.jpaui.model.descr.Property;
import ws.cpcs.adsiuba.jpaui.model.edit.PropertyEditor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Order(100)
public class DatePropertyEditor implements PropertyEditor<Date> {

    private final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        }
    };

    @Override
    public boolean supports(Property property) {
        return property.getType().equals(Date.class);
    }

    @Override
    public String convertFrom(Property<Date> property, Date value) {
        return value == null ? null : dateFormat.get().format(value);
    }

    @Override
    public Date convertTo(Property<Date> property, String value) {
        try {
            return isBlank(value) ? null : dateFormat.get().parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Illegal date: "+ value, e);
        }
    }

    @Override
    public Class getTemplateBase() {
        return StringPropertyEditor.class;
    }

    @Override
    public String render(String action, Property<Date> property, Date value, Map<String, Object> ctx) {
        String result = PropertyEditor.super.render(action, property, value, ctx);
        if ("edit".equals(action)) {
            ctx.put("type", "datetime-local");
        } else if ("show".equals(action)) {
            ctx.put("value", value == null ? null :
                    DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.US).format(value));
        }
        return result;
    }
}
