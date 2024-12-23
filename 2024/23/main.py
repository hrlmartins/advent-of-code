import itertools
import sys
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def build_graph(data):
    graph = {}
    for line in data:
        a, b = line.split("-")
        if a not in graph:
            graph[a] = []
        if b not in graph:
            graph[b] = []

        graph[a].append(b)
        graph[b].append(a)
    return graph


def iterate_groups(lst, group_sizes):
    for size in group_sizes:
        for combination in itertools.combinations(lst, size):
            yield list(combination)


def part1(data: list[str]) -> Any:
    graph = build_graph(data)

    result = set()
    for k, adj_list in graph.items():
        if not k.startswith("t"):
            continue

        for i in range(len(adj_list) - 1):
            for j in range(i + 1, len(adj_list)):
                b, c = adj_list[i], adj_list[j]
                if c in graph[b]:
                    result.add(tuple(sorted((k, b, c))))
    return len(result)


def part2(data: list[str]) -> Any:
    graph = build_graph(data)

    result = set()
    for k, adj_list in graph.items():

        for comb in iterate_groups(adj_list, list(range(2, len(adj_list) + 1))):
            if all(
                comb[k] in graph[comb[l]]
                for k in range(len(comb) - 1)
                for l in range(k + 1, len(comb))
            ):
                result.add(tuple(sorted(comb + [k])))

    return ",".join(max(result, key=len))


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
