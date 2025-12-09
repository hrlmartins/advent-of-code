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

  let comb = combination_list(points)

  io.println("== Part 1 ==")
  echo part1(comb)

  io.println("== Part 2 ==")
  let assert Ok(first) = points |> list.first()
  echo part2(comb, list.append(points, [first]))
}

fn part1(comb: List(#(Point, Point))) {
  comb
  |> list.fold(-1, fn(acc, p_pair) {
    let #(f_point, s_point) = p_pair
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

fn part2(comb: List(#(Point, Point)), input: List(Point)) {
  // build the polygon
  let lines =
    input
    |> list.window_by_2
    |> list.fold([], fn(acc, pair) {
      let #(f_point, s_point) = pair

      [Line(f_point, s_point), ..acc]
    })

  let assert Ok(max_x) =
    input |> list.map(fn(point) { point.x }) |> list.max(int.compare)

  comb
  |> list.fold(-1, fn(acc, p_pair) {
    let #(f_point, s_point) = p_pair
    let Point(x1, y1) = f_point
    let Point(x2, y2) = s_point

    let square_points =
      rectangle_points(f_point, s_point) |> square_border_points
    let inside =
      square_points
      |> list.all(fn(p) {
        point_on_segment(p, lines) || point_in_polygon(p, lines)
      })

    use <- bool.guard(when: !inside, return: acc)

    let diff_x = int.max(x1, x2) - int.min(x1, x2) + 1
    let diff_y = int.max(y1, y2) - int.min(y1, y2) + 1
    let area = diff_x * diff_y
    case acc < area {
      True -> area
      False -> acc
    }
  })
}

fn combination_list(points: List(Point)) -> List(#(Point, Point)) {
  points
  |> list.index_fold([], fn(acc, point_i, i) {
    points
    |> list.index_fold(acc, fn(acc_x, point_j, j) {
      use <- bool.guard(when: i == j || j < i, return: acc_x)
      [#(point_i, point_j), ..acc_x]
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

fn points_in_square(square_points: List(Point)) -> List(Point) {
  let xs = square_points |> list.map(fn(p) { p.x })
  let ys = square_points |> list.map(fn(p) { p.y })

  let min_x =
    xs
    |> list.max(fn(a, b) {
      case a {
        _ if a < b -> order.Gt
        _ if a > b -> order.Lt
        _ -> order.Eq
      }
    })
    |> result.unwrap(0)
  let max_x = xs |> list.max(int.compare) |> result.unwrap(0)
  let min_y =
    ys
    |> list.max(fn(a, b) {
      case a {
        _ if a < b -> order.Gt
        _ if a > b -> order.Lt
        _ -> order.Eq
      }
    })
    |> result.unwrap(0)
  let max_y = ys |> list.max(int.compare) |> result.unwrap(0)

  list.range(min_y, max_y)
  |> list.flat_map(fn(y) {
    list.range(min_x, max_x)
    |> list.map(fn(x) { Point(x, y) })
  })
}

pub fn square_border_points(points: List(Point)) -> List(Point) {
  case points {
    [a, b, c, d] -> {
      let edge1 = points_on_line(a, b)
      let edge2 = points_on_line(b, c)
      let edge3 = points_on_line(c, d)
      let edge4 = points_on_line(d, a)

      edge1
      |> set.union(edge2)
      |> set.union(edge3)
      |> set.union(edge4)
      |> set.to_list
    }
    _ -> []
  }
}

fn points_on_line(p1: Point, p2: Point) -> set.Set(Point) {
  let Point(x1, y1) = p1
  let Point(x2, y2) = p2

  case x1 == x2 {
    True -> {
      list.range(int.min(y1, y2), int.max(y1, y2))
      |> list.map(fn(y) { Point(x1, y) })
      |> set.from_list
    }

    False -> {
      list.range(int.min(x1, x2), int.max(x1, x2))
      |> list.map(fn(x) { Point(x, y1) })
      |> set.from_list
    }
  }
}
