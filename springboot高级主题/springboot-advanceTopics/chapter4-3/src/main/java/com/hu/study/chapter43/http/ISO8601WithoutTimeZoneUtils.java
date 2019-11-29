package com.hu.study.chapter43.http;


import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Copy from jackson ISO8601Utils,remove timezone information. And output hour/min/second/milliseconds when has value, avoid invalid time fields changes during convert. 
 */

/**
 * Utilities methods for manipulating dates in iso8601 format. This is much much
 * faster and GC friendly than using SimpleDateFormat so highly suitable if you
 * (un)serialize lots of date objects.
 *
 * Supported parse format:
 * [yyyy-MM-dd|yyyyMMdd][T(hh:mm[:ss[.sss]]|hhmm[ss[.sss]])]?[Z|[+-]hh:mm]]
 *
 * @see <a href="http://www.w3.org/TR/NOTE-datetime">this specification</a>
 */
public class ISO8601WithoutTimeZoneUtils {

	/**
	 * The GMT timezone, prefetched to avoid more lookups.
	 */
	private static final TimeZone TIMEZONE_DEFAULT = TimeZone.getDefault();

	public static String format(Date date) {
		Calendar calendar = new GregorianCalendar(TIMEZONE_DEFAULT);
		calendar.setTime(date);

		// estimate capacity of buffer as close as we can (yeah, that's pedantic
		// ;)
		int capacity = "yyyy-MM-ddThh:mm:ss.sss".length();

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int ms = calendar.get(Calendar.MILLISECOND);

		StringBuilder formatted = new StringBuilder(capacity);

		padInt(formatted, calendar.get(Calendar.YEAR), "yyyy".length());
		formatted.append('-');
		padInt(formatted, calendar.get(Calendar.MONTH) + 1, "MM".length());
		formatted.append('-');
		padInt(formatted, calendar.get(Calendar.DAY_OF_MONTH), "dd".length());
		if (hour > 0 || min > 0 || second > 0 || ms > 0) {
			formatted.append('T');
			padInt(formatted, hour, "hh".length());
			formatted.append(':');
			padInt(formatted, min, "mm".length());
			formatted.append(':');
			padInt(formatted, second, "ss".length());
			if (ms > 0) {
				formatted.append('.');
				padInt(formatted, ms, "sss".length());
			}
		}
		return formatted.toString();
	}

	/**
	 * Parse a date from ISO-8601 formatted string. It expects a format
	 * [yyyy-MM-dd|yyyyMMdd][T(hh:mm[:ss[.sss]]|hhmm[ss[.sss]])]?[Z|[+-]hh:mm]]
	 * 
	 * @param date
	 *            ISO string to parse in the appropriate format.
	 * @param pos
	 *            The position to start parsing from, updated to where parsing
	 *            stopped.
	 * @return the parsed date
	 * @throws ParseException
	 *             if the date is not in the appropriate format
	 */
	public static Date parse(String date, ParsePosition pos) throws ParseException {
		Exception fail = null;
		try {
			int offset = pos.getIndex();

			// extract year
			int year = parseInt(date, offset, offset += 4);
			if (checkOffset(date, offset, '-')) {
				offset += 1;
			}

			// extract month
			int month = parseInt(date, offset, offset += 2);
			if (checkOffset(date, offset, '-')) {
				offset += 1;
			}

			// extract day
			int day = parseInt(date, offset, offset += 2);
			// default time value
			int hour = 0;
			int minutes = 0;
			int seconds = 0;
			int milliseconds = 0; // always use 0 otherwise returned date will
									// include millis of current time

			// if the value has no time component (and no time zone), we are
			// done
			boolean hasT = checkOffset(date, offset, 'T');

			if (!hasT && (date.length() <= offset)) {
				Calendar calendar = new GregorianCalendar(year, month - 1, day);

				pos.setIndex(offset);
				return calendar.getTime();
			}

			if (hasT) {

				// extract hours, minutes, seconds and milliseconds
				hour = parseInt(date, offset += 1, offset += 2);
				if (checkOffset(date, offset, ':')) {
					offset += 1;
				}

				minutes = parseInt(date, offset, offset += 2);
				if (checkOffset(date, offset, ':')) {
					offset += 1;
				}
				// second and milliseconds can be optional
				if (date.length() > offset) {
					char c = date.charAt(offset);
					if (c != 'Z' && c != '+' && c != '-') {
						seconds = parseInt(date, offset, offset += 2);
						// milliseconds can be optional in the format
						if (checkOffset(date, offset, '.')) {
							milliseconds = parseInt(date, offset += 1, offset += 3);
						}
					}
				}
				if (date.length() > offset) {
					char c = date.charAt(offset);
					if (c == 'Z' || c == '+' || c == '-') {
						throw new IllegalArgumentException(
								"Time zone indicator/offset not support! Char 'Z' or '+' or '-' is not valid!");
					} else {
						throw new IllegalArgumentException("Invalid char: '" + c + "' at pos " + offset + "!");
					}
				}

			}

			org.joda.time.LocalDateTime ldt=new org.joda.time.LocalDateTime(year, month,  day, hour, minutes,seconds,milliseconds);
			pos.setIndex(offset);
			return ldt.toDate(TIMEZONE_DEFAULT);
		} catch (IndexOutOfBoundsException e) {
			fail = e;
		} catch (NumberFormatException e) {
			fail = e;
		} catch (IllegalArgumentException e) {
			fail = e;
		}
		String input = (date == null) ? null : ('"' + date + "'");
		String msg = fail.getMessage();
		if (msg == null || msg.isEmpty()) {
			msg = "(" + fail.getClass().getName() + ")";
		}
		ParseException ex = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
		ex.initCause(fail);
		throw ex;
	}

	/**
	 * Check if the expected character exist at the given offset in the value.
	 * 
	 * @param value
	 *            the string to check at the specified offset
	 * @param offset
	 *            the offset to look for the expected character
	 * @param expected
	 *            the expected character
	 * @return true if the expected character exist at the given offset
	 */
	private static boolean checkOffset(String value, int offset, char expected) {
		return (offset < value.length()) && (value.charAt(offset) == expected);
	}

	/**
	 * Parse an integer located between 2 given offsets in a string
	 * 
	 * @param value
	 *            the string to parse
	 * @param beginIndex
	 *            the start index for the integer in the string
	 * @param endIndex
	 *            the end index for the integer in the string
	 * @return the int
	 * @throws NumberFormatException
	 *             if the value is not a number
	 */
	private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
		if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
			throw new NumberFormatException(value);
		}
		// use same logic as in Integer.parseInt() but less generic we're not
		// supporting negative values
		int i = beginIndex;
		int result = 0;
		int digit;
		if (i < endIndex) {
			digit = Character.digit(value.charAt(i++), 10);
			if (digit < 0) {
				throw new NumberFormatException("Invalid number: " + value);
			}
			result = -digit;
		}
		while (i < endIndex) {
			digit = Character.digit(value.charAt(i++), 10);
			if (digit < 0) {
				throw new NumberFormatException("Invalid number: " + value);
			}
			result *= 10;
			result -= digit;
		}
		return -result;
	}

	/**
	 * Zero pad a number to a specified length
	 * 
	 * @param buffer
	 *            buffer to use for padding
	 * @param value
	 *            the integer value to pad if necessary.
	 * @param length
	 *            the length of the string we should zero pad
	 */
	private static void padInt(StringBuilder buffer, int value, int length) {
		String strValue = Integer.toString(value);
		for (int i = length - strValue.length(); i > 0; i--) {
			buffer.append('0');
		}
		buffer.append(strValue);
	}
}
