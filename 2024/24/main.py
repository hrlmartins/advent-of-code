import re
import sys
from collections import deque
from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def xor_operation(x, y, z, reg):
    if x not in reg or y not in reg:
        return False
    reg[z] = reg[x] ^ reg[y]
    return True


def and_operation(x, y, z, reg):
    if x not in reg or y not in reg:
        return False
    reg[z] = reg[x] & reg[y]
    return True


def or_operation(x, y, z, reg):
    if x not in reg or y not in reg:
        return False
    reg[z] = reg[x] | reg[y]
    return True


def validate_sum(carry_var, l_var, operations, depth, max_depth):
    if depth == max_depth:
        return []

    x_var = f"x{depth:02d}"
    y_var = f"y{depth:02d}"
    z_goal = f"z{depth:02d}"
    changes = []

    if depth == 0:
        # only need to calc result and carry
        # x ^ y -> z<_>
        # x & y -> n_c
        # what to check:
        #   - x ^ y -> z<d>
        #   - x & y -> n_c
        #   - |<var> ^ n_c| -> z<d+1>
        #       - the part between || is what we need to find to know n_c is correct
        #       - we also know that <var> is l in the next iteration
        #       - we will correct this in the next call
        key_xor_raw = x_var + "XOR" + y_var + z_goal
        key = "".join(sorted(x_var + "XOR" + y_var + z_goal))
        res = [
            (i, n)
            for i, n in enumerate(operations)
            if "".join(sorted("".join(n))) == key
        ]

        if len(res) < 1:
            print(f"not correct output for: {key_xor_raw}")

        key_carry_raw = x_var + "AND" + y_var
        key_carry = "".join(sorted(key_carry_raw))
        res = [
            (i, n)
            for i, n in enumerate(operations)
            if key_carry in "".join(sorted("".join(n[0:3])))
        ]

        if len(res) < 1:
            print(f"not correct output for: {key_carry_raw}")

        # the carry!!
        carry = res[0][1][3]
        res = [
            (i, n)
            for i, n in enumerate(operations)
            if carry in n and "XOR" in n and f"z{1:02d}" in n
        ]

        # I know my first digit is correct so this one is kinda hardcoded
        # DEAL WITH IT :D
        l_val = [n for n in res[0][1] if n != carry and n != "XOR" and n != f"z{1:02d}"]
        return changes + validate_sum(carry, l_val[0], operations, depth + 1, max_depth)
    else:
        # x ^ y gives us l
        # so  l ^ <previous_carry> -> z<_>
        # x & y gives us the carry k.
        # what to check:
        #   - x ^ y -> l
        #   - <previous_carry> ^ l -> z<d>
        #       - we can get l
        #    - check l & <previous_carry> -> t_c
        #    - check x & y -> k
        #    - check k | t_c -> n_c
        #    - check n_c ^ <var> -> z<d+1>

        # checking x ^ y -> l
        res = [
            n
            for n in operations
            if y_var in n and x_var in n and "XOR" in n and l_var in n
        ]

        if len(res) != 1:
            print(f"l_var not correct for depth {depth} and l {l_var}")

            res = [(i, n) for i, n in enumerate(operations) if l_var == n[3]]
            res2 = [
                (i, n)
                for i, n in enumerate(operations)
                if y_var in n and x_var in n and "XOR" in n
            ]
            operations[res[0][0]] = (
                res[0][1][0],
                res[0][1][1],
                res[0][1][2],
                res2[0][1][3],
            )
            operations[res2[0][0]] = (
                res2[0][1][0],
                res2[0][1][1],
                res2[0][1][2],
                res[0][1][3],
            )
            changes += [res2[0][1][3], res[0][1][3]]

        # l is correct here for sure
        # checking previous_carry ^ l -> z<d>
        res = [
            n
            for n in operations
            if carry_var in n and l_var in n and "XOR" in n and z_goal in n
        ]

        if len(res) != 1:
            # probably look for all ops having the l_var.. shouldnt be many
            print(
                f"p_carry {carry_var} or out {
                    z_goal} not correct for depth {depth}"
            )
            res = [
                (i, n)
                for i, n in enumerate(operations)
                if l_var in n and "XOR" in n and carry_var in n
            ]
            res2 = [(i, n) for i, n in enumerate(operations) if n[3] == z_goal]
            operations[res[0][0]] = (
                res[0][1][0],
                res[0][1][1],
                res[0][1][2],
                res2[0][1][3],
            )
            operations[res2[0][0]] = (
                res2[0][1][0],
                res2[0][1][1],
                res2[0][1][2],
                res[0][1][3],
            )
            changes += [res2[0][1][3], res[0][1][3]]

        # correct: l, previous_carry
        # extract tmp carry from second half adder
        res = [n for n in operations if l_var in n and "AND" in n and carry_var in n]
        # assume correct
        t_c = res[0][3]

        # assume correct, extract k
        res = [n for n in operations if x_var in n and "AND" in n and y_var in n]
        k = res[0][3]

        # extract n_c
        res = [n for n in operations if k in n and "OR" in n and t_c in n]

        if len(res) != 1:
            print(f"k {k} or t_c {t_c} not correct for depth {depth}")
            return []

        # assume correct
        n_c = res[0][3]

        if max_depth - 1 == depth:
            if n_c != f"z{max_depth}":
                print("n_c is not the correct overflow")
            # n_c is the overflow so it must be the last result digit
            return changes + validate_sum(n_c, "", operations, depth + 1, max_depth)

        # correct: l, previouscarry, k, tc
        # extract n_c ^ <var> for z+1. assert(n_c)
        res = [n for n in operations if n_c in n and "XOR" in n]

        if len(res) != 1:
            print(f"n_c {n_c} not correct for depth {depth}")
            # did not happen in my case. This case is NOT solved :D
            return []

        # correct l, previouscarry, k, tc, n_c
        next_l_val = [n for n in res[0][:3] if n != n_c and n != "XOR"]

        return changes + validate_sum(
            n_c, next_l_val[0], operations, depth + 1, max_depth
        )


