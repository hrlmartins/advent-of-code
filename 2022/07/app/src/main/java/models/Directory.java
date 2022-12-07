package models;

import java.util.HashMap;
import java.util.Map;

public final class Directory extends GadgetFile {
    private HashMap<String, GadgetFile> files;

    private Directory parent;

    public Directory(String fileName, Directory parent) {
        super(false, fileName);
        files = new HashMap<>();
        this.parent = parent;
    }

    public void addFile(String name, GadgetFile file) {
        this.files.putIfAbsent(name, file);
    }

    public boolean hasFile(String fileName) {
        return files.containsKey(fileName);
    }

    public GadgetFile getFile(String fileName) {
        return files.get(fileName);
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public Map<String, GadgetFile> getFiles() {
        return files;
    }
}
