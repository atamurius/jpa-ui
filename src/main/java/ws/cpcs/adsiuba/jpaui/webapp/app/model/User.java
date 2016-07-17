package ws.cpcs.adsiuba.jpaui.webapp.app.model;

import ws.cpcs.adsiuba.jpaui.model.Ident;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class User implements Ident<Long> {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private String title;

    private Date lastLogin;

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
