import sys
from collections import deque
from typing import Any
import itertools


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def find_all_pos(grid, element):
    all = []
    for row, data in enumerate(grid):
        for col, val in enumerate(data):
            if val == element:
                all.append((row, col))

    return all


def is_in_bounds(grid, position):
    rows, cols = len(grid), len(grid[0])
    row, col = position
    return 0 <= row < rows and 0 <= col < cols and grid[row][col] != "#"


def directions():
    return [(0, 1), (1, 0), (-1, 0), (0, -1)]


def move_tuple(t, dir):
    return (t[0] + dir[0], t[1] + dir[1])


def find_path(grid, pos, end_pos):
    to_visit = deque([(0, pos)])
    visited = set()

    while to_visit:
        node = to_visit.popleft()
        if node[1] == end_pos:
            return node[0]
        if node[1] in visited:
            continue

        visited.add(node[1])
        for dir in directions():
            new_pos = move_tuple(node[1], dir)
            if is_in_bounds(grid, new_pos):
                to_visit.append((node[0] + 1, new_pos))

    return -1


def part1(data: list[str]) -> Any:
    grid = [list(row) for row in data]
    start_pos = find_all_pos(grid, "S")[0]
    end_pos = find_all_pos(grid, "E")[0]
    initial_cost = find_path(grid, start_pos, end_pos)
    all_barriers = find_all_pos(grid, "#")

    saved_nseconds = {}
    for row, col in all_barriers:
        grid[row][col] = "."
        cost = find_path(grid, start_pos, end_pos)

        if cost < initial_cost:
            saved = initial_cost - cost
            if saved not in saved_nseconds:
                saved_nseconds[saved] = 0
            saved_nseconds[saved] += 1
        grid[row][col] = "#"

    return sum(v for k, v in saved_nseconds.items() if k >= 100)

def iterate_groups(lst, group_sizes):
    for size in group_sizes:
        for combination in itertools.combinations(lst, size):
            yield list(combination)

def part2(data: list[str]) -> Any:
    grid = [list(row) for row in data]
    start_pos = find_all_pos(grid, "S")[0]
    end_pos = find_all_pos(grid, "E")[0]
    initial_cost = find_path(grid, start_pos, end_pos)
    all_barriers = find_all_pos(grid, "#")

    saved_nseconds = {}

    for group in iterate_groups(all_barriers, list(range(1, 20))):
        for row, col in group:
            grid[row][col] = "."

        cost = find_path(grid, start_pos, end_pos)

        if cost < initial_cost:
            saved = initial_cost - cost
            if saved not in saved_nseconds:
                saved_nseconds[saved] = 0
            saved_nseconds[saved] += 1

        for row, col in group:
            grid[row][col] = "#"

    print(saved_nseconds)
    return sum(v for k, v in saved_nseconds.items() if k >= 100)


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
