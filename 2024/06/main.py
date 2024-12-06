import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def get_lab_info(lab):
    start_pos = ()
    obstacles = []
    for row_idx, row in enumerate(lab):
        for col_idx, value in enumerate(row):
            if value == "^":
                start_pos = (row_idx, col_idx)
            elif value == "#":
                obstacles.append((row_idx, col_idx))

    return (start_pos, obstacles)


def is_in_bounds_limits(lab, row, col, facing_direction) -> bool:
    match facing_direction:
        case "UP": return row > 0
        case "RIGHT": return col < len(lab[0]) - 1
        case "DOWN": return row < len(lab) - 1
        case _: return col > 0


def turn(facing_direction):
    match facing_direction:
        case "UP": return "RIGHT"
        case "RIGHT": return "DOWN"
        case "DOWN": return "LEFT"
        case _: return "UP"


def next_guard_position(lab, obstacles, facing_direction, guard_position):
    row_pos, col_pos = guard_position
    directions = {
        "UP": (
            lambda obs: obs[0] < row_pos and obs[1] == col_pos,
            lambda: (0, col_pos),
            lambda obs: max(obs, key=lambda x: x[0]),
            lambda obs: (obs[0] + 1, obs[1]),
        ),
        "DOWN": (
            lambda obs: obs[0] > row_pos and obs[1] == col_pos,
            lambda: (len(lab) - 1, col_pos),
            lambda obs: min(obs, key=lambda x: x[0]),
            lambda obs: (obs[0] - 1, obs[1]),
        ),
        "LEFT": (
            lambda obs: obs[1] < col_pos and obs[0] == row_pos,
            lambda: (row_pos, 0),
            lambda obs: max(obs, key=lambda x: x[1]),
            lambda obs: (obs[0], obs[1] + 1),
        ),
        "RIGHT": (
            lambda obs: obs[1] > col_pos and obs[0] == row_pos,
            lambda: (row_pos, len(lab[0]) - 1),
            lambda obs: min(obs, key=lambda x: x[1]),
            lambda obs: (obs[0], obs[1] - 1),
        ),
    }

    is_obstacle_valid, edge_position, nearest_obstacle, next_position = directions[
        facing_direction
    ]

    possible_obstacles = [obs for obs in obstacles if is_obstacle_valid(obs)]
    return (
        edge_position()
        if not possible_obstacles
        else next_position(nearest_obstacle(possible_obstacles))
    )


def generate_pairs_between(current_pos, next_pos):
    start_row, start_col = current_pos
    end_row, end_col = next_pos
    if start_col == end_col:
        return [
            (i, start_col)
            for i in range(min(start_row, end_row), max(start_row, end_row) + 1)
        ]
    else:
        return [
            (start_row, i)
            for i in range(min(start_col, end_col), max(start_col, end_col) + 1)
        ]


def produce_path(lab, obstacles, facing_direction, initial_pos, part2=False):
    path = set()
    guard_pos = initial_pos
    test_path = set()
    loop_detected = 0
    while True:
        next_pos = next_guard_position(
            lab, obstacles, facing_direction, guard_pos)
        if part2:
            test_path = path | set(generate_pairs_between(guard_pos, next_pos))
            if len(test_path) <= len(path):
                loop_detected += 1
                if loop_detected == 4:
                    return (test_path, True)
            else:
                loop_detected = 0
        path.update(generate_pairs_between(guard_pos, next_pos))
        guard_pos = next_pos
        if not is_in_bounds_limits(lab, next_pos[0], next_pos[1], facing_direction):
            break
        facing_direction = turn(facing_direction)

    return (path, False)


def part1(data: list[str]) -> Any:
    lab = [list(row) for row in data]
    facing_direction = "UP"
    guard_pos, obstacles = get_lab_info(lab)
    path, _ = produce_path(lab, obstacles, facing_direction, guard_pos)
    return len(path)


def part2(data: list[str]) -> Any:
    lab = [list(row) for row in data]
    facing_direction = "UP"
    guard_pos, obstacles = get_lab_info(lab)
    path, _ = produce_path(lab, obstacles, facing_direction, guard_pos)
    initial_guard_pos = guard_pos
    path.remove(initial_guard_pos)

    return sum(
        1
        for possible_pos in path
        if produce_path(
            lab, obstacles + [possible_pos], facing_direction, guard_pos, True
        )[1]
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
