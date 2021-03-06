package org.ice1000.compare.core;

public class CommentsDeleter {
	private static final char MARK = '"';

	private static final char SLASH = '/';

	private static final char BACKSLASH = '\\';

	private static final char STAR = '*';

	private static final char NEWLINE = '\n';

	private static final int TYPE_EMPTY = 0x00;
	//引号
	private static final int TYPE_MARK = 1;

	//斜杠
	private static final int TYPE_SLASH = 2;

	//反斜杠
	private static final int TYPE_BACKSLASH = 3;

	//星号
	private static final int TYPE_STAR = 4;

	// 双斜杠类型的注释
	private static final int TYPE_DSLASH = 5;

	/**
	 * 删除char[]数组中_start位置到_end位置的元素
	 */
	@SuppressWarnings("WeakerAccess")
	public static char[] del(char[] _target, int _start, int _end) {
		char[] tmp = new char[_target.length - (_end - _start) + 1];
		try {
			System.arraycopy(_target, 0, tmp, 0, _start);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("\n" + _target.length + " " + tmp.length);
		}
		System.arraycopy(_target, _end + 1, tmp, _start, _target.length - _end - 1);
		return tmp;
	}

	/**
	 * 删除代码中的注释
	 *
	 * 写的有问题
	 */
	@SuppressWarnings({"WeakerAccess", "unused"})
	public static String delComments(String _target) {
		int preType = 0, mark = -1, cur, token = -1;
		// 输入字符串
		char[] input = _target.toCharArray();
		for (cur = 0; cur < input.length; cur++) {
			if (input[cur] == MARK) {
				// 首先判断是否为转义引号
				if (preType == TYPE_BACKSLASH) continue;
				// 已经进入引号之内
				mark = mark > 0 ? -1 : cur;
				preType = TYPE_MARK;
			} else if (input[cur] == SLASH) {
				// 当前位置处于引号之中
				if (mark > 0) continue;
				// 如果前一位是*，则进行删除操作
				if (preType == TYPE_STAR) {
					input = del(input, token, cur);
					// 退回一个位置进行处理
					cur = token - 1;
					preType = TYPE_EMPTY;
				} else if (preType == TYPE_SLASH) {
					token = cur - 1;
					preType = TYPE_DSLASH;
				} else preType = TYPE_SLASH;
			} else if (input[cur] == BACKSLASH) preType = TYPE_BACKSLASH;
			else if (input[cur] == STAR) {
				// 当前位置处于引号之中
				if (mark > 0) continue;
				// 如果前一个位置是/,则记录注释开始的位置
				if (preType == TYPE_SLASH) token = cur - 1;
				preType = TYPE_STAR;
			} else if (input[cur] == NEWLINE)
				if (preType == TYPE_DSLASH) {
					input = del(input, token, cur);
					// 退回一个位置进行处理
					cur = token - 1;
					preType = 0;
				}
		}
		return new String(input);
	}
}
