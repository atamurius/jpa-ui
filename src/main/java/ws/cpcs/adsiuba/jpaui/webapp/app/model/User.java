package ws.cpcs.adsiuba.jpaui.webapp.app.model;

import ws.cpcs.adsiuba.jpaui.model.UIDisplayProperty;
import ws.cpcs.adsiuba.jpaui.model.UIPropertiesOrder;
import ws.cpcs.adsiuba.jpaui.model.UIEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@UIPropertiesOrder({"Id","Name","Title","UserGroup","Active","LastLogin","WasLoggedIn"})
public class User implements UIEntity<Long> {

    @Id @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 6, max = 100)
    @Pattern(regexp = "[\\w\\.-]+", message = "Name must be alphanumeric")
    private String name;

    @Size(min = 1, max = 100)
    @NotNull
    private String title;

    private Date lastLogin;

    @NotNull
    @ManyToOne
    private UserGroup userGroup;

    @Column(name = "activeFlag")
    private Boolean active = false;

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

    @UIDisplayProperty
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

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public boolean getWasLoggedIn() {
        return lastLogin != null;
    }
}
