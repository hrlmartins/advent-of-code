import re
import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def print_tiles(tiles):
    for row in tiles:
        print("".join(row))


def directions():
    return [
        (0, 1),
        (0, -1),
        (1, 0),
        (-1, 0),
    ]


def is_in_bounds(tiles, y_axis, x_axis):
    return tiles[y_axis][x_axis] != "#"


def find_first_position(tiles):
    for row_idx, row in enumerate(tiles):
        for col_idx, value in enumerate(row):
            if value != "#":
                return (col_idx, row_idx)
    return None


def fill_map(tiles, start_pos, visited, x_max, y_max):
    to_visit = [start_pos]
    count = 0

    while to_visit:
        node = to_visit.pop()
        if node in visited:
            continue
        visited.add(node)
        count += 1
        for dir in directions():
            next_pos = ((node[0] + dir[0]) % x_max, (node[1] + dir[1]) % y_max)
            if next_pos not in visited and is_in_bounds(tiles, next_pos[1], next_pos[0]):
                to_visit.append(next_pos)
    return count


# each robot is a tuple of (start_x, start_y, v_x, v_y)
def part1(data: list[str]) -> Any:
    x_max = 101
    y_max = 103
    robot_pattern = r"p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)"
    robots = [tuple(map(int, re.findall(robot_pattern, r_raw)[0]))
              for r_raw in data]
    final_pos = []
    seconds = 100
    for robot in robots:
        (x, y, v_x, v_y) = robot
        walk = ((x + v_x * seconds) %
                x_max, (y + v_y * seconds) % y_max, v_x, v_y)
        final_pos.append((walk[0], walk[1]))

    half_x = x_max // 2
    half_y = y_max // 2
    # the checks are always open in the end at the limit of the quadrant
    f_quadrant = [(0, 0), (half_x, half_y)]
    s_quadrant = [(half_x + 1, 0), (x_max, half_y)]
    t_quadrant = [(0, y_max - half_y), (half_x, y_max)]
    fo_quadrant = [(half_x + 1, half_y + 1), (x_max, y_max)]

    quadrants = [f_quadrant, s_quadrant, t_quadrant, fo_quadrant]
    result = 0
    for q in quadrants:
        count = 0
        for pos in final_pos:
            (x, y) = pos
            (x1, y1) = q[0]
            (x2, y2) = q[1]

            if x1 <= x < x2 and y1 <= y < y2:
                count += 1
        if result == 0:
            result = count
        else:
            result *= count
    return result


def part2(data: list[str]) -> Any:
    x_max = 101
    y_max = 103
    robot_pattern = r"p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)"
    robots = [tuple(map(int, re.findall(robot_pattern, r_raw)[0]))
              for r_raw in data]
    result = 0
    for seconds in range(1, 100000):
        tiles = [["." for _ in range(x_max)] for _ in range(y_max)]
        for robot in robots:
            (x, y, v_x, v_y) = robot
            walk = ((x + v_x * seconds) %
                    x_max, (y + v_y * seconds) % y_max, v_x, v_y)
            tiles[walk[1]][walk[0]] = "#"
        positions_filled = fill_map(
            tiles, find_first_position(tiles), set(), x_max, y_max
        )

        if positions_filled < (x_max * y_max - len(robots)):
            # found the tree!
            print_tiles(tiles)
            result = seconds
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
