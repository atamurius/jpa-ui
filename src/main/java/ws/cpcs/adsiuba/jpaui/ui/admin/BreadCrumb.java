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

    public BreadCrumb withBase(String base) {
        url = base + url;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BreadCrumb that = (BreadCrumb) o;

        return url.equals(that.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
