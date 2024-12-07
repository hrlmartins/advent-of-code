from operator import add, mul
from itertools import product
from re import A, I
import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]

def produce_list_tests(data):
    return [(int(raw.split(":")[0]),list(map(int, raw.split(":")[1].split()))) for raw in data]

def generate_combinations(operators, size):
    return list(product(operators, repeat=size))

def join_integers(first, second):
    return int(str(first) + str(second))

def apply_operation(operands, operators, fun_map):
    result = operands[0]
    for idx, operator in enumerate(operators):
        result = fun_map[operator](result, operands[idx + 1])

    return result

def produce_set_of_matching_tests(test_list, operators, fun_map):
    return {
        expected
        for expected, operands in test_list
        for combination in generate_combinations(operators, len(operands) - 1)
        if apply_operation(operands, combination, fun_map) == expected
    }


def part1(data: list[str]) -> Any:
    test_raw = produce_list_tests(data)
    operators = ["+", "*"]
    fun_map = {
        "+": add,
        "*": mul
    }

    sum_set = set()
    for expected, operands in test_raw:
        operators_combinations = generate_combinations(operators, len(operands) - 1)
        for combination in operators_combinations:
            result = operands[0]
            for idx, operator in enumerate(combination):
                result = fun_map[operator](result, operands[idx + 1])
            if result == expected:
                sum_set.add(expected)

    return sum(value for value in sum_set)


def part2(data: list[str]) -> Any:
    test_raw = produce_list_tests(data)
    operators = ["+", "*", "||"]
    fun_map = {
        "+": add,
        "*": mul,
        "||": join_integers
    }

    sum_set = set()
    for expected, operands in test_raw:
        operators_combinations = generate_combinations(operators, len(operands) - 1)
        for combination in operators_combinations:
            result = operands[0]
            for idx, operator in enumerate(combination):
                result = fun_map[operator](result, operands[idx + 1])
            if result == expected:
                sum_set.add(expected)

    return sum(value for value in sum_set)

def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
