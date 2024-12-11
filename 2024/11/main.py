import sys
from collections import defaultdict
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def resolve(stone, mem, iter, total_iterations) -> int:
    if iter == total_iterations:
        return 1

    if (iter, stone) not in mem:
        stone_str = str(stone)
        if stone == 0:
            mem[(iter, stone)] = resolve(1, mem, iter + 1, total_iterations)
        elif len(stone_str) % 2 == 0:
            mid = len(stone_str) // 2
            mem[(iter, stone)] = (
                resolve(int(stone_str[:mid]), mem, iter + 1, total_iterations) +
                resolve(int(stone_str[mid:]), mem, iter + 1, total_iterations)
            )
        else:
            mem[(iter, stone)] = resolve(stone * 2024, mem, iter + 1, total_iterations)

    return mem[(iter, stone)]


def part1(data: list[str]) -> Any:
    init_stones = list(map(int, data[0].split()))
    mem = defaultdict()
    return sum(resolve(stone, mem, 0, 25) for stone in init_stones)


def part2(data: list[str]) -> Any:
    init_stones = list(map(int, data[0].split()))
    mem = defaultdict()
    return sum(resolve(stone, mem, 0, 75) for stone in init_stones)


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
