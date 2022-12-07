package models.commands;

import models.GadgetFile;

import java.util.List;

public record ListDirectory(List<GadgetFile> files) implements Command { }
