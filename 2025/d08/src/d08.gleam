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
  Point(x: Int, y: Int, z: Int)
}

pub type DSet {
  DSet(parent: dict.Dict(Point, Point), size: dict.Dict(Point, Int))
}

pub fn main() {
  let assert Ok(input) = read_all_lines()
  let points =
    input
    |> list.map(ints_from_line)
    |> list.map(fn(point) {
      case point {
        [x, y, z] -> Point(x, y, z)
        _ -> Point(-1, -1, -1)
      }
    })

  let dist_list = calc_dist_list(points)

  let dset =
    points
    |> list.fold(DSet(dict.new(), dict.new()), fn(acc, point) {
      let DSet(a, b) = acc
      DSet(a |> dict.insert(point, point), b |> dict.insert(point, 1))
    })

  io.println("== Part 1 ==")
  echo part1(dset, dist_list)

  io.println("== Part 2 ==")
  echo part2(dset, dist_list)
}

fn part1(dset: DSet, dist_list: List(#(Point, Point, Int))) {
  let dset =
    dist_list
    |> list.take(1000)
    |> list.fold(dset, fn(acc, dist_tuple) {
      let #(a, b, _) = dist_tuple
      union(acc, a, b)
    })

  rep_list(dset)
  |> list.fold([], fn(acc, rep) {
    let assert Ok(num) = dset.size |> dict.get(rep)
    list.append(acc, [num])
  })
  |> list.sort(int.compare)
  |> list.reverse
  |> list.take(3)
  |> list.fold(1, int.multiply)
}

fn part2(dset: DSet, dist_list: List(#(Point, Point, Int))) {
  dist_list
  |> list.fold_until(#(dset, 1), fn(acc, dist_tuple) {
    let #(a, b, _) = dist_tuple
    let #(dset, res) = acc

    let dset = union(dset, a, b)
    let rep_size =
      rep_list(dset)
      |> list.length

    case rep_size == 1 {
      True -> list.Stop(#(dset, a.x * b.x))
      False -> list.Continue(#(dset, res))
    }
  })
  |> pair.second
}

fn find(dset: DSet, p: Point) -> #(Point, DSet) {
  let DSet(parent, size) = dset

  let assert Ok(root) = parent |> dict.get(p)
  let assert Ok(root_parent) = parent |> dict.get(root)

  case root != root_parent {
    True -> {
      let #(new_parent, n_dset) = find(dset, root)
      #(
        new_parent,
        DSet(..n_dset, parent: n_dset.parent |> dict.insert(p, new_parent)),
      )
    }
    False -> #(root, dset)
  }
}

fn union(dset: DSet, f_point: Point, s_point: Point) -> DSet {
  let #(f_point_rep, dset) = find(dset, f_point)
  let #(s_point_rep, dset) = find(dset, s_point)

  use <- bool.guard(when: f_point_rep == s_point_rep, return: dset)

  let assert Ok(f_point_size) = dset.size |> dict.get(f_point_rep)
  let assert Ok(s_point_size) = dset.size |> dict.get(s_point_rep)

  case f_point_size < s_point_size {
    True -> {
      let n_size = s_point_size + f_point_size
      DSet(
        dset.parent |> dict.insert(f_point_rep, s_point_rep),
        dset.size |> dict.insert(s_point_rep, n_size),
      )
    }
    False -> {
      let n_size = s_point_size + f_point_size
      DSet(
        dset.parent |> dict.insert(s_point_rep, f_point_rep),
        dset.size |> dict.insert(f_point_rep, n_size),
      )
    }
  }
}

fn rep_list(dset: DSet) -> List(Point) {
  dset.parent
  |> dict.keys
  |> list.map(fn(x) { find(dset, x).0 })
  |> list.unique
}

fn calc_dist_list(points: List(Point)) -> List(#(Point, Point, Int)) {
  let dist_list =
    points
    |> list.index_fold([], fn(acc, point_i, i) {
      points
      |> list.index_fold(acc, fn(acc_x, point_j, j) {
        use <- bool.guard(when: i == j || j < i, return: acc_x)
        let dist = dist(point_i, point_j)
        [#(point_i, point_j, dist), ..acc_x]
      })
    })
    |> list.sort(fn(f, s) {
      let #(_, _, dist_f) = f
      let #(_, _, dist_s) = s
      int.compare(dist_f, dist_s)
    })
  dist_list
}

fn dist(point_i: Point, point_j: Point) {
  let Point(xi, yi, zi) = point_i
  let Point(xj, yj, zj) = point_j
  let dx = xi - xj
  let dy = yi - yj
  let dz = zi - zj
  dx * dx + dy * dy + dz * dz
}
