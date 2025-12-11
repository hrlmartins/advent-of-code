import gleam/bool
import gleam/deque
import gleam/dict
import gleam/int
import gleam/io
import gleam/list
import gleam/option
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
pub type State {
  Off
  On
}

pub type LightsState =
  dict.Dict(Int, State)

pub type JoltsConfig =
  dict.Dict(Int, Int)

pub type ButtonsConfig =
  List(List(Int))

pub type Machine {
  Machine(
    light_state: LightsState,
    buttons_config: ButtonsConfig,
    jolts_config: JoltsConfig,
  )
}

pub type LightNode {
  LightNode(ls: LightsState)
}

pub type JoltNode {
  JoltNode(js: JoltsConfig)
}

pub fn parse_line(input: String) {
  let assert Ok(pattern_re) = regexp.from_string("\\[(.*)\\]")
  let assert Ok(group_re) = regexp.from_string("\\((.*?)\\)")
  let assert Ok(values_re) = regexp.from_string("\\{(.*?)\\}")

  let final_state =
    regexp.scan(pattern_re, input)
    |> list.map(fn(m) { m.content })
    |> list.flat_map(fn(m) {
      m |> string.to_graphemes |> list.filter(fn(x) { x != "[" && x != "]" })
    })

  let buttons_config =
    regexp.scan(group_re, input)
    |> list.flat_map(fn(m) {
      m.submatches |> list.map(fn(x) { option.unwrap(x, "") })
    })
    |> list.map(fn(x) {
      x
      |> string.split(",")
      |> list.map(fn(y) { y |> int.parse |> result.unwrap(-1) })
    })

  let jolts_config =
    regexp.scan(values_re, input)
    |> list.flat_map(fn(m) {
      m.submatches |> list.map(fn(x) { option.unwrap(x, "") })
    })
    |> list.flat_map(fn(x) {
      x
      |> string.split(",")
      |> list.map(fn(y) { y |> int.parse |> result.unwrap(-1) })
    })

  #(final_state, buttons_config, jolts_config)
}

pub fn main() {
  let assert Ok(input) = read_all_lines()
  let machines =
    input
    |> list.map(parse_line)
    |> list.map(fn(configs) {
      let #(final_state, buttons_config, jolts_config) = configs

      let expected_state: LightsState = convert_state(final_state)
      let jolts = convert_jolts(jolts_config)

      Machine(expected_state, buttons_config, jolts)
    })

  io.println("== Part 1 ==")
  echo part1(machines)

  io.println("== Part 2 ==")
  echo part2(machines)
}

