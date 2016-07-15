package ws.cpcs.adsiuba.jpaui.ui.admin;

public class BreadCrumb {
    private String url;
    private String title;

    public BreadCrumb(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
