import re
import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def solve(prize, dax, day, dbx, dby):
    # at most 100
    cost = (prize[0] + prize[1]) * 100000000000000
    has_sol = False
    for astep in range(1, 101):
        for bstep in range(1, 101):
            v_x = dax * astep + dbx * bstep
            v_y = day * astep + dby * bstep
            if v_x == prize[0] and v_y == prize[1]:
                has_sol = True
                cost = min(cost, (astep * 3) + (bstep * 1))

    return cost if has_sol else 0


def solve_fast(prize, dax, day, dbx, dby):
    tx, ty = prize
    b_presses = (ty * dax - tx * day) / (-dbx * day + dax * dby)
    a_presses = (tx - b_presses * dbx) / dax

    if not a_presses.is_integer() or not b_presses.is_integer():
        return 0
    else:
        return int(a_presses * 3 + b_presses * 1)


def part1(data: list[str]) -> Any:
    button_pattern = r"Button [A-Z]: X\+(\d+), Y\+(\d+)"
    prize_pattern = r"Prize: X=(\d+), Y=(\d+)"
    data = [n for n in data if len(n) > 0]
    total_cost = 0
    for i in range(0, len(data), 3):
        a_button = tuple(map(int, re.findall(button_pattern, data[i])[0]))
        b_button = tuple(map(int, re.findall(button_pattern, data[i + 1])[0]))
        prize = tuple(map(int, re.findall(prize_pattern, data[i + 2])[0]))
        total_cost += solve_fast(
            prize, a_button[0], a_button[1], b_button[0], b_button[1]
        )

    return total_cost


def part2(data: list[str]) -> Any:
    button_pattern = r"Button [A-Z]: X\+(\d+), Y\+(\d+)"
    prize_pattern = r"Prize: X=(\d+), Y=(\d+)"
    data = [n for n in data if len(n) > 0]
    total_cost = 0
    for i in range(0, len(data), 3):
        a_button = tuple(map(int, re.findall(button_pattern, data[i])[0]))
        b_button = tuple(map(int, re.findall(button_pattern, data[i + 1])[0]))
        prize = tuple(map(int, re.findall(prize_pattern, data[i + 2])[0]))
        prize = (prize[0] + 10000000000000, prize[1] + 10000000000000)
        total_cost += solve_fast(
            prize, a_button[0], a_button[1], b_button[0], b_button[1]
        )

    return total_cost


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
