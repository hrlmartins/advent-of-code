import gleam/int
import gleam/io
import gleam/list
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

fn reverse_transpose(matrix: List(List(Int))) -> List(List(Int)) {
  case matrix {
    [] -> []
    [_, ..] -> {
      let width =
        matrix
        |> list.map(fn(l) { l |> list.length })
        |> list.max(int.compare)
        |> result.unwrap(-1)

      list.range(width - 1, 0)
      |> list.map(fn(i) {
        matrix
        |> list.map(fn(row) { nth(row, i) })
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
  let problems = input |> parse_columns
  io.println("== Part 1 ==")
  echo part1(problems)

  io.println("== Part 2 ==")
  echo part2(problems)
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

fn part2(problems: List(Column)) {
  problems
  |> list.fold(0, fn(acc, problem) {
    let digit_list =
      problem.values
      |> list.map(fn(x) { x |> int.digits(10) |> result.unwrap([]) })
    let transpose_digits = digit_list |> reverse_transpose

    acc
    + case problem.op {
      "+" -> problem.values |> list.fold(0, int.add)
      "*" -> problem.values |> list.fold(1, int.multiply)
      _ -> 0
    }
  })
}
