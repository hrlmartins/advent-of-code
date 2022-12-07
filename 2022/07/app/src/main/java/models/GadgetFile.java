package models;

public abstract sealed class GadgetFile permits RegularFile, Directory {
    protected boolean isFile;
    protected String fileName;

    public GadgetFile(boolean isFile, String fileName) {
        this.isFile = isFile;
        this.fileName = fileName;
    }

    public boolean isFile() {
        return this.isFile;
    }

    public String getFileName() {
        return this.fileName;
    }
}
