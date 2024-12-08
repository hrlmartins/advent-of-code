import sys
from collections import defaultdict
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def construct_antena_map(raw_map):
    antena_pos = defaultdict(list)
    for row in range(len(raw_map)):
        for col in range(len(raw_map[0])):
            current_value = raw_map[row][col]
            if current_value.isalnum():
                if current_value not in antena_pos:
                    antena_pos[current_value] = []
                antena_pos[current_value].append((row, col))
    return antena_pos


def calculate_antinodes(a_map, antena_pos, part2=False):
    antinode_pos = set()
    if part2:
        for positions in antena_pos.values():
            if len(positions) > 1:
                antinode_pos |= set(positions)

    for positions in antena_pos.values():
        for i in range(len(positions)):
            for j in range(i + 1, len(positions)):
                fa_row, fa_col = positions[i]
                sa_row, sa_col = positions[j]

                diff_row = sa_row - fa_row
                diff_col = sa_col - fa_col
                antinodes = []
                if part2:
                    # We just keep going... so the plan is to iterate in excess
                    # as its easier to use that value and then filter out the ones
                    # that are out of bounds
                    neg_offset_pos = (fa_row - diff_row, fa_col - diff_col)
                    pos_offset_pos = (sa_row + diff_row, sa_col + diff_col)
                    for _ in range(len(a_map) + len(a_map[0])):
                        antinodes += [neg_offset_pos, pos_offset_pos]
                        neg_offset_pos = (
                            neg_offset_pos[0] - diff_row,
                            neg_offset_pos[1] - diff_col,
                        )
                        pos_offset_pos = (
                            pos_offset_pos[0] + diff_row,
                            pos_offset_pos[1] + diff_col,
                        )
                else:
                    antinodes += [
                        (fa_row - diff_row, fa_col - diff_col),
                        (sa_row + diff_row, sa_col + diff_col),
                    ]

                antinode_pos |= set(
                    list(filter(lambda x: is_within_range(a_map, x), antinodes))
                )
    return antinode_pos


def is_within_range(raw_map, position):
    max_row = len(raw_map)
    max_col = len(raw_map[0])

    return 0 <= position[0] < max_row and 0 <= position[1] < max_col


def part1(data: list[str]) -> Any:
    a_map = [list(line) for line in data]
    # Construct map with all antenas. The map contains an array with all positions for that antenna
    # process the antena dict to create the antinodes between all of the antena positions
    antena_pos = construct_antena_map(a_map)
    return len(calculate_antinodes(a_map, antena_pos))


def part2(data: list[str]) -> Any:
    a_map = [list(line) for line in data]
    # Construct map with all antenas. The map contains an array with all positions for that antenna
    # process the antena dict to create the antinodes between all of the antena positions
    antena_pos = construct_antena_map(a_map)
    return len(calculate_antinodes(a_map, antena_pos, True))


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
