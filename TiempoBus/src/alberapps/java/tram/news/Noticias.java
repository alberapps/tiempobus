package alberapps.java.tram.news;

import java.util.List;

public class Noticias {

    private String title;

    private String description;

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private List<NoticiaRss> noticiasList;

    public List<NoticiaRss> getNoticiasList() {
        return noticiasList;
    }

    public void setNoticiasList(List<NoticiaRss> noticiasList) {
        this.noticiasList = noticiasList;
    }
}
