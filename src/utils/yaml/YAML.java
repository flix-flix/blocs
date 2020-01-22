package utils.yaml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import utils.FlixBlocksUtils;

public class YAML {

	static class Int {
		int value;

		Int(int i) {
			this.value = i;
		}
	}

	// =========================================================================================================================
	// Parser

	public static TreeMap<String, Object> decode(ArrayList<String> list) {
		return decode(list, new Int(0), 0);
	}

	public static TreeMap<String, Object> decode(ArrayList<String> list, Int index, int decal) {
		TreeMap<String, Object> tree = new TreeMap<>();

		for (; index.value < list.size(); index.value++) {
			if (!isAligned(list, index, decal))
				break;

			String[] elems = list.get(index.value).split(":");

			if (elems.length != 2) {
				if (elems.length == 1) {

					// Test if it is a List
					if (isAList(list, index.value)) {
						index.value++;
						tree.put(elems[0].trim(), decodeList(list, index, decal + 4));
						continue;
					}
					// Decode child node
					else {
						index.value++;
						tree.put(elems[0].trim(), decode(list, index, decal + 4));
						index.value--;
						continue;
					}
				}
				// Wrong number of ':'
				System.err.println("[YAML]: " + (elems.length - 1) + " ':' on a line => " + list.get(index.value));
			} else {
				tree.put(elems[0].trim(), decodeLine(elems[1]));
			}
		}
		return tree;
	}

	public static boolean isAList(ArrayList<String> list, int index) {
		return list.size() > index + 1 && list.get(index + 1).contains("-");
	}

	public static Object[] decodeList(ArrayList<String> list, Int index, int decal) {
		ArrayList<Object> array = new ArrayList<>();

		for (; index.value < list.size(); index.value++) {
			if (!isAligned(list, index, decal))
				break;

			if (!list.get(index.value).contains("-"))
				break;

			array.add(decodeLine(list.get(index.value)));
		}

		index.value--;
		return array.toArray();
	}

	public static Object[] decodeArray(String line) {
		String[] elems = line.substring(line.indexOf('[') + 1, line.indexOf(']')).split(",");

		for (int i = 0; i < elems.length; i++)
			elems[i] = elems[i].trim();

		return elems;
	}

	public static Object decodeLine(String line) {
		return line.contains("[") ? decodeArray(line) : line.trim();
	}

	public static boolean isAligned(ArrayList<String> list, Int index, int decal) {
		for (int i = 0; i < decal; i++)
			if (list.get(index.value).charAt(i) != ' ')
				return false;
		return true;
	}

	// =========================================================================================================================
	// Encoder

	public static String encode(TreeMap<String, Object> tree) {
		StringWriter sw = new StringWriter();

		encode(sw, tree, 0);

		return sw.toString();
	}

	public static void encode(StringWriter sw, TreeMap<String, Object> tree, int decal) {
		for (String name : tree.keySet()) {
			decal(sw, decal);

			sw.write(name);
			sw.write(": ");

			Class<? extends Object> c = tree.get(name).getClass();

			if (c == String.class || c == Integer.class || c == boolean.class) {
				sw.write(String.valueOf(tree.get(name)));
				sw.write("\r\n");
			} else if (c == String[][].class) {
				sw.write("\r\n");
				encodeDoubleArray(sw, (String[][]) tree.get(name), decal + 4);
			} else if (c == TreeMap.class) {
				sw.write("\r\n");
				encode(sw, (TreeMap<String, Object>) tree.get(name), decal + 4);
			} else
				System.err.println("[YAML] class: " + c);
		}
	}

	public static void decal(StringWriter sw, int decal) {
		for (int i = 0; i < decal; i++)
			sw.write(" ");
	}

	public static void encodeDoubleArray(StringWriter sw, String[][] array, int decal) {
		for (String[] objs : array) {
			decal(sw, decal);
			sw.write("- [");
			sw.write(String.join(", ", Arrays.asList(objs)));
			sw.write("]\r\n");
		}
	}

	// =========================================================================================================================

	public static TreeMap<String, Object> parseFile(String file) {
		String[] lines = FlixBlocksUtils.read(file).split("\r\n");
		ArrayList<String> list = new ArrayList<>();

		for (String line : lines) {
			// Ignore empty lines
			if (line.trim().isEmpty())
				continue;

			// Removes last spaces
			int end = line.length();
			while (line.charAt(--end) == ' ')
				;

			list.add(line.substring(0, end + 1));
		}

		return decode(list);
	}

	public static void encodeFile(TreeMap<String, Object> tree, String file) {
		FlixBlocksUtils.write(encode(tree), file);
	}
}
