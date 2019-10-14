package com.github.yujisoftware.sudoku;

import java.util.Arrays;

public class Sudoku {
	private final byte[] solutions;

	private byte[] quizzes;
	private int[] horizontal;
	private int[] vertical;
	private int[] box;

	private Sudoku(byte[] quizzes, byte[] solutions, int[] horizontal, int[] vertical, int[] box) {
		this.solutions = solutions;

		this.quizzes = Arrays.copyOf(quizzes, quizzes.length);
		this.horizontal = Arrays.copyOf(horizontal, horizontal.length);
		this.vertical = Arrays.copyOf(vertical, vertical.length);
		this.box = Arrays.copyOf(box, box.length);
	}

	public Sudoku(byte[] quizzes, byte[] solutions) {
		this.solutions = solutions;

		this.quizzes = new byte[9 * 9];
		this.horizontal = new int[9];
		this.vertical = new int[9];
		this.box = new int[9];

		for (int i = 0; i < quizzes.length; i++) {
			if (quizzes[i] != 0) {
				set(i, quizzes[i]);
			}
		}
	}

	public static Sudoku parse(char[] quizzes, char[] solutions) {
		byte[] q = new byte[quizzes.length];
		for (int i = 0; i < quizzes.length; i++) {
			q[i] = (byte) (quizzes[i] - '0');
		}

		byte[] s = new byte[solutions.length];
		for (int i = 0; i < solutions.length; i++) {
			s[i] = (byte) (solutions[i] - '0');
		}

		return new Sudoku(q, s);
	}

	public byte get(int index) {
		return quizzes[index];
	}

	public void set(int index, byte number) {
		quizzes[index] = number;

		int h = index / 9;
		int v = index % 9;
		int value = 1 << (number - 1);
		horizontal[h] |= value;
		vertical[v] |= value;
		box[(h / 3) * 3 + (v / 3)] |= value;
	}

	public int getCandidateBit(int index) {
		int h = index / 9;
		int v = index % 9;

		int bit = 0;
		bit |= horizontal[h];
		bit |= vertical[v];
		bit |= box[(h / 3) * 3 + (v / 3)];

		return ~bit & 0x1FF;
	}

	public boolean isValid() {
		for (int i = 0; i < quizzes.length; i++) {
			if (quizzes[i] != solutions[i]) {
				return false;
			}
		}

		return true;
	}

	public Sudoku clone() {
		return new Sudoku(quizzes, solutions, horizontal, vertical, box);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			if (i > 0) {
				sb.append(System.getProperty("line.separator"));
			}

			for (int j = 0; j < 9; j++) {
				if (j > 0) {
					sb.append(' ');
				}
				sb.append((char) (quizzes[i * 9 + j] + '0'));
			}
		}

		return sb.toString();
	}
}
