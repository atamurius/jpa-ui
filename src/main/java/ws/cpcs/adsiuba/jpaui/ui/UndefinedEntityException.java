package ws.cpcs.adsiuba.jpaui.ui;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UndefinedEntityException extends RuntimeException {
    public UndefinedEntityException(String entity) {
        super(entity);
    }
}
