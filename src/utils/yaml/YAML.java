package utils.yaml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import utils.FlixBlocksUtils;

public class YAML {
	/** Number of spaces shifted for each child node */
	private static int shiftStep = 4;
	/** Data */
	TreeMap<String, Object> tree;

	// =========================================================================================================================

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

	/**
	 * @param path
	 *            - "node.node.key"
	 * @param force
	 *            - true : will create the YAML object at the given path
	 */
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

	@SuppressWarnings("unchecked")
	public ArrayList<YAML> getList(String path) {
		return (ArrayList<YAML>) getObject(path);
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

		for (int y = 0; y < objects.length; y++) {
			String[] strings = (String[]) objects[y];

			for (int x = 0; x < strings.length; x++)
				hexa[y * strings.length + x] = FlixBlocksUtils.parseHexa(strings[x]);
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
		for (int x = 0; x < width; x++)
			for (int y = 0; y < doubleArray.length; y++)
				doubleArray[y][x] = FlixBlocksUtils.hexaToString(array[y * width + x]);
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

	public static YAML decode(ArrayList<String> lines) {
		return decode(lines, new Int(0), 0);
	}

	/**
	 * @param lines
	 *            - lines of the file
	 * @param index
	 *            - current line index
	 * @param shift
	 *            - number of spaces shifted of the current node
	 */
	public static YAML decode(ArrayList<String> lines, Int index, int shift) {
		YAML yaml = new YAML();

		for (; index.value < lines.size(); index.value++) {
			if (!isAligned(lines, index, shift))
				break;

			String line = lines.get(index.value).trim();
			int colon = line.indexOf(':');

			if (colon == -1) {
				FlixBlocksUtils.debug("No ':'");
				break;
			}

			String key = line.substring(0, colon).trim();

			// Line with only "<Key>:"
			if (colon == line.length() - 1) {
				// Double Array
				if (isADoubleArray(lines, index.value)) {
					index.value++;
					yaml.put(key, decodeList(lines, index, shift + shiftStep));
					continue;
				}
				// Child node
				else {
					index.value++;
					YAML node = decode(lines, index, shift + shiftStep);
					index.value--;

					// List
					if (key.contains("[")) {
						key = key.substring(0, key.indexOf('['));

						if (!yaml.contains(key))
							yaml.put(key, new ArrayList<YAML>());
						yaml.getList(key).add(node);
					}
					// Classic
					else
						yaml.put(key, node);
					continue;
				}
			}
			// Classic line (Key: Value)
			else
				yaml.put(key, decodeLine(line.substring(colon + 1)));
		}
		return yaml;
	}

	public static boolean isADoubleArray(ArrayList<String> lines, int index) {
		return lines.size() > index + 1 && lines.get(index + 1).contains("[");
	}

	public static Object decodeList(ArrayList<String> lines, Int index, int shift) {
		ArrayList<Object> array = new ArrayList<>();

		for (; index.value < lines.size(); index.value++) {
			if (!isAligned(lines, index, shift))
				break;

			if (!lines.get(index.value).contains("-"))
				break;

			array.add(decodeLine(lines.get(index.value)));
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

	public static boolean isAligned(ArrayList<String> lines, Int index, int shift) {
		for (int i = 0; i < shift; i++)
			if (lines.get(index.value).charAt(i) != ' ')
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

	/**
	 * @param sw
	 *            - yaml transcription of the future file
	 * @param yaml
	 *            - object to be encoded
	 * @param shift
	 *            - number of spaces shifted of the current node
	 */
	public static void encode(StringWriter sw, YAML yaml, int shift) {
		for (String name : yaml.tree.keySet()) {

			Object obj = yaml.tree.get(name);
			Class<? extends Object> c = obj.getClass();

			// List
			if (c == ArrayList.class) {
				ArrayList<?> list = (ArrayList<?>) obj;
				for (int i = 0; i < list.size(); i++) {
					shift(sw, shift);
					sw.write(String.format("%s[%d]: \r\n", name, i));
					encode(sw, (YAML) list.get(i), shift + shiftStep);
				}
				continue;
			}

			shift(sw, shift);
			sw.write(name);
			sw.write(": ");

			// Variables
			if (c == String.class || c == Integer.class || c == Boolean.class) {
				sw.write(String.valueOf(obj));
				sw.write("\r\n");
			}
			// Double array
			else if (c == String[][].class) {
				sw.write("\r\n");
				encodeDoubleArray(sw, (String[][]) yaml.getObject(name), shift + shiftStep);
			}
			// Child node
			else if (c == YAML.class) {
				sw.write("\r\n");
				encode(sw, (YAML) obj, shift + shiftStep);
			} else
				FlixBlocksUtils.debug("Wrong class " + c);
		}
	}

	public static void shift(StringWriter sw, int shift) {
		for (int i = 0; i < shift; i++)
			sw.write(" ");
	}

	public static void encodeDoubleArray(StringWriter sw, String[][] array, int shift) {
		for (String[] objs : array) {
			shift(sw, shift);
			sw.write("- [");
			sw.write(String.join(", ", Arrays.asList(objs)));
			sw.write("]\r\n");
		}
	}

	// =========================================================================================================================

	public static YAML parseFile(String file) {
		String[] _lines = FlixBlocksUtils.read(file).split("\r\n");
		ArrayList<String> lines = new ArrayList<>();

		for (String line : _lines) {
			// Detect comment
			int comment = line.indexOf('#');
			if (comment != -1)
				line = line.substring(0, comment);

			// Ignore empty lines
			if (line.trim().isEmpty())
				continue;

			// Removes last spaces
			int end = line.length();
			while (line.charAt(--end) == ' ')
				;

			lines.add(line.substring(0, end + 1));
		}

		return decode(lines);
	}

	public static void encodeFile(YAML yaml, String file) {
		FlixBlocksUtils.write(encode(yaml), file);
	}
}
