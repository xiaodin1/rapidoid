package org.rapidoid.util;

/*
 * #%L
 * rapidoid-u
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class U {

	protected static final Random RND = new Random();
	private static ScheduledThreadPoolExecutor EXECUTOR;
	private static long measureStart;

	// regex taken from
	// http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
	private static Pattern CAMEL_SPLITTER_PATTERN = Pattern
			.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])");

	private static Pattern PLURAL1 = Pattern.compile(".*(s|x|z|ch|sh)$");
	private static Pattern PLURAL1U = Pattern.compile(".*(S|X|Z|CH|SH)$");
	private static Pattern PLURAL2 = Pattern.compile(".*[bcdfghjklmnpqrstvwxz]o$");
	private static Pattern PLURAL2U = Pattern.compile(".*[BCDFGHJKLMNPQRSTVWXZ]O$");
	private static Pattern PLURAL3 = Pattern.compile(".*[bcdfghjklmnpqrstvwxz]y$");
	private static Pattern PLURAL3U = Pattern.compile(".*[BCDFGHJKLMNPQRSTVWXZ]Y$");

	private U() {
	}

	public static String text(Object obj) {
		if (obj == null) {
			return "null";
		} else if (obj instanceof byte[]) {
			return Arrays.toString((byte[]) obj);
		} else if (obj instanceof short[]) {
			return Arrays.toString((short[]) obj);
		} else if (obj instanceof int[]) {
			return Arrays.toString((int[]) obj);
		} else if (obj instanceof long[]) {
			return Arrays.toString((long[]) obj);
		} else if (obj instanceof float[]) {
			return Arrays.toString((float[]) obj);
		} else if (obj instanceof double[]) {
			return Arrays.toString((double[]) obj);
		} else if (obj instanceof boolean[]) {
			return Arrays.toString((boolean[]) obj);
		} else if (obj instanceof char[]) {
			return Arrays.toString((char[]) obj);
		} else if (obj instanceof Object[]) {
			return text((Object[]) obj);
		} else {
			return String.valueOf(obj);
		}
	}

	public static String text(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < objs.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(text(objs[i]));
		}

		sb.append("]");

		return sb.toString();
	}

	public static RuntimeException rte(String message, Object... args) {
		return new RuntimeException(readable(message, args));
	}

	public static RuntimeException rte(Throwable cause) {
		return new RuntimeException(cause);
	}

	public static RuntimeException rte(String message) {
		return new RuntimeException(message);
	}

	public static RuntimeException notExpected() {
		return rte("This operation is not expected to be called!");
	}

	public static IllegalArgumentException illegalArg(String message) {
		return new IllegalArgumentException(message);
	}

	public static <T> T or(T value, T fallback) {
		return value != null ? value : fallback;
	}

	public static String format(String s, Object... args) {
		return String.format(s, args);
	}

	public static String readable(String format, Object... args) {

		for (int i = 0; i < args.length; i++) {
			args[i] = text(args[i]);
		}

		return String.format(format, args);
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new ThreadDeath();
		}
	}

	public static String text(Collection<Object> coll) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		boolean first = true;

		for (Object obj : coll) {
			if (!first) {
				sb.append(", ");
			}

			sb.append(text(obj));
			first = false;
		}

		sb.append("]");
		return sb.toString();
	}

	public static String text(Iterator<?> it) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		boolean first = true;

		while (it.hasNext()) {
			if (first) {
				sb.append(", ");
				first = false;
			}

			sb.append(text(it.next()));
		}

		sb.append("]");

		return sb.toString();
	}

	public static String textln(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < objs.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append("\n  ");
			sb.append(text(objs[i]));
		}

		sb.append("\n]");

		return sb.toString();
	}

	public static String replaceText(String s, String[][] repls) {
		for (String[] repl : repls) {
			s = s.replaceAll(Pattern.quote(repl[0]), repl[1]);
		}
		return s;
	}

	public static <T> String join(String sep, T... items) {
		return render(items, "%s", sep);
	}

	public static String join(String sep, Iterable<?> items) {
		return render(items, "%s", sep);
	}

	public static String join(String sep, char[][] items) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				sb.append(sep);
			}
			sb.append(items[i]);
		}

		return sb.toString();
	}

	public static String render(Object[] items, String itemFormat, String sep) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				sb.append(sep);
			}
			sb.append(readable(itemFormat, items[i]));
		}

		return sb.toString();
	}

	public static String render(Iterable<?> items, String itemFormat, String sep) {
		StringBuilder sb = new StringBuilder();

		int i = 0;
		Iterator<?> it = items.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			if (i > 0) {
				sb.append(sep);
			}

			sb.append(readable(itemFormat, item));
			i++;
		}

		return sb.toString();
	}

	public static <T> T[] array(T... items) {
		return items;
	}

	public static <T> Iterator<T> arrayIterator(T[] arr) {
		return Arrays.asList(arr).iterator();
	}

	public static <T> Set<T> set(Collection<? extends T> coll) {
		Set<T> set = new LinkedHashSet<T>();
		set.addAll(coll);
		return set;
	}

	public static <T> Set<T> set(T... values) {
		Set<T> set = new LinkedHashSet<T>();

		for (T val : values) {
			set.add(val);
		}

		return set;
	}

	public static <T> List<T> list(Collection<? extends T> coll) {
		List<T> list = new ArrayList<T>();
		list.addAll(coll);
		return list;
	}

	public static <T> List<T> list(T... values) {
		List<T> list = new ArrayList<T>();

		for (T item : values) {
			list.add(item);
		}

		return list;
	}

	public static <K, V> Map<K, V> map(Map<? extends K, ? extends V> src) {
		Map<K, V> map = map();
		map.putAll(src);
		return map;
	}

	public static <K, V> Map<K, V> map() {
		return new HashMap<K, V>();
	}

	public static <K, V> Map<K, V> map(K key, V value) {
		Map<K, V> map = map();
		map.put(key, value);
		return map;
	}

	public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2) {
		Map<K, V> map = map(key1, value1);
		map.put(key2, value2);
		return map;
	}

	public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3) {
		Map<K, V> map = map(key1, value1, key2, value2);
		map.put(key3, value3);
		return map;
	}

	public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
		Map<K, V> map = map(key1, value1, key2, value2, key3, value3);
		map.put(key4, value4);
		return map;
	}

	public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5,
			V value5) {
		Map<K, V> map = map(key1, value1, key2, value2, key3, value3, key4, value4);
		map.put(key5, value5);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> map(Object... keysAndValues) {
		must(keysAndValues.length % 2 == 0, "Incorrect number of arguments (expected key-value pairs)!");

		Map<K, V> map = map();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			map.put((K) keysAndValues[i * 2], (V) keysAndValues[i * 2 + 1]);
		}

		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(Map<? extends K, ? extends V> src) {
		ConcurrentMap<K, V> map = concurrentMap();
		map.putAll(src);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key, V value) {
		ConcurrentMap<K, V> map = concurrentMap();
		map.put(key, value);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1);
		map.put(key2, value2);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2, K key3, V value3) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1, key2, value2);
		map.put(key3, value3);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2, K key3, V value3,
			K key4, V value4) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1, key2, value2, key3, value3);
		map.put(key4, value4);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2, K key3, V value3,
			K key4, V value4, K key5, V value5) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1, key2, value2, key3, value3, key4, value4);
		map.put(key5, value5);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ConcurrentMap<K, V> concurrentMap(Object... keysAndValues) {
		must(keysAndValues.length % 2 == 0, "Incorrect number of arguments (expected key-value pairs)!");

		ConcurrentMap<K, V> map = concurrentMap();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			map.put((K) keysAndValues[i * 2], (V) keysAndValues[i * 2 + 1]);
		}

		return map;
	}

	public static <T> Queue<T> queue(int maxSize) {
		return maxSize > 0 ? new ArrayBlockingQueue<T>(maxSize) : new ConcurrentLinkedQueue<T>();
	}

	public static long time() {
		return System.currentTimeMillis();
	}

	public static boolean xor(boolean a, boolean b) {
		return a && !b || b && !a;
	}

	public static boolean eq(Object a, Object b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		return a.equals(b);
	}

	public static void failIf(boolean failureCondition, String msg) {
		if (failureCondition) {
			throw rte(msg);
		}
	}

	public static void failIf(boolean failureCondition, String msg, Object... args) {
		if (failureCondition) {
			throw rte(msg, args);
		}
	}

	public static boolean must(boolean expectedCondition, String message) {
		if (!expectedCondition) {
			throw rte(message);
		}
		return true;
	}

	public static String copyNtimes(String s, int n) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static RuntimeException rte(String message, Throwable cause, Object... args) {
		return new RuntimeException(readable(message, args), cause);
	}

	public static RuntimeException rte(String message, Throwable cause) {
		return new RuntimeException(message, cause);
	}

	public static boolean must(boolean expectedCondition) {
		if (!expectedCondition) {
			throw rte("Expectation failed!");
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, long arg) {
		if (!expectedCondition) {
			throw rte(message, arg);
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg) {
		if (!expectedCondition) {
			throw rte(message, text(arg));
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg1, Object arg2) {
		if (!expectedCondition) {
			throw rte(message, text(arg1), text(arg2));
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg1, Object arg2, Object arg3) {
		if (!expectedCondition) {
			throw rte(message, text(arg1), text(arg2), text(arg3));
		}
		return true;
	}

	public static void secure(boolean condition, String msg) {
		if (!condition) {
			throw new SecurityException(readable(msg));
		}
	}

	public static void secure(boolean condition, String msg, Object arg) {
		if (!condition) {
			throw new SecurityException(readable(msg, arg));
		}
	}

	public static void secure(boolean condition, String msg, Object arg1, Object arg2) {
		if (!condition) {
			throw new SecurityException(readable(msg, arg1, arg2));
		}
	}

	public static void bounds(int value, int min, int max) {
		must(value >= min && value <= max, "%s is not in the range [%s, %s]!", value, min, max);
	}

	public static void notNullAll(Object... items) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				throw rte("The item[%s] must NOT be null!", i);
			}
		}
	}

	public static <T> T notNull(T value, String desc, Object... descArgs) {
		if (value == null) {
			throw rte("%s must NOT be null!", readable(desc, descArgs));
		}

		return value;
	}

	public static RuntimeException notReady() {
		return rte("Not yet implemented!");
	}

	public static RuntimeException notSupported() {
		return rte("This operation is not supported by this implementation!");
	}

	public static void show(Object... values) {
		String text = values.length == 1 ? text(values[0]) : text(values);
		print(">" + text + "<");
	}

	public static synchronized void schedule(Runnable task, long delay) {
		if (EXECUTOR == null) {
			EXECUTOR = new ScheduledThreadPoolExecutor(3);
		}

		EXECUTOR.schedule(task, delay, TimeUnit.MILLISECONDS);
	}

	public static void startMeasure() {
		measureStart = time();
	}

	public static void endMeasure() {
		long delta = time() - measureStart;
		show(delta + " ms");
	}

	public static void endMeasure(String info) {
		long delta = time() - measureStart;
		show(info + ": " + delta + " ms");
	}

	public static void print(Object value) {
		System.out.println(value);
	}

	public static void printAll(Collection<?> collection) {
		for (Object item : collection) {
			print(item);
		}
	}

	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static boolean isEmpty(Object[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		} else if (value instanceof String) {
			return isEmpty((String) value);
		} else if (value instanceof Object[]) {
			return isEmpty((Object[]) value);
		} else if (value instanceof Collection<?>) {
			return isEmpty((Collection<?>) value);
		} else if (value instanceof Map<?, ?>) {
			return isEmpty((Map<?, ?>) value);
		}
		return false;
	}

	public static String capitalized(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String uncapitalized(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static String mid(String s, int beginIndex, int endIndex) {
		if (endIndex < 0) {
			endIndex = s.length() + endIndex;
		}
		return s.substring(beginIndex, endIndex);
	}

	public static String urlDecode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw rte(e);
		}
	}

	public static String mul(String s, int n) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static int num(String s) {
		return Integer.parseInt(s);
	}

	public static String bytesAsText(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	private static MessageDigest digest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw rte("Cannot find algorithm: " + algorithm);
		}
	}

	public static String md5(byte[] bytes) {
		MessageDigest md5 = digest("MD5");
		md5.update(bytes);
		return bytesAsText(md5.digest());
	}

	public static String md5(String data) {
		return md5(data.getBytes());
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassIfExists(String className) {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static String fillIn(String template, String placeholder, String value) {
		return template.replace("{{" + placeholder + "}}", value);
	}

	public static ByteBuffer expand(ByteBuffer buf, int newSize) {
		ByteBuffer buf2 = ByteBuffer.allocate(newSize);

		ByteBuffer buff = buf.duplicate();
		buff.rewind();
		buff.limit(buff.capacity());

		buf2.put(buff);

		return buf2;
	}

	public static ByteBuffer expand(ByteBuffer buf) {
		int cap = buf.capacity();

		if (cap <= 1000) {
			cap *= 10;
		} else if (cap <= 10000) {
			cap *= 5;
		} else {
			cap *= 2;
		}

		return expand(buf, cap);
	}

	public static String buf2str(ByteBuffer buf) {
		ByteBuffer buf2 = buf.duplicate();

		buf2.rewind();
		buf2.limit(buf2.capacity());

		byte[] bytes = new byte[buf2.capacity()];
		buf2.get(bytes);

		return new String(bytes);
	}

	public static ByteBuffer buf(String s) {
		byte[] bytes = s.getBytes();

		ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);
		buf.put(bytes);
		buf.rewind();

		return buf;
	}

	public static void benchmark(String name, int count, Runnable runnable) {
		long start = time();

		for (int i = 0; i < count; i++) {
			runnable.run();
		}

		benchmarkComplete(name, count, start);
	}

	public static void benchmarkComplete(String name, int count, long startTime) {
		long end = time();
		long ms = end - startTime;

		if (ms == 0) {
			ms = 1;
		}

		double avg = ((double) count / (double) ms);

		String avgs = avg > 1 ? Math.round(avg) + "K" : Math.round(avg * 1000) + "";

		String data = format("%s: %s in %s ms (%s/sec)", name, count, ms, avgs);

		print(data + " | " + getCpuMemStats());
	}

	public static void benchmarkMT(int threadsN, final String name, final int count, final CountDownLatch outsideLatch,
			final Runnable runnable) {

		eq(count % threadsN, 0);
		final int countPerThread = count / threadsN;

		final CountDownLatch latch = outsideLatch != null ? outsideLatch : new CountDownLatch(threadsN);

		long time = time();

		for (int i = 1; i <= threadsN; i++) {
			new Thread() {
				public void run() {
					benchmark(name, countPerThread, runnable);
					if (outsideLatch == null) {
						latch.countDown();
					}
				};
			}.start();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw rte(e);
		}

		benchmarkComplete("avg(" + name + ")", threadsN * countPerThread, time);
	}

	public static void benchmarkMT(int threadsN, final String name, final int count, final Runnable runnable) {
		benchmarkMT(threadsN, name, count, null, runnable);
	}

	public static String getCpuMemStats() {
		Runtime rt = Runtime.getRuntime();
		long totalMem = rt.totalMemory();
		long maxMem = rt.maxMemory();
		long freeMem = rt.freeMemory();
		long usedMem = totalMem - freeMem;
		int megs = 1024 * 1024;

		String msg = "MEM [total=%s MB, used=%s MB, max=%s MB]";
		return format(msg, totalMem / megs, usedMem / megs, maxMem / megs);
	}

	public static String camelSplit(String s) {
		return CAMEL_SPLITTER_PATTERN.matcher(s).replaceAll(" ");
	}

	public static String camelPhrase(String s) {
		return capitalized(camelSplit(s).toLowerCase());
	}

	public static int limit(int min, int value, int max) {
		return Math.min(Math.max(min, value), max);
	}

	public static String plural(String s) {
		if (isEmpty(s)) {
			return s;
		}

		if (PLURAL1.matcher(s).matches()) {
			return s + "es";
		} else if (PLURAL2.matcher(s).matches()) {
			return s + "es";
		} else if (PLURAL3.matcher(s).matches()) {
			return mid(s, 0, -1) + "ies";
		} else if (PLURAL1U.matcher(s).matches()) {
			return s + "ES";
		} else if (PLURAL2U.matcher(s).matches()) {
			return s + "ES";
		} else if (PLURAL3U.matcher(s).matches()) {
			return mid(s, 0, -1) + "IES";
		} else {
			boolean upper = Character.isUpperCase(s.charAt(s.length() - 1));
			return s + (upper ? "S" : "s");
		}
	}

	public static Throwable rootCause(Throwable e) {
		while (e.getCause() != null) {
			e = e.getCause();
		}
		return e;
	}

	public static <T> T single(Collection<T> coll) {
		must(coll.size() == 1, "Expected exactly 1 items, but found: %s!", coll.size());
		return coll.iterator().next();
	}

	public static <T> T singleOrNone(Collection<T> coll) {
		must(coll.size() <= 1, "Expected 0 or 1 items, but found: %s!", coll.size());
		return !coll.isEmpty() ? coll.iterator().next() : null;
	}

	@SuppressWarnings("unchecked")
	public static <T> int cmp(T val1, T val2) {
		if (val1 == null && val2 == null) {
			return 0;
		} else if (val1 == null) {
			return -1;
		} else if (val2 == null) {
			return 1;
		} else {
			return ((Comparable<T>) val1).compareTo(val2);
		}
	}

	public static char rndChar() {
		return (char) (65 + rnd(26));
	}

	public static String rndStr(int length) {
		return rndStr(length, length);
	}

	public static String rndStr(int minLength, int maxLength) {
		int len = minLength + rnd(maxLength - minLength + 1);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len; i++) {
			sb.append(rndChar());
		}

		return sb.toString();
	}

	public static int rnd(int n) {
		return RND.nextInt(n);
	}

	public static int rndExcept(int n, int except) {
		if (n > 1 || except != 0) {
			while (true) {
				int num = RND.nextInt(n);
				if (num != except) {
					return num;
				}
			}
		} else {
			throw new RuntimeException("Cannot produce such number!");
		}
	}

	public static <T> T rnd(T[] arr) {
		return arr[rnd(arr.length)];
	}

	public static int rnd() {
		return RND.nextInt();
	}

	public static long rndL() {
		return RND.nextLong();
	}

}
