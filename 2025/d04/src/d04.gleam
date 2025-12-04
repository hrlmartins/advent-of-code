import gleam/dict
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

pub type Pos {
  Pos(x: Int, y: Int)
}

// So... I thought the second problem would be different hence the "empty_spots"
// But it was not needed. And even the dict could be a set... OH well... at least I learned how to update records
// TODO change Grid to be just a SET! :D
type Grid {
  Grid(grid: dict.Dict(Pos, String), empty_spots: List(Pos))
}

pub fn main() {
  let assert Ok(input) = read_all_lines()
  let grid_map =
    input
    |> list.index_fold(Grid(dict.new(), []), fn(acc, line, y_idx) {
      line
      |> string.to_graphemes
      |> list.index_fold(acc, fn(acc_x, char, x_idx) {
        case char {
          "@" ->
            Grid(
              ..acc_x,
              grid: acc_x.grid |> dict.insert(Pos(x_idx, y_idx), char),
            )
          _ ->
            Grid(
              ..acc_x,
              empty_spots: acc_x.empty_spots |> list.append([Pos(x_idx, y_idx)]),
            )
        }
      })
    })

  io.println("== Part 1 ==")
  echo part1(grid_map)

  io.println("== Part 2 ==")
  echo part2(grid_map)
}

fn part1(grid_map: Grid) {
  grid_map.grid
  |> dict.fold(0, fn(acc, pos, _) {
    let count =
      neighbours(pos)
      |> list.count(fn(visit) { grid_map.grid |> dict.has_key(visit) })

    case count < 4 {
      True -> acc + 1
      False -> acc
    }
  })
}

fn part2(grid_map: Grid) {
  loop(grid_map, 0)
}

fn loop(map: Grid, acc: Int) {
  let old_size = map.grid |> dict.size
  let new_map = clear_rolls(map)
  let new_size = new_map.grid |> dict.size

  case new_size < old_size {
    True -> loop(new_map, acc + { old_size - new_size })
    False -> acc
  }
}

fn clear_rolls(grid_map: Grid) {
  let new_grid =
    grid_map.grid
    |> dict.filter(fn(pos, _) {
      let count =
        neighbours(pos)
        |> list.count(fn(visit) { grid_map.grid |> dict.has_key(visit) })

      count >= 4
    })

  Grid(..grid_map, grid: new_grid)
}

fn neighbours(pos: Pos) -> List(Pos) {
  [
    Pos(pos.x - 1, pos.y - 1),
    Pos(pos.x - 1, pos.y),
    Pos(pos.x - 1, pos.y + 1),
    Pos(pos.x, pos.y - 1),
    Pos(pos.x, pos.y + 1),
    Pos(pos.x + 1, pos.y - 1),
    Pos(pos.x + 1, pos.y),
    Pos(pos.x + 1, pos.y + 1),
  ]
}
