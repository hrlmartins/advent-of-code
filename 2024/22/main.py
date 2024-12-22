import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def process_new_secret(number):
    mod_value = 16777216
    tmp = number * 64
    number = (number ^ tmp) % mod_value

    tmp = number // 32
    number = (number ^ tmp) % mod_value

    tmp = number * 2048
    number = (number ^ tmp) % mod_value

    return number


def sliding_window(change_list, n):
    result = []
    for i in range(len(change_list) - n + 1):
        window = tuple(change_list[i : i + n])
        result.append((i, window))
    return result


def find_sequence(change_list, seq):
    seq_len = len(seq)
    for i in range(len(change_list) - seq_len + 1):
        if change_list[i : (i + seq_len)] == seq:
            return i
    return -1


def part1(data: list[str]) -> Any:
    s_numbers = [int(v) for v in data]
    values = []
    for s in s_numbers:
        for _ in range(2000):
            s = process_new_secret(s)
        values.append(s)

    return sum(values)


def part2(data: list[str]) -> Any:
    s_numbers = [int(v) for v in data]
    change_values = []
    for s in s_numbers:
        buyer_change = []
        for _ in range(2000):
            current_l_digit = s % 10
            tmp = process_new_secret(s)
            s = tmp
            tmp %= 10  # last digit
            # (price, diff_previous_secret) )
            buyer_change.append((tmp, tmp - current_l_digit))
        change_values.append(buyer_change)

    prices = {}
    for changes in change_values:
        diff_list = [v[1] for v in changes]
        visited = set()
        for i, window in sliding_window(diff_list, 4):
            if window not in visited:
                visited.add(window)
                if window not in prices:
                    prices[window] = 0
                prices[window] += changes[i + 3][0]

    return max(v for v in prices.values())


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
