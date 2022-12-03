package models;

import java.util.ArrayList;
import java.util.List;

public record Rucksack(List<Character> contents) {
    public List<Character> firstCompartimentContent() {
        return new ArrayList<>(contents.subList(0, contents.size() / 2));
    }

    public List<Character> secondCompartimentContent() {
        return new ArrayList<>(contents.subList(contents.size() / 2, contents.size()));
    }

    public List<Character> fullContent() {
        return new ArrayList<>(contents);
    }
}
