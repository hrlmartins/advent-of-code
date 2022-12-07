package models.commands;

public sealed interface Command permits ChangeDirectory, ListDirectory { }
