import gleam/float
import gleam/int
import gleam/io
import gleam/list
import gleam/pair
import gleam/regexp
import gleam/result
import gleam/string
import in

// ===================================================== Helper functions

pub fn read_all_lines_concatenate() -> String {
  let assert Ok(all) = read_loop([])
  string.concat(all)
}

pub fn read_all_lines() -> Result(List(String), eof) {
  read_loop([])
}

fn read_loop(acc: List(String)) -> Result(List(String), eof) {
  case in.read_line() {
    Ok(line) -> read_loop([line, ..acc])
    Error(_) -> Ok(list.reverse(acc))
  }
}

pub fn ints_from_line(line: String) -> List(Int) {
  let assert Ok(re) = regexp.from_string("-?\\d+")
  regexp.scan(re, line)
  |> list.map(fn(m) {
    m.content
    |> int.parse
    |> result.unwrap(0)
  })
}

// ======================================================= end helpers
pub type Command {
  Command(direction: String, value: Int)
}

pub fn parse_command(s: String) {
  let direction: String = result.unwrap(string.first(s), "")
  let number_str = string.drop_end(s, 1) |> string.drop_start(1)

  let value: Int = result.unwrap(int.parse(number_str), 0)

  Command(direction, value)
}

pub fn main() {
  let assert Ok(input) = read_all_lines()

  let directions = input |> list.map(parse_command)

  io.println("== Part 1 ==")
  echo part1(directions)

  io.println("== Part 2 ==")
  echo part2(directions)
}

fn part1(directions) {
  let initial = #(50, 0)

  directions
  |> list.fold(initial, fn(acc, dir: Command) {
    let #(pos, zero_count) = acc
    let new_pos = case dir.direction {
      "L" -> {
        { pos - dir.value } % 100
      }
      "R" -> {
        { pos + dir.value } % 100
      }
      _ -> pos
    }

    case new_pos {
      0 -> #(new_pos, zero_count + 1)
      _ -> #(new_pos, zero_count)
    }
  })
  |> pair.second
}

fn part2(directions) {
  let initial = #(50, 0)

  directions
  |> list.fold(initial, fn(acc, dir: Command) {
    let #(pos, zero_count) = acc

    let delta = case dir.direction {
      "L" -> {
        -dir.value
      }
      "R" -> {
        dir.value
      }
      _ -> 0
    }

    let raw = pos + delta
    let new_pos = result.unwrap(int.modulo(raw, 100), 0)

    let remainder = int.modulo(dir.value, 100) |> result.unwrap(0)
    let turns = int.floor_divide(dir.value, 100) |> result.unwrap(0)

    let extra_crossed = case delta >= 0 {
      True ->
        case pos + remainder > 100 || new_pos == 0 {
          True -> 1
          False -> 0
        }
      False ->
        case { pos - remainder < 0 && pos != 0 } || new_pos == 0 {
          True -> 1
          False -> 0
        }
    }

    let crossed = int.absolute_value(turns) + extra_crossed

    #(new_pos, zero_count + crossed)
  })
  |> pair.second
}
