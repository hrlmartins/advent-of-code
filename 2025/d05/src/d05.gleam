import gleam/int
import gleam/io
import gleam/list
import gleam/regexp
import gleam/result
import gleam/set
import gleam/string
import in

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
//
pub type Range {
  Range(min: Int, max: Int)
}

pub fn main() {
  let assert Ok(input) = read_all_lines()
  let split_idx =
    input
    |> list.index_fold(-1, fn(acc, x, idx) {
      case x == "" {
        True -> idx
        False -> acc
      }
    })

  let #(ranges, ingredients) = input |> list.split(split_idx)
  let ingredients =
    ingredients
    |> list.filter(fn(x) { x != "" })
    |> list.map(fn(x) { int.parse(x) |> result.unwrap(-10) })
  let ranges =
    ranges
    |> list.map(fn(x) {
      let raw_range = string.split(x, "-")
      let min =
        raw_range
        |> list.first
        |> result.unwrap("")
        |> int.parse
        |> result.unwrap(-10)
      let max =
        raw_range
        |> list.last
        |> result.unwrap("")
        |> int.parse
        |> result.unwrap(-10)

      Range(min, max)
    })
  io.println("== Part 1 ==")
  echo part1(ranges, ingredients)

  io.println("== Part 2 ==")
  echo part2(ranges, ingredients)
}

fn part1(ranges: List(Range), ingredients: List(Int)) {
  ingredients
  |> list.count(fn(ingredient) {
    ranges
    |> list.any(fn(range) { ingredient >= range.min && ingredient <= range.max })
  })
}

fn part2(ranges: List(Range), ingredients: List(Int)) {
  let all_ranges = loop(ranges)

  all_ranges
  |> list.fold(0, fn(acc, range) { acc + { range.max - range.min + 1 } })
}

fn zip_it(ranges: List(Range)) -> List(Range) {
  let all_ranges =
    ranges
    |> list.fold(
      [ranges |> list.first |> result.unwrap(Range(-1, -1))],
      fn(acc: List(Range), range: Range) {
        case acc |> list.any(fn(e_range) { overlaps(e_range, range) }) {
          False -> [range, ..acc]
          True -> {
            // lets update the overlap  
            acc
            |> list.map(fn(e_range) {
              case overlaps(e_range, range) {
                True -> merge(e_range, range)
                False -> e_range
              }
            })
          }
        }
      },
    )
  all_ranges
}

fn loop(ranges: List(Range)) {
  let old_size = ranges |> list.length
  let new_ranges = zip_it(ranges)
  let new_size = new_ranges |> list.length

  case old_size > new_size {
    True -> loop(new_ranges)
    False -> new_ranges
  }
}

fn merge(e_range: Range, range: Range) -> Range {
  let Range(a_min, a_max) = e_range
  let Range(b_min, b_max) = range

  let new_min = int.min(a_min, b_min)
  let new_max = int.max(a_max, b_max)

  Range(min: new_min, max: new_max)
}

fn overlaps(a: Range, b: Range) -> Bool {
  let Range(a_min, a_max) = a
  let Range(b_min, b_max) = b

  a_min <= b_max && b_min <= a_max
}
