import gleam/int
import gleam/io
import gleam/list
import gleam/pair
import gleam/regexp
import gleam/result
import gleam/string
import in

// ===================================================== Helper functions

fn read_all_lines_concatenate() -> String {
  let assert Ok(all) = read_loop([])
  string.concat(all)
}

fn read_all_lines() -> Result(List(String), eof) {
  read_loop([])
}

fn read_loop(acc: List(String)) -> Result(List(String), eof) {
  case in.read_line() {
    Ok(line) -> read_loop([line |> string.trim, ..acc])
    Error(_) -> Ok(list.reverse(acc))
  }
}

fn ints_from_line(line: String) -> List(Int) {
  let assert Ok(re) = regexp.from_string("-?\\d+")
  regexp.scan(re, line)
  |> list.map(fn(m) {
    m.content
    |> int.parse
    |> result.unwrap(0)
  })
}

// ======================================================= end Helper

pub fn has_repeating_block(digits: List(Int), exactly_two: Bool) {
  let len = list.length(digits)

  let possible_sizes =
    list.range(1, len)
    |> list.filter(fn(size) { len % size == 0 })
    |> list.reverse

  list.any(possible_sizes, fn(repeat_size) {
    let times = len / repeat_size
    case { !exactly_two && times >= 2 } || { exactly_two && times == 2 } {
      True -> {
        let prefix = list.take(digits, repeat_size)
        let repeated = repeat_list(prefix, times)
        repeated == digits
      }
      False -> False
    }
  })
}

fn repeat_list(xs: List(Int), times: Int) {
  case times {
    0 -> []
    _ -> list.append(xs, repeat_list(xs, times - 1))
  }
}

fn calc_range_invalid(range: List(Int), exactly_two: Bool) {
  let start = range |> list.first |> result.unwrap(-1)
  let end = range |> list.reverse |> list.first |> result.unwrap(-1)

  list.range(start, end)
  |> list.fold(0, fn(acc, value) {
    let digits = value |> int.digits(10) |> result.unwrap([])

    case has_repeating_block(digits, exactly_two) {
      True -> {
        acc + value
      }
      False -> acc
    }
  })
}

pub fn main() {
  let assert Ok(input) = read_all_lines()
  let range_list =
    input
    |> list.flat_map(fn(line: String) { line |> string.split(",") })
    |> list.map(fn(code) {
      code
      |> string.split("-")
      |> list.map(fn(x) { x |> int.parse |> result.unwrap(0) })
    })

  io.println("== Part 1 ==")
  echo part1(range_list)

  io.println("== Part 2 ==")
  echo part2(range_list)
}

fn part1(range_list: List(List(Int))) {
  range_list
  |> list.fold(0, fn(acc, range) { acc + calc_range_invalid(range, True) })
}

fn part2(range_list: List(List(Int))) {
  range_list
  |> list.fold(0, fn(acc, range) { acc + calc_range_invalid(range, False) })
}
