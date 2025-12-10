import gleam/bool
import gleam/dict
import gleam/int
import gleam/io
import gleam/list
import gleam/order
import gleam/pair
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

pub type Point {
  Point(x: Int, y: Int)
}

pub type Line {
  Line(f_point: Point, s_point: Point)
}

pub fn main() {
  let assert Ok(input) = read_all_lines()
  let points =
    input
    |> list.map(ints_from_line)
    |> list.map(fn(point) {
      case point {
        [x, y] -> Point(x, y)
        _ -> Point(-1, -1)
      }
    })

  let comb =
    combination_list(points)
    |> list.sort(fn(a, b) {
      let #(_, _, area_a) = a
      let #(_, _, area_b) = b

      int.compare(area_a, area_b)
    })

  io.println("== Part 1 ==")
  echo part1(comb)

  io.println("== Part 2 ==")
  let assert Ok(first) = points |> list.first()
  echo part2(comb |> list.reverse, list.append(points, [first]))
}

fn part1(comb: List(#(Point, Point, Int))) {
  comb
  |> list.fold(-1, fn(acc, p_pair) {
    let #(f_point, s_point, _) = p_pair
    let Point(x1, y1) = f_point
    let Point(x2, y2) = s_point

    let diff_x = int.max(x1, x2) - int.min(x1, x2) + 1
    let diff_y = int.max(y1, y2) - int.min(y1, y2) + 1
    let area = diff_x * diff_y
    case acc < area {
      True -> area
      False -> acc
    }
  })
}

fn part2(comb: List(#(Point, Point, Int)), input: List(Point)) {
  let lines =
    input
    |> list.window_by_2
    |> list.fold([], fn(acc, pair) {
      let #(f_point, s_point) = pair
      let f_point_n =
        Point(int.min(f_point.x, s_point.x), int.min(f_point.y, s_point.y))
      let s_point_n =
        Point(int.max(f_point.x, s_point.x), int.max(f_point.y, s_point.y))

      [Line(f_point_n, s_point_n), ..acc]
    })
    |> list.reverse

  comb
  |> list.fold_until(-1, fn(acc, p_pair) {
    let #(f_point, s_point, area) = p_pair
    let f_point_n =
      Point(int.min(f_point.x, s_point.x), int.min(f_point.y, s_point.y))
    let s_point_n =
      Point(int.max(f_point.x, s_point.x), int.max(f_point.y, s_point.y))

    let all_square_edges_inside =
      list.all(rectangle_points(f_point_n, s_point_n), fn(x) {
        point_on_segment(x, lines) || point_in_polygon(x, lines)
      })
    case
      all_square_edges_inside
      && is_contained(lines, f_point_n.x, f_point_n.y, s_point_n.x, s_point_n.y)
    {
      True -> list.Stop(area)
      False -> list.Continue(acc)
    }
  })
}

// this does not work for all examples. I have a few small ones that break the algo in general
// But seems to work for the input :shrug:. So would no trust this to work for all inputs 
// unless all inputs share the same properties
// But basically all it does it test that all polygon edges do not cross the rectangle
// and so on each each we see if itś horizontal or vertical and check if there is a crossing 
// And include all polygon edges that are can cross horizonally or vertically (either min or max of the edge is inside the rectangle)
fn is_contained(
  edges: List(Line),
  min_x: Int,
  min_y: Int,
  max_x: Int,
  max_y: Int,
) {
  edges
  |> list.all(fn(edge) {
    let Line(Point(e_min_x, e_min_y), Point(e_max_x, e_max_y)) = edge
    case e_min_x == e_max_x {
      True ->
        !{
          min_x < e_min_x
          && max_x > e_max_x
          && min_y < e_max_y
          && max_y > e_min_y
        }
      False ->
        !{
          min_y < e_min_y
          && max_y > e_max_y
          && min_x < e_max_x
          && max_x > e_min_x
        }
    }
  })
}

fn combination_list(points: List(Point)) -> List(#(Point, Point, Int)) {
  points
  |> list.index_fold([], fn(acc, point_i, i) {
    points
    |> list.index_fold(acc, fn(acc_x, point_j, j) {
      use <- bool.guard(when: i == j || j < i, return: acc_x)

      let Point(x1, y1) = point_i
      let Point(x2, y2) = point_j

      let diff_x = int.max(x1, x2) - int.min(x1, x2) + 1
      let diff_y = int.max(y1, y2) - int.min(y1, y2) + 1
      let area = diff_x * diff_y
      [#(point_i, point_j, area), ..acc_x]
    })
  })
}

fn rectangle_points(p1: Point, p2: Point) -> List(Point) {
  let Point(x1, y1) = p1
  let Point(x2, y2) = p2

  let a = Point(x1, y1)
  let b = Point(x1, y2)
  let c = Point(x2, y2)
  let d = Point(x2, y1)

  [a, b, c, d]
}

fn point_on_segment(p: Point, lines: List(Line)) -> Bool {
  lines
  |> list.any(fn(line) {
    let Point(px, py) = p
    let Point(x1, y1) = line.f_point
    let Point(x2, y2) = line.s_point

    px >= int.min(x1, x2)
    && px <= int.max(x1, x2)
    && py >= int.min(y1, y2)
    && py <= int.max(y1, y2)
  })
}

fn point_in_polygon(point: Point, lines: List(Line)) -> Bool {
  let Point(px, py) = point

  let crossings =
    lines
    |> list.count(fn(line) {
      let Line(p1, p2) = line
      let Point(x1, y1) = p1
      let Point(x2, y2) = p2

      x1 == x2 && int.min(y1, y2) <= py && py <= int.max(y1, y2) && x1 > px
    })

  crossings % 2 == 1
}
