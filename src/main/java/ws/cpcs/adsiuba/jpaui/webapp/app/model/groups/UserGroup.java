package ws.cpcs.adsiuba.jpaui.webapp.app.model.groups;

import ws.cpcs.adsiuba.jpaui.model.WithId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserGroup implements WithId<Long> {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
