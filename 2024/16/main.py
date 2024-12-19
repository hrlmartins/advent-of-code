import heapq
import sys
from collections import defaultdict
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def print_grid(grid, positions):
    for row, data in enumerate(grid):
        for col, val in enumerate(data):
            if (row, col) in positions:
                print("O", end="")
            else:
                print(val, end="")
        print()


def find_all_pos(grid, element):
    all = []
    for row, data in enumerate(grid):
        for col, val in enumerate(data):
            if val == element:
                all.append((row, col))

    return all


def build_grid(data):
    return [list(row) for row in data]


def directions():
    return [(0, 1), (1, 0), (0, -1), (-1, 0)]


def move_tuple(t, dir):
    return (t[0] + dir[0], t[1] + dir[1])


def simple_visit(parents, cost_map, start_node, target_node):
    to_visit = [
        (target_node, dir) for dir in directions() if (target_node, dir) in cost_map
    ]
    visited = set()
    while to_visit:
        node = to_visit.pop()
        visited.add(node[0])
        if node not in parents:
            continue
        to_visit += parents[node]

    return visited


def visit_all(start_pos, end_pos, initial_dir, grid):
    visited = set()
    to_visit = []
    parents = defaultdict(list)
    cost_map = {(start_pos, initial_dir): 0}
    min_cost = -1
    # the heap has tuple with (cost, (r, c), direction)
    heapq.heappush(to_visit, (0, start_pos, initial_dir))

    while to_visit:
        cost, pos, curr_dir = heapq.heappop(to_visit)
        if (pos, curr_dir) in visited:
            continue
        if pos == end_pos:
            if min_cost == -1:
                min_cost = cost
            visited.add((pos, curr_dir))
            continue

        visited.add((pos, curr_dir))
        dirs = directions()
        for dir in dirs:
            curr_idx = dirs.index(curr_dir)
            next_idx = dirs.index(dir)
            turns = abs(next_idx - curr_idx) % len(dir)
            turns = min(turns, len(dirs) - turns)
            if turns > 1:
                # no point in turning back
                continue
            new_pos = move_tuple(pos, dir)
            if grid[new_pos[0]][new_pos[1]] != "#":
                new_cost = cost + 1 + (turns * 1000)
                if -1 < min_cost < new_cost:
                    continue

                if (new_pos, dir) not in cost_map or new_cost < cost_map[
                    (new_pos, dir)
                ]:
                    cost_map[(new_pos, dir)] = new_cost
                    parents[(new_pos, dir)] = [(pos, curr_dir)]
                    heapq.heappush(to_visit, (new_cost, new_pos, dir))
                elif new_cost == cost_map[(new_pos, dir)]:
                    parents[(new_pos, dir)].append((pos, curr_dir))

    print_grid(grid, simple_visit(parents, cost_map, start_pos, end_pos))
    return len(simple_visit(parents, cost_map, start_pos, end_pos))


def visit(start_pos, end_pos, initial_dir, grid):
    visited = set()
    to_visit = []
    # the heap has tuple with (cost, (r, c), direction)
    heapq.heappush(to_visit, (0, start_pos, initial_dir))

    while to_visit:
        cost, pos, curr_dir = heapq.heappop(to_visit)
        if pos in visited:
            continue
        if pos == end_pos:
            return cost

        visited.add(pos)
        dirs = directions()
        for dir in dirs:
            curr_idx = dirs.index(curr_dir)
            next_idx = dirs.index(dir)
            turns = abs(next_idx - curr_idx) % len(dir)
            turns = min(turns, len(dirs) - turns)
            new_pos = move_tuple(pos, dir)
            if grid[new_pos[0]][new_pos[1]] != "#":
                new_cost = cost + 1 + (turns * 1000)
                heapq.heappush(to_visit, (new_cost, new_pos, dir))

    return -1


def part1(data: list[str]) -> Any:
    grid = build_grid(data)
    start = find_all_pos(grid, "S")[0]
    end = find_all_pos(grid, "E")[0]
    return visit(start, end, (0, 1), grid)


def part2(data: list[str]) -> Any:
    grid = build_grid(data)
    start = find_all_pos(grid, "S")[0]
    end = find_all_pos(grid, "E")[0]
    return visit_all(start, end, (0, 1), grid)


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
