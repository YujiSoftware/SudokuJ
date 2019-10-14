package com.github.yujisoftware.sudoku;

import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		String file = args[0];
		int rows = Integer.parseInt(args[1]);

		System.err.println("Start read...");

		long startRead = System.currentTimeMillis();
		Sudoku[] sudoku = new Sudoku[rows];
		try (FileReader reader = new FileReader(file)){
			// 1行目をスキップ
			while (reader.read() != '\n') {
			}

			for (int i = 0; i < sudoku.length; i++) {
				// 問題
				char[] quizzes = new char[9 * 9];
				reader.read(quizzes, 0, quizzes.length);

				// カンマ (読み捨て)
				reader.read();

				// 回答
				char[] solutions = new char[9 * 9];
				reader.read(solutions, 0, solutions.length);

				// 改行 (読み捨て)
				reader.read();

				sudoku[i] = Sudoku.parse(quizzes, solutions);
			}
		}
		long endRead = System.currentTimeMillis();

		System.err.println("Start resolve...");

		long startSolve = System.currentTimeMillis();
		for (int i = 0; i < sudoku.length; i++) {
			if (!solve(sudoku[i])) {
				throw new RuntimeException("Unsolved. [" + sudoku[i] + "]");
			}

			if (!sudoku[i].isValid()) {
				throw new RuntimeException("Invalid. [" + sudoku[i] + "]");
			}
		}
		long endSolve = System.currentTimeMillis();

		System.out.println((endRead - startRead) + "ms\t" + (endSolve - startSolve) + "ms");
	}

	private static boolean solve(Sudoku sudoku) {
		boolean updated;
		boolean solved;
		do {
			updated = false;
			solved = true;

			for (int i = 0; i < 9 * 9; i++) {
				if (sudoku.get(i) != 0) {
					continue;
				}
				solved = false;

				int candidate = sudoku.getCandidateBit(i);
				if (candidate == 0) {
					return false;
				}

				int highest = Integer.highestOneBit(candidate);
				int lowest = Integer.lowestOneBit(candidate);
				if (highest == lowest) {
					sudoku.set(i, toNum(lowest));
					updated = true;
				}
			}
		} while (updated);

		if (solved) {
			return true;
		}

		// 候補が少ない箇所を探す
		int count = Integer.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < 9 * 9; i++) {
			if (sudoku.get(i) != 0) {
				continue;
			}

			int c = Integer.bitCount(sudoku.getCandidateBit(i));
			if (c < count) {
				count = c;
				index = i;
			}
		}

		// 複数ある候補のうちの一つを仮定して、再帰的に解析
		int candidate = sudoku.getCandidateBit(index);
		for (int i = 0; i < 9; i++) {
			int bit = candidate & 1 << i;
			if (bit == 0) {
				continue;
			}

			Sudoku clone = sudoku.clone();
			clone.set(index, toNum(bit));

			if (solve(clone)) {
				for (int j = 0; j < 9 * 9; j++) {
					sudoku.set(j, clone.get(j));
				}
				return true;
			}
		}

		return false;
	}

	private static byte toNum(int bit) {
		// 対数関数で計算できるが、パターンが少ないので switch の方が速い
		switch (bit) {
		case 1:
			return 1;
		case 2:
			return 2;
		case 4:
			return 3;
		case 8:
			return 4;
		case 16:
			return 5;
		case 32:
			return 6;
		case 64:
			return 7;
		case 128:
			return 8;
		case 256:
			return 9;
		default:
			throw new RuntimeException("Invalid bit. [" + bit + "]");
		}
	}
}
