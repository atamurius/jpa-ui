package ws.cpcs.adsiuba.jpaui.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Entity with ID of given type
 */
public interface UIEntity<T extends Serializable> {

    T getId();

    static boolean equals(UIEntity<?> self, Object other) {
        return !(other == null || !other.getClass().equals(self.getClass()))
                && Objects.equals(self.getId(), ((UIEntity) other).getId());
    }

    static int hashCode(UIEntity<?> obj) {
        return obj == null ? 0 : Objects.hashCode(obj.getId());
    }
}
