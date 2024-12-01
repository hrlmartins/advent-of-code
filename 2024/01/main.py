import sys
from collections import Counter
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def process_input(data: list[str]) -> (list[int], list[int]):
    pairs_raw = [line.split() for line in data]
    first_set, second_set = zip(*[(int(num1), int(num2))
                                for num1, num2 in pairs_raw])
    return first_set, second_set


def part1(data: list[str]) -> Any:
    first_set, second_set = process_input(data)
    first_ordered_list, second_ordered_list = (
        sorted(list(first_set)),
        sorted(list(second_set)),
    )

    diff_list = [
        abs(num1 - num2) for num1, num2 in zip(first_ordered_list, second_ordered_list)
    ]

    return sum(diff_list)


def part2(data: list[str]) -> Any:
    first_set, second_set = process_input(data)
    frequency = Counter(second_set)
    similarity_score = [num * frequency[num] for num in first_set]

    return sum(similarity_score)


def main() -> None:
    input_data = sys.stdin.read()

    data = parse_input(input_data)

    result1 = part1(data)
    print(f"Part 1: {result1}")

    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
