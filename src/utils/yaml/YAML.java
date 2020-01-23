package utils.yaml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import utils.FlixBlocksUtils;

public class YAML {

	TreeMap<String, Object> tree;

	public YAML() {
		this(new TreeMap<>());
	}

	YAML(TreeMap<String, Object> tree) {
		this.tree = tree;
	}

	// =========================================================================================================================

	public boolean contains(String path) {
		YAML yaml = get(path);
		return yaml != null && yaml.tree.containsKey(lastKey(path));
	}

	public String lastKey(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	// =========================================================================================================================
	// Getters

	public YAML get(String path) {
		return get(path, false);
	}

	/** force = true Will create the YAML object at the given path */
	public YAML get(String path, boolean force) {
		String[] keys = path.split("\\.");
		YAML yaml = this;

		for (int i = 0; i < keys.length - 1; i++)
			if (yaml.tree.containsKey(keys[i]))
				yaml = (YAML) yaml.tree.get(keys[i]);
			else if (force) {
				yaml.tree.put(keys[i], new YAML());
				yaml = (YAML) yaml.tree.get(keys[i]);
			} else
				return null;

		return yaml;
	}

	public Object getObject(String path) {
		return get(path).tree.get(lastKey(path));
	}

	public YAML getYAML(String path) {
		return (YAML) getObject(path);
	}

	public String getString(String path) {
		return (String) getObject(path);
	}

	public int getInt(String path) {
		return Integer.valueOf(getString(path));
	}

	public double getDouble(String path) {
		return Double.valueOf(getString(path));
	}

	public int getHexa(String path) {
		return FlixBlocksUtils.parseHexa((getString(path)));
	}

	public boolean getBoolean(String path) {
		return Boolean.valueOf(getString(path));
	}

	public int[][] getHexaDoubleArray(String path) {
		Object[] objects = (Object[]) (getObject(path));

		int[][] hexa = new int[objects.length][];

		for (int i = 0; i < objects.length; i++) {
			String[] strings = (String[]) objects[i];
			hexa[i] = new int[strings.length];

			for (int j = 0; j < strings.length; j++)
				hexa[i][j] = FlixBlocksUtils.parseHexa(strings[j]);
		}

		return hexa;
	}

	public int[] getHexaDoubleArrayInline(String path, int size) {
		Object[] objects = (Object[]) (getObject(path));

		int[] hexa = new int[size];

		for (int i = 0; i < objects.length; i++) {
			String[] strings = (String[]) objects[i];

			for (int j = 0; j < strings.length; j++)
				hexa[i * strings.length + j] = FlixBlocksUtils.parseHexa(strings[j]);
		}

		return hexa;
	}

	// =========================================================================================================================
	// Setters

	public void put(String path, Object obj) {
		get(path, true).tree.put(lastKey(path), obj);
	}

	public void putHexa(String path, int hexa) {
		put(path, FlixBlocksUtils.hexaToString(hexa));
	}

	public void putHexaDoubleArrayInline(String path, int[] array, int width) {
		String[][] doubleArray = new String[array.length / width][width];
		for (int col = 0; col < width; col++)
			for (int row = 0; row < doubleArray.length; row++)
				doubleArray[row][col] = FlixBlocksUtils.hexaToString(array[row * width + col]);
		put(path, doubleArray);
	}

	// =========================================================================================================================
	// =========================================================================================================================

	static class Int {
		int value;

		Int(int i) {
			this.value = i;
		}
	}

	// =========================================================================================================================
	// Parser

	public static YAML decode(ArrayList<String> list) {
		return decode(list, new Int(0), 0);
	}

	public static YAML decode(ArrayList<String> list, Int index, int decal) {
		YAML yaml = new YAML();

		for (; index.value < list.size(); index.value++) {
			if (!isAligned(list, index, decal))
				break;

			String[] elems = list.get(index.value).split(":");

			if (elems.length != 2) {
				if (elems.length == 1) {

					// Test if it is a List
					if (isAList(list, index.value)) {
						index.value++;
						yaml.put(elems[0].trim(), decodeList(list, index, decal + 4));
						continue;
					}
					// Decode child node
					else {
						index.value++;
						yaml.put(elems[0].trim(), decode(list, index, decal + 4));
						index.value--;
						continue;
					}
				}
				// Wrong number of ':'
				System.err.println("[YAML]: " + (elems.length - 1) + " ':' on a line => " + list.get(index.value));
			} else {
				yaml.put(elems[0].trim(), decodeLine(elems[1]));
			}
		}
		return yaml;
	}

	public static boolean isAList(ArrayList<String> list, int index) {
		return list.size() > index + 1 && list.get(index + 1).contains("[");
	}

	public static Object decodeList(ArrayList<String> list, Int index, int decal) {
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

	public static String[] decodeArray(String line) {
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

	public static String encode(YAML yaml) {
		StringWriter sw = new StringWriter();

		encode(sw, yaml, 0);

		return sw.toString();
	}

	public static void encode(StringWriter sw, YAML yaml, int decal) {
		for (String name : yaml.tree.keySet()) {
			decal(sw, decal);

			sw.write(name);
			sw.write(": ");

			Object obj = yaml.tree.get(name);
			Class<? extends Object> c = obj.getClass();

			if (c == String.class || c == Integer.class || c == Boolean.class) {// Variables
				sw.write(String.valueOf(obj));
				sw.write("\r\n");
			} else if (c == String[][].class) {// Double array
				sw.write("\r\n");
				encodeDoubleArray(sw, (String[][]) yaml.getObject(name), decal + 4);
			} else if (c == YAML.class) {// Child node
				sw.write("\r\n");
				encode(sw, (YAML) obj, decal + 4);
			} else
				System.err.println("[YAML] " + c);
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

	public static YAML parseFile(String file) {
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

	public static void encodeFile(YAML yaml, String file) {
		FlixBlocksUtils.write(encode(yaml), file);
	}
}
