import gleam/int
import gleam/io
import gleam/list
import gleam/pair
import gleam/regexp
import gleam/result
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
    Ok(line) -> read_loop([line, ..acc])
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
pub type Column {
  Column(op: String, values: List(Int))
}

fn split_ws(line: String) -> List(String) {
  line
  |> string.split(" ")
  |> list.filter(fn(s) { s != "" })
}

fn nth(xs: List(Int), index: Int) -> Int {
  xs
  |> list.drop(index)
  |> list.first
  |> result.unwrap(0)
}

fn nth_string(xs: List(String), index: Int) -> String {
  xs
  |> list.drop(index)
  |> list.first
  |> result.unwrap("")
}

fn transpose(matrix: List(List(Int))) -> List(List(Int)) {
  case matrix {
    [] -> []
    [first, ..] -> {
      let width = list.length(first)

      list.range(0, width - 1)
      |> list.map(fn(i) {
        matrix
        |> list.map(fn(row) { nth(row, i) })
      })
    }
  }
}

fn transpose_string(matrix: List(List(String))) -> List(List(String)) {
  case matrix {
    [] -> []
    [first, ..] -> {
      let width = list.length(first)

      list.range(0, width - 1)
      |> list.map(fn(i) {
        matrix
        |> list.map(fn(row) { nth_string(row, i) })
      })
    }
  }
}

pub fn parse_columns(lines: List(String)) -> List(Column) {
  let ops_line =
    list.last(lines)
    |> result.unwrap("")

  let number_lines = list.take(lines, { lines |> list.length } - 1)

  let number_matrix =
    number_lines
    |> list.map(fn(line) {
      line
      |> split_ws
      |> list.map(fn(x) { x |> int.parse })
      |> list.map(fn(x) { x |> result.unwrap(-10) })
    })

  let columns = transpose(number_matrix)
  let ops =
    ops_line
    |> split_ws

  list.zip(ops, columns)
  |> list.map(fn(pair) {
    let #(op, vals) = pair
    Column(op: op, values: vals)
  })
}

pub fn main() {
  let assert Ok(input) = read_all_lines()
  let problems = input |> list.map(string.trim) |> parse_columns
  io.println("== Part 1 ==")
  echo part1(problems)

  let input_clean =
    input
    |> list.map(fn(x) {
      string.to_graphemes(x) |> list.filter(fn(y) { y != "\n" })
    })
    |> transpose_string
  io.println("== Part 2 ==")
  echo part2(input_clean)
}

fn part1(problems: List(Column)) {
  problems
  |> list.fold(0, fn(acc, problem) {
    acc
    + case problem.op {
      "+" -> problem.values |> list.fold(0, int.add)
      "*" -> problem.values |> list.fold(1, int.multiply)
      _ -> 0
    }
  })
}

fn part2(problems: List(List(String))) {
  let len_numbers =
    { problems |> list.first |> result.unwrap([]) |> list.length } - 1

  let problems =
    list.append(problems, [
      string.repeat(" ", len_numbers + 1) |> string.to_graphemes,
    ])

  let #(res, _, _) =
    problems
    |> list.fold(#(0, 0, ""), fn(acc, problem) {
      let #(global_acc, curr_acc, op) = acc
      let number = problem |> list.take(len_numbers)
      let new_op = problem |> list.last |> result.unwrap("")
      let digits =
        number
        |> list.map(int.parse)
        |> list.map(fn(x) { x |> result.unwrap(0) })
        |> list.filter(fn(x) { x != 0 })
        |> int.undigits(10)
        |> result.unwrap(0)

      case digits == 0 {
        True -> #(global_acc + curr_acc, 0, "")
        False -> {
          case op {
            "+" -> #(global_acc, curr_acc + digits, op)
            "*" -> #(global_acc, curr_acc * digits, op)
            _ -> #(global_acc, digits, new_op)
          }
        }
      }
    })

  res
}
