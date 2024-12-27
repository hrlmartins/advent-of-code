import sys
from collections import deque
from functools import cache
from typing import Any

KEYPAD_GRID = [["7", "8", "9"], ["4", "5", "6"],
               ["1", "2", "3"], ["#", "0", "A"]]

DIR_GRID = [["#", "^", "A"], ["<", "v", ">"]]


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
    dir_map = {
        (0, 1): ">",
        (1, 0): "v",
        (-1, 0): "^",
        (0, -1): "<",
    }

    to_visit = deque([(0, pos, [])])
    lowest_cost = -1

    while to_visit:
        node = to_visit.popleft()
        if lowest_cost > -1 and node[0] > lowest_cost:
            continue

        if node[1] == end_pos:
            if lowest_cost == -1:
                lowest_cost = node[0]

            yield (node[0], node[2])

        if lowest_cost > -1 and node[0] > lowest_cost:
            continue

        for dir in directions():
            new_pos = move_tuple(node[1], dir)
            if is_in_bounds(grid, new_pos):
                new_dir = dir_map[dir]
                to_visit.append((node[0] + 1, new_pos, node[2] + [new_dir]))

    yield (-1, [])


@cache
def resolve(dir, start_dir, iteration, max_iterations):
    if iteration == max_iterations:
        return 1

    dest = find_all_pos(DIR_GRID, dir)[0]

    count = sys.maxsize
    # no stone unturned... just run through all best paths and
    # check which provides us the smallest cost
    for cost, calc_path in find_path(DIR_GRID, start_dir, dest):
        if cost < 0:
            continue
        tmp_count = 0
        tmp_start_dir = find_all_pos(DIR_GRID, "A")[0]
        for p in calc_path + ["A"]:
            tmp_count += resolve(p, tmp_start_dir,
                                 iteration + 1, max_iterations)
            tmp_start_dir = find_all_pos(DIR_GRID, p)[0]
        count = min(count, tmp_count)

    return count


def part1(data: list[str]) -> Any:
    start_dial_init = find_all_pos(KEYPAD_GRID, "A")[0]
    start_dir_init = find_all_pos(DIR_GRID, "A")[0]

    complexity_list = []
    for code in data:
        start_dial = start_dial_init
        start_dir = start_dir_init
        dial_paths = [[]]
        for c in code:
            dest = find_all_pos(KEYPAD_GRID, c)[0]
            many_paths = [
                p for p in find_path(KEYPAD_GRID, start_dial, dest) if p[0] >= 0
            ]
            dial_paths = [e + p + ["A"]
                          for _, p in many_paths for e in dial_paths]
            start_dial = dest

        for _ in range(2):
            iter_paths = []
            for d_p in dial_paths:
                start_dir = start_dir_init
                all_paths = [[]]
                for p in d_p:
                    dest = find_all_pos(DIR_GRID, p)[0]
                    _, path = next(find_path(DIR_GRID, start_dir, dest))
                    all_paths = [e + path + ["A"] for e in all_paths]
                    start_dir = dest
                iter_paths += all_paths

            dial_paths = iter_paths.copy()

        num_code = int(code[: len(code) - 1])
        complexity = len(min(dial_paths, key=len))
        complexity_list += [num_code * complexity]
    return sum(complexity_list)


def part2(data: list[str]) -> Any:
    start_dial_init = find_all_pos(KEYPAD_GRID, "A")[0]
    start_dir_init = find_all_pos(DIR_GRID, "A")[0]

    total_complexity = 0
    for code in data:
        start_dial = start_dial_init
        start_dir = start_dir_init
        dial_paths = [[]]
        for c in code:
            dest = find_all_pos(KEYPAD_GRID, c)[0]
            paths = [p for p in find_path(
                KEYPAD_GRID, start_dial, dest) if p[0] >= 0]
            dial_paths = [e + p + ["A"] for _, p in paths for e in dial_paths]
            start_dial = dest

        min_seq_len = sys.maxsize
        # there are several presses that takes us there
        # only some takes us to the shortest sequence
        # so just try them all and fetch the smallest
        for path in dial_paths:
            tmp_len = 0
            start_dir = start_dir_init
            for step in path:
                tmp_len += resolve(step, start_dir, 0, 25)
                start_dir = find_all_pos(DIR_GRID, step)[0]
            min_seq_len = min(min_seq_len, tmp_len)

        total_complexity += int(code[: len(code) - 1]) * min_seq_len

    return total_complexity


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
