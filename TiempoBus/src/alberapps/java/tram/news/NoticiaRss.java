package alberapps.java.tram.news;

public class NoticiaRss {

    private String title;

    private String description;

    private String comments;

    private String pubDate;

    private String contentEncoded;

    private String commentRss;

    private String slashComments;

    private String link;

    private String urlPrimeraImagen;

    //podcast

    private String guid;

    private String author;

    private String authorShort;

    private String duration;

    private String enclosureUrl;

    private boolean programa = false;

    public boolean isPrograma() {
        return programa;
    }

    public void setPrograma(boolean programa) {
        this.programa = programa;
    }

    public String getEnclosureUrl() {
        return enclosureUrl;
    }

    public void setEnclosureUrl(String enclosureUrl) {
        this.enclosureUrl = enclosureUrl;
    }

    public String getAuthorShort() {
        return authorShort;
    }

    public void setAuthorShort(String authorShort) {
        this.authorShort = authorShort;
    }

    public String getCommentRss() {
        return commentRss;
    }

    public void setCommentRss(String commentRss) {
        this.commentRss = commentRss;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getContentEncoded() {
        return contentEncoded;
    }

    public void setContentEncoded(String contentEncoded) {
        this.contentEncoded = contentEncoded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getSlashComments() {
        return slashComments;
    }

    public void setSlashComments(String slashComments) {
        this.slashComments = slashComments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlPrimeraImagen() {
        return urlPrimeraImagen;
    }

    public void setUrlPrimeraImagen(String urlPrimeraImagen) {
        this.urlPrimeraImagen = urlPrimeraImagen;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
