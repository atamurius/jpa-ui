package ws.cpcs.adsiuba.jpaui.webapp.app.model;

import ws.cpcs.adsiuba.jpaui.model.UIDisplayProperty;
import ws.cpcs.adsiuba.jpaui.model.UIEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserGroup implements UIEntity<Long> {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @Override
    public Long getId() {
        return id;
    }

    @UIDisplayProperty
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
