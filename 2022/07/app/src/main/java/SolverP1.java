import models.Directory;
import models.GadgetFile;
import models.GadgetFileSystem;
import models.RegularFile;
import models.commands.ChangeDirectory;
import models.commands.Command;
import models.commands.ListDirectory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolverP1 {
    public void solve(List<String> allLines) {
        // Construct file system
        // Navigate the file system changing directories
        // at each list command fill the current directory with the information given
        var fileSystem = new GadgetFileSystem();

        for (int pos = 0; pos < allLines.size(); pos++) {
            var rawCommand = allLines.get(pos);
            if (rawCommand.isEmpty() || rawCommand.isBlank()) continue;

            if (isCommmand(rawCommand)) {
                // hackz
                var command = processRawCommand(rawCommand, allLines, pos + 1);

                if (command instanceof ChangeDirectory cd) {
                    fileSystem.changeDirectory(cd);
                } else {
                    // well actually it's a listing... we need to gather the list information
                    var listDirectory = (ListDirectory) command;
                    listDirectory.files().forEach(fileSystem::addFileToCurrentDirectory);
                }
            }
        }

        // Calculate file size (with at most 100k in size)
        var dirSize = new HashMap<String, BigInteger>();
        var totalSize = calculateSize(fileSystem.getRootDir(), dirSize);
        var limitSize = BigInteger.valueOf(100000);

        System.out.printf("Total size: %d\n", totalSize);
        System.out.println(dirSize.values().stream().filter(value -> value.compareTo(limitSize) <= 0).reduce(BigInteger::add).get());

    }

    private BigInteger calculateSize(GadgetFile root, Map<String, BigInteger> dirSize) {
        return switch (root) {
            case RegularFile regularFile -> regularFile.fileSize();
            case Directory directory -> {
                var sumOf =
                        directory.getFiles()
                                .values().stream()
                                .map(f -> calculateSize(f, dirSize))
                                .reduce(BigInteger.valueOf(0), BigInteger::add);

                dirSize.put(extractDirectoryPath(directory), sumOf);
                yield sumOf;
            }
        };
    }

    private String extractDirectoryPath(Directory root) {
        if (root.getFileName().equals("/")) {
            return "/";
        }

        return extractDirectoryPath(root.getParent()) + root.getFileName() + "/";
    }

    private Command processRawCommand(String rawCommand, List<String> allLines, int pos) {
        // 0 - dollar sign, 1 - command, 2 - argument
        String[] command = rawCommand.split(" ");

        if (command[1].equals("cd")) {
            return new ChangeDirectory(command[2]);
        } else {
            var tmpList = new ArrayList<GadgetFile>();
            while (pos < allLines.size() && !allLines.get(pos).startsWith("$")) {
                processListOutput(allLines, pos, tmpList);
                pos += 1;
            }

            return new ListDirectory(tmpList);
        }
    }

    private static void processListOutput(List<String> allLines, int pos, ArrayList<GadgetFile> tmpList) {
        var output = allLines.get(pos);
        if (output.isBlank() || output.isEmpty()) return;

        // 0 - dir or filesize, 1 - filename
        String[] info = output.split(" ");
        if (info[0].startsWith("dir")) {
            tmpList.add(new Directory(info[1], null));
        } else {
            tmpList.add(new RegularFile(info[1], info[0]));
        }
    }

    private boolean isCommmand(String rawCommand) {
        return rawCommand.startsWith("$");
    }
}
