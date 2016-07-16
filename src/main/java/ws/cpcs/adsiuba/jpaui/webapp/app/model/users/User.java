package ws.cpcs.adsiuba.jpaui.webapp.app.model.users;

import ws.cpcs.adsiuba.jpaui.model.WithId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class User implements WithId<Long>, ListedUser {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private String title;

    private Date lastLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        return WithId.equals(this, o);
    }

    @Override
    public int hashCode() {
        return WithId.hashCode(this);
    }
}
