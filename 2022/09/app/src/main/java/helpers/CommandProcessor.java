package helpers;

import models.commands.*;

public class CommandProcessor {
    public static Command processCommand(String[] splitCommand) {
        if (splitCommand[0].equals("U")) {
            return new Up(Integer.parseInt(splitCommand[1]));
        } else if (splitCommand[0].equals("D")) {
            return new Down(Integer.parseInt(splitCommand[1]));
        } else if (splitCommand[0].equals("L")) {
            return new Left(Integer.parseInt(splitCommand[1]));
        } else {
            return new Right(Integer.parseInt(splitCommand[1]));
        }
    }

}
