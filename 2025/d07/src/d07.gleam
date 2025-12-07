import gleam/bool
import gleam/dict
import gleam/int
import gleam/io
import gleam/list
import gleam/pair
import gleam/regexp
import gleam/result
import gleam/set
import gleam/string
import gleam/string_tree
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

pub type SpaceType {
  Blank
  Start
  Splitter
}

pub type Pos {
  Pos(x: Int, y: Int)
}

fn convert_char(char: String) -> SpaceType {
  case char {
    "^" -> Splitter
    "S" -> Start
    _ -> Blank
  }
}

pub fn main() {
  let assert Ok(input) = read_all_lines()

  let grid =
    input
    |> list.index_fold(dict.new(), fn(acc, line, y) {
      string.to_graphemes(line)
      |> list.index_fold(acc, fn(acc_x, char, x) {
        let s_type = convert_char(char)
        acc_x |> dict.insert(Pos(x, y), s_type)
      })
    })

  io.println("== Part 1 ==")
  echo part1(grid)

  io.println("== Part 2 ==")
  echo part2(grid)
}

fn part1(grid: dict.Dict(Pos, SpaceType)) {
  let filter_dict = grid |> dict.filter(fn(pos, val) { val == Start })
  let start_pos =
    filter_dict |> dict.keys |> list.first |> result.unwrap(Pos(-1, -1))
  visit(grid, start_pos, dict.new(), False) |> pair.first
}

fn part2(grid: dict.Dict(Pos, SpaceType)) {
  let filter_dict = grid |> dict.filter(fn(pos, val) { val == Start })
  let start_pos =
    filter_dict |> dict.keys |> list.first |> result.unwrap(Pos(-1, -1))
  visit(grid, start_pos, dict.new(), True) |> pair.first
}

fn visit(
  grid: dict.Dict(Pos, SpaceType),
  to_visit: Pos,
  visited: dict.Dict(Pos, Int),
  p2: Bool,
) -> #(Int, dict.Dict(Pos, Int)) {
  use <- bool.guard(
    when: visited_or_invalid(to_visit, grid, visited),
    return: case p2 {
      True -> {
        case visited |> dict.has_key(to_visit) {
          True -> {
            let assert Ok(val) = visited |> dict.get(to_visit)
            #(val, visited)
          }
          False -> {
            #(1, visited)
          }
        }
      }
      False -> #(0, visited)
    },
  )

  let space = grid |> dict.get(to_visit) |> result.unwrap(Blank)

  let #(diff, next) = case space {
    Splitter if !p2 -> #(1, neighbour(to_visit, True))
    Splitter if p2 -> #(0, neighbour(to_visit, True))
    _ -> #(0, neighbour(to_visit, False))
  }

  next
  |> list.fold(#(diff, visited), fn(acc, pos) {
    let #(c, v) = acc
    let #(r_count, r_visited) = visit(grid, pos, v, p2)
    #(c + r_count, r_visited |> dict.insert(to_visit, c + r_count))
  })
}

fn visited_or_invalid(
  pos: Pos,
  grid: dict.Dict(Pos, SpaceType),
  visited: dict.Dict(Pos, Int),
) {
  visited |> dict.has_key(pos) || !dict.has_key(grid, pos)
}

fn neighbour(pos: Pos, is_split: Bool) {
  case is_split {
    True -> [Pos(pos.x - 1, pos.y), Pos(pos.x + 1, pos.y)]
    False -> [Pos(pos.x, pos.y + 1)]
  }
}
