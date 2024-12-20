from typing import Any


def parse_input(input_data: str) -> list[str]:
    """
    Parse the raw input from stdin into a list of strings.
    """
    return [line.strip() for line in input_data.strip().splitlines()]


def get_combo_operand_value(value, registers):
    match value:
        case 0 | 1 | 3:
            return value
        case 4:
            return registers["A"]
        case 5:
            return registers["B"]
        case 6:
            return registers["C"]
        case _:
            # its a bug!!
            return -1


def run_operation(program, pointer, registers, instruction):
    combo_operand = get_combo_operand_value(program[pointer + 1], registers)
    literal_operand = program[pointer + 1]
    match instruction:
        case 0:
            registers["A"] //= 2**combo_operand
        case 1:
            registers["B"] ^= program[pointer + 1]
        case 2:
            registers["B"] = combo_operand % 8
        case 3:
            if registers["A"] != 0:
                return literal_operand
        case 4:
            registers["B"] ^= registers["C"]
        case 5:
            registers["OUT"].append(combo_operand % 8)
        case 6:
            registers["B"] = registers["A"] // 2**combo_operand
        case 7:
            registers["C"] = registers["A"] // 2**combo_operand
        case _:
            # END PROGRAM!!!!
            return len(program)

    return pointer + 2


def find_solution(a_value, program, prog_idx):
    if prog_idx == 16:
        # hack to not make the number too big since we shifted before comming here :D
        return a_value >> 3

    registers = {"A": 0, "B": 0, "C": 0, "OUT": []}

    for j in range(0, 8):
        pointer = 0
        registers["A"] = a_value | j
        registers["B"] = 0
        registers["C"] = 0
        registers["OUT"] = []
        while pointer < len(program):
            correct_program = list(reversed(program))
            pointer = run_operation(
                correct_program, pointer, registers, correct_program[pointer]
            )
        if registers["OUT"][0] == program[prog_idx]:
            res = find_solution((a_value | j) << 3, program, prog_idx + 1)
            if res > -1:
                return res

    return -1


def part1(a) -> Any:
    registers = {"A": a, "B": 0, "C": 0, "OUT": []}
    pointer = 0
    program = [2, 4, 1, 5, 7, 5, 1, 6, 0, 3, 4, 6, 5, 5, 3, 0]

    while pointer < len(program):
        pointer = run_operation(program, pointer, registers, program[pointer])

    return registers["OUT"]


def part2(data: list[str]) -> Any:
    program = [2, 4, 1, 5, 7, 5, 1, 6, 0, 3, 4, 6, 5, 5, 3, 0]
    a_value = find_solution(0, list(reversed(program)), 0)
    return a_value


def main() -> None:
    result1 = part1(51064159)
    print(f"Part 1: {result1}")
    result2 = part2([])
    print(f"Part 2: {result2}")


if __name__ == "__main__":
    main()
