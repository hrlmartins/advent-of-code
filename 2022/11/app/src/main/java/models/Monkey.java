package models;

import java.util.Queue;
import java.util.function.Function;

public record Monkey(int idx, Queue<Item> items, Function<Item, Item> updateFunction, Function<Item, Integer> throwFunction) { }
