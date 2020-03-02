package alberapps.android.tiempobus.favoritos.googledriverest;

public class DriveFileInfo {

    private String name;

    private String id;

    private boolean folder = false;

    public DriveFileInfo(String name, String id, boolean folder) {
        this.name = name;
        this.id = id;
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }
}
