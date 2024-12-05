import sys
from collections import defaultdict
from typing import Any


# not used today
def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def produce_edges_updates(data: str) -> tuple[list[tuple[int, ...]], list[list[int]]]:
    order_command = data.split("\n\n")
    edges_raw = order_command[0]
    updates_raw = order_command[1]
    edges = [tuple(map(int, line.split("|"))) for line in edges_raw.splitlines()]
    updates = [list(map(int, line.split(","))) for line in updates_raw.splitlines()]

    return (edges, updates)


def build_graph(edges: list[tuple[int, ...]], considered_nodes: list[int]):
    graph = defaultdict(list)
    in_degree = defaultdict(int)

    for start, end in edges:
        if start not in considered_nodes:
            continue
        graph[start].append(end)
        in_degree[end] += 1
        if start not in in_degree:
            in_degree[start] = 0

    return (graph, in_degree)


def separate_valid_invalid(edges, updates):
    valid_ar = []
    invalid_ar = []
    for update in updates:
        graph, in_degree = build_graph(edges, update)
        valid = True
        for node in update:
            if in_degree[node] == 0:
                for neighbor in graph[node]:
                    if neighbor in in_degree:
                        in_degree[neighbor] -= 1
            else:
                valid = False
        if valid:
            valid_ar.append(update)
        else:
            invalid_ar.append(update)
    return (valid_ar, invalid_ar)


def part1(data: str) -> Any:
    edges, updates = produce_edges_updates(data)
    valid, _ = separate_valid_invalid(edges, updates)
    return sum(update[int((len(update) - 1) / 2)] for update in valid)


def part2(data: str) -> Any:
    edges, updates = produce_edges_updates(data)
    _, invalid = separate_valid_invalid(edges, updates)

    sum = 0
    for invalid_update in invalid:
        graph, in_degree = build_graph(edges, invalid_update)
        proper_ordered = []
        while len(proper_ordered) < len(invalid_update):
            for node in invalid_update:
                if in_degree[node] == 0 and node not in proper_ordered:
                    proper_ordered.append(node)
                    for neighbor in graph[node]:
                        if neighbor in in_degree:
                            in_degree[neighbor] -= 1

        sum += proper_ordered[int((len(proper_ordered) - 1) / 2)]
    return sum


def main() -> None:
    input_data = sys.stdin.read().strip()
    data = input_data
    data = "".join(data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
