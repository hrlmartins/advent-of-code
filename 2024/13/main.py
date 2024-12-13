import sys
from typing import Any
import re


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

def part1(data: list[str]) -> Any:
    button_pattern = r"Button [A-Z]: X\+(\d+), Y\+(\d+)"
    prize_pattern = r"Prize: X=(\d+), Y=(\d+)"
    data = [n for n in data if len(n) > 0]
    total_cost = 0
    for i in range(0, len(data), 3):
        a_button = tuple(map(int, re.findall(button_pattern, data[i])[0]))
        b_button = tuple(map(int, re.findall(button_pattern, data[i + 1])[0]))
        prize = tuple(map(int,re.findall(prize_pattern, data[i + 2])[0]))
        total_cost += solve(prize, a_button[0], a_button[1], b_button[0], b_button[1])

    return total_cost


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
