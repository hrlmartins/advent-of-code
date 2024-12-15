import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def build_data(data):
    sep_idx = data.index("")
    grid_raw = data[:sep_idx]
    commands = list("".join(data[sep_idx + 1:]))
    grid = [list(row) for row in grid_raw]

    return (grid, commands)


def find_all_pos(grid, element):
    all = []
    for row, data in enumerate(grid):
        for col, val in enumerate(data):
            if val == element:
                all.append((row, col))

    return all


def direction_of(command):
    match command:
        case "^":
            return (-1, 0)
        case "<":
            return (0, -1)
        case ">":
            return (0, 1)
        case _:
            return (1, 0)


def in_bounds(grid, position):
    (row, col) = position
    return grid[row][col] != "#"


def move_tuple(pos, dir):
    return (pos[0] + dir[0], pos[1] + dir[1])


def flat_map(lst, func):
    return [item for sublist in lst for item in func(sublist)]


def transform(tile):
    match tile:
        case "#":
            return ["#", "#"]
        case "O":
            return ["O", "O"]
        case ".":
            return [".", "."]
        case _:
            return ["@", "."]


def visit_boxes(ind_boxes, start_pos, big_boxes, dir, part2=False):
    visited = set()
    to_visit = [start_pos]

    while to_visit:
        node = to_visit.pop()
        if node in visited:
            continue
        visited.add(node)
        if not part2:
            new_n = move_tuple(node, dir)
            if new_n in ind_boxes:
                to_visit.append(new_n)
        else:
            hit_boxes = flat_map(
                [b for b in big_boxes if set(
                    list(b)) & set([node])], lambda x: x
            )
            to_visit.append(hit_boxes[0])
            to_visit.append(hit_boxes[1])
            new_n1 = move_tuple(hit_boxes[0], dir)
            new_n2 = move_tuple(hit_boxes[1], dir)
            if new_n1 in ind_boxes:
                to_visit.append(new_n1)
            if new_n2 in ind_boxes:
                to_visit.append(new_n2)

    return visited


def part1(data: list[str]) -> Any:
    grid, commands = build_data(data)
    bot_position = find_all_pos(grid, "@")[0]
    boxes_positions = set(find_all_pos(grid, "O"))

    for command in commands:
        dir = direction_of(command)
        next_bot_pos = move_tuple(bot_position, dir)
        if next_bot_pos in boxes_positions:
            boxes = visit_boxes(boxes_positions, next_bot_pos, [], dir)
            if all(in_bounds(grid, move_tuple(pos, dir)) for pos in boxes):
                boxes_positions.difference_update(boxes)
                new_b_positions = set(map(lambda x: move_tuple(x, dir), boxes))
                boxes_positions |= new_b_positions
                bot_position = next_bot_pos
        elif in_bounds(grid, next_bot_pos):
            bot_position = next_bot_pos

    return sum(100 * box[0] + box[1] for box in boxes_positions)


def part2(data: list[str]) -> Any:
    grid, commands = build_data(data)
    for i, row in enumerate(grid):
        grid[i] = flat_map(list(map(lambda x: transform(x), row)), lambda x: x)

    bot_position = find_all_pos(grid, "@")[0]
    simple_hit_map = find_all_pos(grid, "O")

    # each tuple now has four values ((start_row, start col), (end row, end col))
    boxes_positions = set(
        [
            (simple_hit_map[i - 1], simple_hit_map[i])
            for i in range(1, len(simple_hit_map), 2)
        ]
    )
    individual_s_boxes = set(simple_hit_map)

    for command in commands:
        dir = direction_of(command)
        next_bot_pos = move_tuple(bot_position, dir)
        if next_bot_pos in individual_s_boxes:
            boxes = visit_boxes(
                individual_s_boxes, next_bot_pos, boxes_positions, dir, True
            )

            hit_boxes = set(
                [b for b in boxes_positions if set(list(b)) & set(boxes)])

            if all(in_bounds(grid, move_tuple(pos, dir)) for pos in boxes):
                individual_s_boxes.difference_update(boxes)
                boxes_positions.difference_update(hit_boxes)

                new_b_positions = set(
                    map(
                        lambda x: (move_tuple(x[0], dir),
                                   move_tuple(x[1], dir)),
                        hit_boxes,
                    )
                )

                boxes_positions |= new_b_positions
                individual_s_boxes |= set(
                    flat_map(new_b_positions, lambda x: x))
                bot_position = next_bot_pos
        elif in_bounds(grid, next_bot_pos):
            bot_position = next_bot_pos

    return sum(100 * min(box)[0] + min(box)[1] for box in boxes_positions)


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
