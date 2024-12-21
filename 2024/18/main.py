import sys
from collections import deque
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def is_in_bounds(grid, position):
    rows, cols = len(grid), len(grid[0])
    row, col = position
    return 0 <= row < rows and 0 <= col < cols and grid[row][col] != "#"


def pretty_print(grid):
    for row in grid:
        print("".join(row))


def create_matrix(n):
    return [["." for _ in range(n)] for _ in range(n)]


def fall_in_bytes(grid, bytes_pos, n):
    for b in bytes_pos[:n]:
        grid[b[0]][b[1]] = "#"


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
    bytes_pos = [(int(line.split(",")[1]), int(line.split(",")[0])) for line in data]
    matrix_size = 71
    grid = create_matrix(matrix_size)
    fall_in_bytes(grid, bytes_pos, 1024)
    pretty_print(grid)
    return find_path(grid, (0, 0), (matrix_size - 1, matrix_size - 1))


def part2(data: list[str]) -> Any:
    bytes_pos = [(int(line.split(",")[1]), int(line.split(",")[0])) for line in data]
    matrix_size = 71
    grid = create_matrix(matrix_size)

    result = ""
    for b in range(1025, len(bytes_pos)):
        fall_in_bytes(grid, bytes_pos, b)
        cost = find_path(grid, (0, 0), (matrix_size - 1, matrix_size - 1))
        if cost == -1:
            (row, col) = bytes_pos[b - 1]
            result = f"{col},{row}"
            break
    return result


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
