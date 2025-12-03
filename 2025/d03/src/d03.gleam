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

pub fn main() {
  let assert Ok(input) = read_all_lines()
  let banks =
    input
    |> list.map(fn(x: String) { x |> ints_from_line })
    |> list.map(fn(x: List(Int)) {
      x
      |> list.first
      |> result.unwrap(-1)
      |> int.digits(10)
      |> result.unwrap([])
    })

  io.println("== Part 1 ==")
  echo part1(banks)

  io.println("== Part 2 ==")
  echo part2(banks)
}

fn part1(banks) {
  banks
  |> list.fold(0, fn(acc, bank) {
    let max_jolt =
      bank
      |> list.combinations(2)
      |> list.max(fn(f_comb, s_comb) {
        let first = int.undigits(f_comb, 10) |> result.unwrap(-1)
        let second = int.undigits(s_comb, 10) |> result.unwrap(-1)

        int.compare(first, second)
      })
      |> result.unwrap([])
      |> int.undigits(10)
      |> result.unwrap(-1)

    acc + max_jolt
  })
}

fn part2(banks) {
  banks
  |> list.fold(0, fn(acc, bank) {
    acc
    + {
      find_max_bank_config(bank, 12) |> int.undigits(10) |> result.unwrap(-1)
    }
  })
}

fn find_max_bank_config(bank: List(Int), n: Int) -> List(Int) {
  case n {
    0 -> []
    _ -> {
      // to find the n more significant bank it means n - 1 are reserved for the other digits
      let r_index = { bank |> list.length } - { n - 1 }
      let n_bank_space = bank |> list.take(r_index)
      let segment_max_val: #(Int, Int) = calculate_first_max(n_bank_space)

      [
        segment_max_val.0,
        ..find_max_bank_config(bank |> list.drop(segment_max_val.1 + 1), n - 1)
      ]
    }
  }
}

fn calculate_first_max(n_bank_space: List(Int)) -> #(Int, Int) {
  n_bank_space
  |> list.index_fold(#(-1, -1), fn(acc, elem, idx) {
    case elem > acc.0 {
      True -> #(elem, idx)
      False -> acc
    }
  })
}
