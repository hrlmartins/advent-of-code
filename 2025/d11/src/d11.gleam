import gleam/bool
import gleam/dict
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
pub fn main() {
  let assert Ok(input) = read_all_lines()

  let paths =
    input
    |> list.fold(dict.new(), fn(acc, line) {
      let definition =
        line
        |> string.split(":")
        |> list.map(string.trim)
        |> list.flat_map(fn(x) { string.split(x, " ") })

      case definition {
        [h, ..rest] -> acc |> dict.insert(h, rest)
        _ -> acc
      }
    })

  io.println("== Part 1 ==")
  echo part1(paths)

  io.println("== Part 2 ==")
  echo part2(paths)
}

fn part1(paths: dict.Dict(String, List(String))) {
  visit(paths, "you", "out", dict.new()) |> pair.first
}

fn part2(paths: dict.Dict(String, List(String))) {
  let srv_fft = visit(paths, "svr", "fft", dict.new()) |> pair.first
  let fft_dac = visit(paths, "fft", "dac", dict.new()) |> pair.first
  let dac_out = visit(paths, "dac", "out", dict.new()) |> pair.first

  let srv_dac = visit(paths, "svr", "dac", dict.new()) |> pair.first
  let dac_fft = visit(paths, "dac", "fft", dict.new()) |> pair.first
  let fft_out = visit(paths, "fft", "out", dict.new()) |> pair.first

  { srv_dac * dac_fft * fft_out } + { srv_fft * fft_dac * dac_out }
}

fn visit(
  paths: dict.Dict(String, List(String)),
  node: String,
  target: String,
  visited: dict.Dict(String, Int),
) -> #(Int, dict.Dict(String, Int)) {
  use <- bool.guard(when: node == target, return: #(1, visited))
  use <- bool.guard(when: !{ paths |> dict.has_key(node) }, return: #(
    0,
    visited,
  ))
  use <- bool.guard(when: { visited |> dict.has_key(node) }, return: #(
    dict.get(visited, node) |> result.unwrap(-1),
    visited,
  ))

  let assert Ok(next) = dict.get(paths, node)
  let #(final_count, visited) =
    next
    |> list.fold(#(0, visited), fn(acc, next_node) {
      let #(count, c_visited) = acc
      let #(r_count, r_visited) = visit(paths, next_node, target, c_visited)
      #(count + r_count, r_visited)
    })

  #(final_count, visited |> dict.insert(node, final_count))
}
