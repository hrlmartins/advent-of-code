import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def has_solution_count(towels, design, solution, mem):
    if len(design) == 0:
        return 1

    if design in mem:
        return mem[design]

    mem[design] = 0
    for ln in range(1, len(design) + 1):
        match_prefix = design[:ln]
        if match_prefix in towels:
            solvable = has_solution_count(
                towels, design[ln:], solution + match_prefix, mem
            )
            if solvable > 0:
                mem[design] += solvable

    return mem[design]


def part1(data: list[str]) -> Any:
    towels = [t.strip() for t in data[0].split(",")]
    designs = data[2:]
    towels_set = set(towels)
    mem = {}
    return sum(
        has_solution_count(towels_set, design, "", mem) > 0 for design in designs
    )


def part2(data: list[str]) -> Any:
    towels = [t.strip() for t in data[0].split(",")]
    designs = data[2:]
    towels_set = set(towels)
    mem = {}
    return sum(has_solution_count(towels_set, design, "", mem) for design in designs)


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
