import sys
from collections import defaultdict
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def directions():
    return [
        (0, 1),
        (1, 0),
        (-1, 0),
        (0, -1),
    ]


def find_all_index(area, crop):
    return [
        (row, col)
        for row, r_list in enumerate(area)
        for col, value in enumerate(r_list)
        if value == crop
    ]


def is_in_bounds(area, position):
    rows = len(area)
    cols = len(area[0]) if rows > 0 else 0
    row, col = position

    return 0 <= row < rows and 0 <= col < cols


def create_regions(area, known_crops):
    regions = defaultdict(list)
    region_id = 0
    for crop in known_crops:
        visited = set()
        all_pos = find_all_index(area, crop)
        region_tmp = []
        for pos in all_pos:
            if pos in visited:
                continue
            to_visit = [pos]
            while to_visit:
                node = to_visit.pop()
                if node not in visited:
                    visited.add(node)
                    region_tmp.append(node)
                    cur_r, cur_c = node
                    for dr, dc in directions():
                        next_r, next_c = cur_r + dr, cur_c + dc
                        if (
                            is_in_bounds(area, (next_r, next_c))
                            and area[next_r][next_c] == crop
                        ):
                            to_visit.append((next_r, next_c))
            # found a region at this point
            regions[(region_id, crop)] = region_tmp.copy()
            region_id += 1
            region_tmp = []

    return regions


def calc_perimeter(farm_area, region, crop):
    count = 0
    for pos in region:
        for dr, dc in directions():
            next_r, next_c = pos[0] + dr, pos[1] + dc
            if (
                not is_in_bounds(farm_area, (next_r, next_c))
                or farm_area[next_r][next_c] != crop
            ):
                count += 1

    return count


def dedup_list(values):
    return [
        values[i] for i in range(len(values)) if i == 0 or values[i] != values[i - 1]
    ]


def calc_perimeter_line(farm_area, region, crop):
    min_row = min(v[0] for v in region) - 1
    max_row = max(v[0] for v in region) + 2
    min_col = min(v[1] for v in region) - 1
    max_col = max(v[1] for v in region) + 2

    count = 0
    for row in range(min_row, max_row):
        going_in = []
        going_out = []
        for col in range(min_col, max_col):
            current = (row, col)
            look_ahead = (row + 1, col)
            if (current not in region) and (look_ahead in region):
                going_in.append(True)
                going_out.append(False)
            elif (current in region) and (look_ahead not in region):
                going_out.append(True)
                going_in.append(False)
            else:
                going_in.append(False)
                going_out.append(False)
        count += len([v for v in dedup_list(going_in) if v])
        count += len([v for v in dedup_list(going_out) if v])

    for col in range(min_col, max_col):
        going_in = []
        going_out = []
        for row in range(min_row, max_row):
            current = (row, col)
            look_ahead = (row, col + 1)
            if (current not in region) and (look_ahead in region):
                going_in.append(True)
                going_out.append(False)
            elif (current in region) and (look_ahead not in region):
                going_out.append(True)
                going_in.append(False)
            else:
                going_in.append(False)
                going_out.append(False)
        count += len([v for v in dedup_list(going_in) if v])
        count += len([v for v in dedup_list(going_out) if v])
    return count


def part1(data: list[str]) -> Any:
    farm_area = [list(row) for row in data]
    crops = {char for row in farm_area for char in row}
    regions = create_regions(farm_area, list(crops))
    price = 0
    for r in regions.keys():
        region = regions[r]
        area = len(region)
        perimeter = calc_perimeter(farm_area, region, r[1])
        price += area * perimeter

    return price


def part2(data: list[str]) -> Any:
    farm_area = [list(row) for row in data]
    crops = {char for row in farm_area for char in row}
    regions = create_regions(farm_area, list(crops))
    price = 0
    price_region = defaultdict(int)
    for r in regions.keys():
        region = regions[r]
        area = len(region)
        region_map = set(region)
        perimeter = calc_perimeter_line(farm_area, region_map, r[1])
        if r[1] not in price_region:
            price_region[r[1]] = 0
        price_region[r[1]] += area * perimeter
        price += area * perimeter
    return price


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