fn part1(machines: List(Machine)) {
  machines
  |> list.fold(0, fn(acc, machine) {
    // TODO for each machine start with a zero'd machine until we reach the desired state
    let machine_start =
      Machine(
        ..machine,
        light_state: machine.light_state |> dict.map_values(fn(_, _) { Off }),
      )
    visit(
      machine,
      deque.from_list([#(LightNode(machine_start.light_state), 0)]),
      machine.light_state,
      set.new(),
    )
    + acc
  })
}

fn part2(machines: List(Machine)) {
  machines
  |> list.take(1)
  |> list.fold(0, fn(acc, machine) {
    // TODO for each machine start with a zero'd machine until we reach the desired state
    let machine_start =
      Machine(
        ..machine,
        jolts_config: machine.jolts_config |> dict.map_values(fn(_, _) { 0 }),
      )
    visit_jolts(
      machine,
      deque.from_list([#(JoltNode(machine_start.jolts_config), 0)]),
      machine.jolts_config,
      set.new(),
    )
    + acc
  })
}

fn convert_jolts(jolts_config: List(Int)) {
  jolts_config
  |> list.index_fold(dict.new(), fn(acc, j, idx) { acc |> dict.insert(idx, j) })
}

fn convert_state(final_state: List(String)) {
  final_state
  |> list.index_fold(dict.new(), fn(acc, l, idx) {
    let state = case l {
      "." -> Off
      _ -> On
    }
    acc |> dict.insert(idx, state)
  })
}

fn visit(
  mach: Machine,
  to_visit: deque.Deque(#(LightNode, Int)),
  target: LightsState,
  visited: set.Set(LightNode),
) -> Int {
  case deque.pop_front(to_visit) {
    Error(_) -> 0
    Ok(#(#(node, cost), next_visit)) -> {
      use <- bool.guard(when: node.ls == target, return: cost)

      let visited = visited |> set.insert(node)
      let ls = node.ls

      let new_visits =
        calc_next_visits(mach, ls)
        |> list.filter(fn(n) { !{ visited |> set.contains(n) } })
        |> list.fold(next_visit, fn(acc, node) {
          acc |> deque.push_back(#(node, cost + 1))
        })

      visit(mach, new_visits, target, visited)
    }
  }
}

fn visit_jolts(
  mach: Machine,
  to_visit: deque.Deque(#(JoltNode, Int)),
  target: JoltsConfig,
  visited: set.Set(JoltNode),
) -> Int {
  case deque.pop_front(to_visit) {
    Error(_) -> 0
    Ok(#(#(node, cost), next_visit)) -> {
      echo cost
      use <- bool.guard(when: node.js == target, return: cost)

      let visited = visited |> set.insert(node)
      let js = node.js
      let new_visits =
        calc_next_visits_p2(mach, js)
        |> list.filter(fn(n) { !{ visited |> set.contains(n) } })
        |> list.fold(next_visit, fn(acc, node) {
          acc |> deque.push_back(#(node, cost + 1))
        })

      visit_jolts(mach, new_visits, target, visited)
    }
  }
}

fn calc_next_visits(mach: Machine, ls: LightsState) {
  mach.buttons_config
  |> list.map(fn(bc) {
    let new_n =
      bc
      |> list.fold(ls, fn(acc, light_idx) {
        let assert Ok(lstate) = dict.get(acc, light_idx)
        acc |> dict.insert(light_idx, toggle(lstate))
      })
    LightNode(new_n)
  })
}

fn calc_next_visits_p2(mach: Machine, ls: JoltsConfig) {
  mach.buttons_config
  |> list.map(fn(bc) {
    let new_n =
      bc
      |> list.fold(ls, fn(acc, jolt_idx) {
        let assert Ok(jstate) = dict.get(acc, jolt_idx)
        acc |> dict.insert(jolt_idx, jstate + 1)
      })
    JoltNode(new_n)
  })
}

fn toggle(state: State) {
  case state {
    Off -> On
    On -> Off
  }
}
// fn visit(
//   grid: dict.Dict(Pos, SpaceType),
//   to_visit: Pos,
//   visited: dict.Dict(Pos, Int),
// ) -> #(Int, dict.Dict(Pos, Int)) {
//   use <- bool.guard(
//     when: visited_or_invalid(to_visit, grid, visited),
//     return: case p2 {
//       True -> {
//         case visited |> dict.has_key(to_visit) {
//           True -> {
//             let assert Ok(val) = visited |> dict.get(to_visit)
//             #(val, visited)
//           }
//           False -> {
//             #(1, visited)
//           }
//         }
//       }
//       False -> #(0, visited)
//     },
//   )
//
//   let space = grid |> dict.get(to_visit) |> result.unwrap(Blank)
//
//   let #(diff, next) = case space {
//     Splitter if !p2 -> #(1, neighbour(to_visit, True))
//     Splitter if p2 -> #(0, neighbour(to_visit, True))
//     _ -> #(0, neighbour(to_visit, False))
//   }
//
//   next
//   |> list.fold(#(diff, visited), fn(acc, pos) {
//     let #(c, v) = acc
//     let #(r_count, r_visited) = visit(grid, pos, v, p2)
//     #(c + r_count, r_visited |> dict.insert(to_visit, c + r_count))
//   })
// }
