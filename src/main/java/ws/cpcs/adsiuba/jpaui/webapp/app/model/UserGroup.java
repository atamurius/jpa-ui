package ws.cpcs.adsiuba.jpaui.webapp.app.model;

import ws.cpcs.adsiuba.jpaui.model.Ident;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserGroup implements Ident<Long> {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
