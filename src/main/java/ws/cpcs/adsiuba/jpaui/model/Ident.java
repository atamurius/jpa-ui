package ws.cpcs.adsiuba.jpaui.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Entity with ID of given type
 */
public interface Ident<T extends Serializable> {

    T getId();

    static boolean equals(Ident<?> self, Object other) {
        return !(other == null || !other.getClass().equals(self.getClass()))
                && Objects.equals(self.getId(), ((Ident) other).getId());
    }

    static int hashCode(Ident<?> obj) {
        return obj == null ? 0 : Objects.hashCode(obj.getId());
    }
}
