package com.github.yujisoftware.sudoku;

public class Sudoku {
	private byte[] quizzes = new byte[9 * 9];
	private int[] horizontal = new int[9];
	private int[] vertical = new int[9];
	private int[] box = new int[9];

	private Sudoku(byte[] quizzes, int[] horizontal, int[] vertical, int[] box) {
		System.arraycopy(quizzes, 0, this.quizzes, 0, this.quizzes.length);
		System.arraycopy(horizontal, 0, this.horizontal, 0, this.horizontal.length);
		System.arraycopy(vertical, 0, this.vertical, 0, this.vertical.length);
		System.arraycopy(box, 0, this.box, 0, this.box.length);
	}

	public Sudoku(byte[] quizzes) {
		for (int i = 0; i < quizzes.length; i++) {
			if (quizzes[i] != 0) {
				set(i, quizzes[i]);
			}
		}
	}

	public static Sudoku parse(char[] quizzes) {
		byte[] q = new byte[quizzes.length];
		for (int i = 0; i < quizzes.length; i++) {
			q[i] = (byte) (quizzes[i] - '0');
		}

		return new Sudoku(q);
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

	public Object clone() {
		return new Sudoku(quizzes, horizontal, vertical, box);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
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
