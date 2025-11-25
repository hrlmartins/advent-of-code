import gleam/int
import gleam/io
import gleam/list
import gleam/regexp
import gleam/result
import gleam/string
import in

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

pub fn main() {
  let assert Ok(input) = read_all_lines()

  let value_list: List(List(Int)) = input |> list.map(ints_from_line)

  io.println("== Part 1 ==")
  echo part1(value_list)

  io.println("== Part 2 ==")
  echo part2(value_list)
}

fn is_safe(report: List(Int)) -> Bool {
  {
    report
    |> list.window_by_2
    |> list.all(fn(pair) { pair.0 > pair.1 })
    || report
    |> list.window_by_2
    |> list.all(fn(pair) { pair.0 < pair.1 })
  }
  && report
  |> list.window_by_2
  |> list.all(fn(pair) {
    let tmp = int.absolute_value(pair.0 - pair.1)
    tmp >= 1 && tmp <= 3
  })
}

pub fn remove_at(xs: List(Int), i: Int) -> List(Int) {
  let left = list.take(xs, i)
  let right = list.drop(xs, i + 1)
  list.append(left, right)
}

fn part1(pairs: List(List(Int))) -> Int {
  pairs
  |> list.count(fn(report) { is_safe(report) })
}

fn part2(pairs: List(List(Int))) -> Int {
  pairs
  |> list.count(fn(report) {
    let is_safe_raw = is_safe(report)

    case is_safe_raw {
      True -> True
      False -> {
        let len = report |> list.length

        list.range(0, len - 1)
        |> list.any(fn(idx) {
          remove_at(report, idx)
          |> is_safe
        })
      }
    }
  })
}
