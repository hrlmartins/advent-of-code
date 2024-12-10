import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def directions():
    return [
        (0, 1),
        (0, -1),
        (1, 0),
        (-1, 0),
    ]


def in_bounds(map, position):
    return 0 <= position[0] < len(map) and 0 <= position[1] < len(map[0])


def find_positions(map, target_value):
    positions = []
    for row_idx, row in enumerate(map):
        for col_idx, value in enumerate(row):
            if value == target_value:
                positions.append((row_idx, col_idx))
    return positions


def possible_trails(map, starting_position, destination_trail, part2=False) -> int:
    visited = set()
    next_path = [starting_position]
    count = 0

    while next_path:
        row, col = next_path.pop()
        if (row, col) not in visited:
            if not part2:
                visited.add((row, col))
            if map[row][col] == destination_trail:
                count += 1
            for dr, dc in directions():
                n_row, n_col = row + dr, col + dc
                if in_bounds(map, (n_row, n_col)):
                    step_size = map[n_row][n_col] - map[row][col]
                    if step_size == 1:
                        next_path.append((n_row, n_col))

    return count


def part1(data: list[str]) -> Any:
    trail_map = [list(map(int, row)) for row in data]
    starting_positions = find_positions(trail_map, 0)
    return sum(
        possible_trails(trail_map, position, 9) for position in starting_positions
    )


def part2(data: list[str]) -> Any:
    trail_map = [list(map(int, row)) for row in data]
    starting_positions = find_positions(trail_map, 0)
    return sum(
        possible_trails(trail_map, position, 9, True) for position in starting_positions
    )


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
