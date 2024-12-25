import sys
from collections import deque
from typing import Any


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
    to_visit = deque([(0, pos, [pos])])
    visited = set()

    while to_visit:
        node = to_visit.popleft()
        if node[1] == end_pos:
            return (node[0], node[2])
        if node[1] in visited:
            continue

        visited.add(node[1])
        for dir in directions():
            new_pos = move_tuple(node[1], dir)
            if is_in_bounds(grid, new_pos):
                to_visit.append((node[0] + 1, new_pos, node[2] + [new_pos]))

    return (-1, [])


def manhattan_distance(pos1, pos2):
    r1, c1 = pos1
    r2, c2 = pos2
    return abs(r1 - r2) + abs(c1 - c2)


def part1(data: list[str]) -> Any:
    grid = [list(row) for row in data]
    start_pos = find_all_pos(grid, "S")[0]
    end_pos = find_all_pos(grid, "E")[0]
    initial_cost, path = find_path(grid, start_pos, end_pos)

    saved_nseconds = {}
    for i, i_pos in enumerate(path):
        for j in range(i + 1, len(path)):
            j_pos = path[j]
            dist = manhattan_distance(i_pos, j_pos)
            saved = j - i - dist
            if 0 < dist <= 2 and saved > 0:
                if saved not in saved_nseconds:
                    saved_nseconds[saved] = 0
                saved_nseconds[saved] += 1

    return sum(v for k, v in saved_nseconds.items() if k >= 100)


def part2(data: list[str]) -> Any:
    grid = [list(row) for row in data]
    start_pos = find_all_pos(grid, "S")[0]
    end_pos = find_all_pos(grid, "E")[0]
    initial_cost, path = find_path(grid, start_pos, end_pos)
    saved_nseconds = {}

    for i, i_pos in enumerate(path):
        for j in range(i + 1, len(path)):
            j_pos = path[j]
            dist = manhattan_distance(i_pos, j_pos)
            saved = j - i - dist
            if 0 < dist <= 20 and saved > 0:
                if saved not in saved_nseconds:
                    saved_nseconds[saved] = 0
                saved_nseconds[saved] += 1

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
