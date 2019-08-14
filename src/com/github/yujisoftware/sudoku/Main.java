package com.github.yujisoftware.sudoku;

import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		FileReader reader = null;
		try {
			reader = new FileReader(args[0]);

			// 1行目をスキップ
			while (reader.read() != '\n') {
			}

			int row = 1;
			do {
				System.out.println(row++);

				// 問題
				char[] quizzes = new char[9 * 9];
				reader.read(quizzes, 0, quizzes.length);

				// カンマ (読み捨て)
				reader.read();

				// 回答
				char[] solutions = new char[9 * 9];
				reader.read(solutions, 0, solutions.length);

				Sudoku sudoku = Sudoku.parse(quizzes);
				if (!solve(sudoku)) {
					throw new RuntimeException("Unsolved. [" + sudoku + "]");
				}

				for (int i = 0; i < solutions.length; i++) {
					if (solutions[i] - '0' != sudoku.get(i)) {
						throw new RuntimeException("Invalid. [" + sudoku + "]");
					}
				}
			} while (reader.read() != -1);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
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

				int highest = highestOneBit(candidate);
				int lowest = lowestOneBit(candidate);
				if (highest == lowest) {
					sudoku.set(i, toNum(lowest));
					updated = true;
				}
			}
		} while (updated);

		if (solved) {
			return true;
		}

		int count = Integer.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < 9 * 9; i++) {
			if (sudoku.get(i) != 0) {
				continue;
			}

			int c = bitCount(sudoku.getCandidateBit(i));
			if (c < count) {
				count = c;
				index = i;
			}
		}

		int candidate = sudoku.getCandidateBit(index);
		for (int i = 0; i < 9; i++) {
			int bit = candidate & 1 << i;
			if (bit == 0) {
				continue;
			}

			Sudoku clone = (Sudoku) sudoku.clone();
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

	private static int highestOneBit(int i) {
		return i & (Integer.MIN_VALUE >>> numberOfLeadingZeros(i));
	}

	private static int numberOfLeadingZeros(int i) {
		if (i <= 0)
			return i == 0 ? 32 : 0;
		int n = 31;
		if (i >= 1 << 16) {
			n -= 16;
			i >>>= 16;
		}
		if (i >= 1 << 8) {
			n -= 8;
			i >>>= 8;
		}
		if (i >= 1 << 4) {
			n -= 4;
			i >>>= 4;
		}
		if (i >= 1 << 2) {
			n -= 2;
			i >>>= 2;
		}
		return n - (i >>> 1);
	}

	private static int lowestOneBit(int i) {
		return i & -i;
	}

	private static int bitCount(int i) {
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		i = (i + (i >>> 4)) & 0x0f0f0f0f;
		i = i + (i >>> 8);
		i = i + (i >>> 16);
		return i & 0x3f;
	}
}