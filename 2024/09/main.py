import sys
from collections import deque
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def build_fs(raw):
    fs = []
    id = 0
    blocks = []
    for idx, val in enumerate(raw):
        char_to_fill = ""
        if idx % 2 != 0:  # free space blocck
            char_to_fill = "."
        else:  # file block
            char_to_fill = id
            id += 1

        # Metadata info that stores (block, idx firs occur, size of block)
        blocks.append((char_to_fill, len(fs), int(val)))
        for _ in range(int(val)):
            fs.append(char_to_fill)

    return (fs, blocks)


def part1(data: list[str]) -> Any:
    raw = data[0]
    fs, _ = build_fs(raw)
    f_empty_idxs = deque(i for i, val in enumerate(fs) if val == ".")
    for rev_idx in range(len(fs) - 1, -1, -1):
        if rev_idx <= f_empty_idxs[0] or len(f_empty_idxs) == 0:
            # nothing to do here
            break
        item = fs[rev_idx]
        if item != ".":
            f_empty_idx = f_empty_idxs.popleft()
            fs[f_empty_idx], fs[rev_idx] = fs[rev_idx], fs[f_empty_idx]
    return sum(val * idx for idx, val in enumerate(fs) if val != ".")


def part2(data: list[str]) -> Any:
    raw = data[0]
    fs, blocks = build_fs(raw)
    empty_blocks = [block for block in blocks if block[0] == "."]
    file_blocks = deque(block for block in blocks if block[0] != ".")
    while file_blocks:
        b_id, f_idx, size = file_blocks.pop()
        empty_space = next(
            ((i, val) for i, val in enumerate(empty_blocks) if val[2] >= size), None
        )
        if empty_space == None or empty_space[1][1] >= f_idx:
            continue

        e_block_idx = empty_space[0]
        _, e_idx, _ = empty_space[1]

        for i in range(f_idx, f_idx + size):
            fs[e_idx], fs[i] = fs[i], fs[e_idx]
            e_idx += 1

        empty_blocks[e_block_idx] = (
            empty_blocks[e_block_idx][0],
            # updates the empty space start index
            empty_blocks[e_block_idx][1] + size,
            # update the size of the empty space
            empty_blocks[e_block_idx][2] - size,
        )
    return sum(val * idx for idx, val in enumerate(fs) if val != ".")


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
