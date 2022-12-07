package models;

import models.commands.ChangeDirectory;

public class GadgetFileSystem {

    private Directory rootDir = new Directory("/", null);
    private Directory currentDirectory = rootDir;
    public void changeDirectory(ChangeDirectory changeDirectoryCommand) {
        if (changeDirectoryCommand.directoryName().equals(rootDir.fileName)) {
            currentDirectory = rootDir;
        } else {
            if (changeDirectoryCommand.directoryName().equals("..")) {
                currentDirectory = currentDirectory.getParent();

            } else if (changeDirectoryCommand.directoryName().equals("/")) {
                currentDirectory = rootDir;
            } else if (currentDirectory.hasFile(changeDirectoryCommand.directoryName())) {
                // always setting the parent just in case
                ((Directory) currentDirectory.getFile(changeDirectoryCommand.directoryName())).setParent(currentDirectory);
                currentDirectory = (Directory) currentDirectory.getFile(changeDirectoryCommand.directoryName());
            } else {
                //create new Directory
                var newDir = new Directory(changeDirectoryCommand.directoryName(), currentDirectory);
                currentDirectory = newDir;
            }
        }
    }

    public void addFileToCurrentDirectory(GadgetFile file) {
        currentDirectory.addFile(file.fileName, file);
    }

    public Directory getRootDir() {
        return rootDir;
    }
}
