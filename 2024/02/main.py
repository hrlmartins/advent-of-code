import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def check_all_increasing(report: list[int]) -> bool:
    return all(report[i] < report[i + 1] for i in range(len(report) - 1))


def check_all_decreasing(report: list[int]) -> bool:
    return all(report[i] > report[i + 1] for i in range(len(report) - 1))


def check_correct_dist(report: list[int]) -> bool:
    return all(
        abs(report[i] - report[i + 1]) in range(1, 4) for i in range(len(report) - 1)
    )


def is_valid_report(report: list[int]) -> bool:
    return (
        check_all_increasing(report) or check_all_decreasing(report)
    ) and check_correct_dist(report)


def remove_by_index(lst: list, index: int) -> list:
    return lst[:index] + lst[index + 1:]


def part1(data: list[str]) -> Any:
    # list of list of int
    reports = [list(map(int, line.split())) for line in data]
    return sum(is_valid_report(report_line) for report_line in reports)


def part2(data: list[str]) -> Any:
    # list of list of int
    reports = [list(map(int, line.split())) for line in data]
    count = 0
    for report in reports:
        if is_valid_report(report):
            count += 1
        else:
            for r_value_idx, _ in enumerate(report):
                tmp_report = remove_by_index(report, r_value_idx)
                if is_valid_report(tmp_report):
                    count += 1
                    break
    return count


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