def get_num(bin_list, registers):
    bin_num = ""
    for k in bin_list:
        bin_num += str(registers[k])

    return int(bin_num, 2)


def part1(data: list[str]) -> Any:
    sep_index = data.index("")
    registers = {}
    operations = []

    for line in data[:sep_index]:
        reg, val = line.split(":")
        registers[reg] = int(val.strip())

    operation_map = {
        "XOR": xor_operation,
        "AND": and_operation,
        "OR": or_operation,
    }

    pattern = r"(\w+)\s+(\w+)\s+(\w+)\s*->\s*(\w+)"
    for line in data[sep_index + 1 :]:
        reg_1, op, reg_2, reg_result = re.match(pattern, line).groups()
        operations.append((reg_1, reg_2, reg_result, operation_map[op]))

    to_visit = deque(operations)
    while to_visit:
        reg1, reg2, reg_res, fun = op = to_visit.popleft()
        if not fun(reg1, reg2, reg_res, registers):
            to_visit.append(op)

    s_keys = [k for k in sorted(registers.keys(), reverse=True) if k.startswith("z")]
    bin_num = ""
    for k in s_keys:
        bin_num += str(registers[k])

    return int(bin_num, 2)


def part2(data: list[str]) -> Any:
    sep_index = data.index("")
    registers = {}
    operations = []

    for line in data[:sep_index]:
        reg, val = line.split(":")
        registers[reg] = int(val.strip())

    max_depth = len(registers) // 2
    pattern = r"(\w+)\s+(\w+)\s+(\w+)\s*->\s*(\w+)"
    for line in data[sep_index + 1:]:
        reg_1, op, reg_2, reg_result = re.match(pattern, line).groups()
        operations.append((reg_1, op, reg_2, reg_result))

    changes = validate_sum("", "", operations, 0, max_depth)

    return ",".join(sorted(changes))


def main() -> None:
    input_data = sys.stdin.read()
    data = parse_input(input_data)
    result1 = part1(data)
    print(f"Part 1: {result1}")
    result2 = part2(data)
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
