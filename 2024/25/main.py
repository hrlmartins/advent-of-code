import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def process_raw_grids(data):
    grids = []
    current_grid = []

    for line in data:
        if line.strip() == "":
            grids.append(current_grid)
            current_grid = []
        else:
            current_grid.append(line)

    if current_grid:
        grids.append(current_grid)

    grid_sets = []
    for grid in grids:
        positions = set()
        for row, line in enumerate(grid):
            for col, char in enumerate(line):
                if char == "#":
                    positions.add((row, col))
        grid_sets.append(positions)

    return grid_sets, grids


def part1(data: list[str]) -> Any:
    grid_sets, _ = process_raw_grids(data)
    keys = [grid for grid in grid_sets if (0, 0) not in grid]
    locks = [grid for grid in grid_sets if (0, 0) in grid]

    return sum(1 for l in locks for k in keys if len(l & k) == 0)


def part2(data: list[str]) -> Any:
    return None


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
