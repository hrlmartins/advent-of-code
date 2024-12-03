import re
import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def all_mul_matches(whole_str: str):
    pattern_muls = r"mul\((\d+),(\d+)\)"
    return re.findall(pattern_muls, whole_str)


def part1(data: list[str]) -> Any:
    whole_str = "".join(data)
    matches = all_mul_matches(whole_str)
    return sum(int(first) * int(second) for first, second in matches)


def part2(data: list[str]) -> Any:
    whole_str = "".join(data)
    # all matches between dont and do
    pattern = r"don't\(\).*?do\(\)"
    filtered_string = re.sub(pattern, "", whole_str, flags=re.DOTALL)
    matches = all_mul_matches(filtered_string)
    return sum(int(first) * int(second) for first, second in matches)


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
