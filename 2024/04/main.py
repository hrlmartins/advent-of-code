from collections import Counter
import sys
from typing import Any

# Do I suspect a trie will work the general problem of this day? :D
LOOKUP_WORD = "XMAS"
LOOKUP_WORD_REV = LOOKUP_WORD[::-1]

def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]

def get_all_directions():
    return [
        (-1, 0),
        (1, 0),
        (0, -1),
        (0, 1),
        (-1, -1),
        (-1, 1),
        (1, -1),
        (1, 1),
    ]

def get_diag_directions():
    return [
        (-1, -1),
        (-1, 1),
        (1, -1),
        (1, 1),
    ]
def is_within_bounds(scramble: list[list[str]], row: int, col:int) -> bool:
    return 0 <= row < len(scramble) and 0 <= col < len(scramble[0])

def build_word(scramble: list[list[str]], row: int, col:int, direction: tuple[int, int], size: int) -> str:
    d_row, d_col = direction
    chars = []
    for step in range(size):
        new_row = row + step * d_row
        new_col = col + step * d_col

        if is_within_bounds(scramble, new_row, new_col):
            chars.append(scramble[new_row][new_col])
        else:
            break

    return "".join(chars)

def calculate_occurrences(scramble, initial_str, size, lookup_word, diag):
    occur = []
    for row in range(len(scramble)):
        for col in range(len(scramble[0])):
            if scramble[row][col] == initial_str:
                dirs = get_all_directions() if not diag else get_diag_directions()
                for dir in dirs:
                    tmp_word = build_word(scramble, row, col, dir, size)
                    if tmp_word == lookup_word or tmp_word == lookup_word[::-1]:
                        occur.append((row, col) if not diag else (row + dir[0], col + dir[1]))
    return occur


def part1(data: list[str]) -> Any:
    word_scramble = [list(row) for row in data]
    occur = calculate_occurrences(word_scramble, "X", 4, "XMAS", False)
    return len(occur)

def part2(data: list[str]) -> Any:
    word_scramble = [list(row) for row in data]
    occur = calculate_occurrences(word_scramble, "M", 3, "MAS", True)
    freq = Counter(occur)
    return sum(1 for count in freq.values() if count >= 2)

def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
