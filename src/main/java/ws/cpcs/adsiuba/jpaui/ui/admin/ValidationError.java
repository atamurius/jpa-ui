package ws.cpcs.adsiuba.jpaui.ui.admin;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ValidationError {
    private final String message;
    private final String property;
    private final Object value;

    public ValidationError(ConstraintViolation<?> violation) {
        this.message = violation.getMessage();
        this.property = StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                .map(Path.Node::getName).collect(Collectors.joining("."));
        this.value = violation.getInvalidValue();
    }

    public String getMessage() {
        return message;
    }

    public String getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }
}
