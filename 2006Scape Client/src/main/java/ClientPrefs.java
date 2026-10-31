import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Settings that survive between launches, stored next to the cache as
 * client_settings.properties. Values are only read before the game starts
 * and written whenever a setting changes or the window closes.
 */
final class ClientPrefs {

	private static final Properties props = new Properties();
	private static boolean loaded;

	private static File file() {
		return new File(Signlink.findcachedir(), "client_settings.properties");
	}

	static synchronized void load() {
		if (loaded) {
			return;
		}
		loaded = true;
		File f = file();
		if (!f.exists()) {
			return;
		}
		try (InputStream in = new FileInputStream(f)) {
			props.load(in);
		} catch (Exception e) {
			System.out.println("Could not read client settings: " + e);
		}
	}

	static synchronized void save() {
		try (OutputStream out = new FileOutputStream(file())) {
			props.store(out, "2006Scape client settings");
		} catch (Exception e) {
			System.out.println("Could not save client settings: " + e);
		}
	}

	static boolean has(String key) {
		load();
		return props.containsKey(key);
	}

	static boolean getBool(String key, boolean def) {
		load();
		String v = props.getProperty(key);
		return v == null ? def : Boolean.parseBoolean(v);
	}

	static int getInt(String key, int def) {
		load();
		try {
			return Integer.parseInt(props.getProperty(key));
		} catch (Exception e) {
			return def;
		}
	}

	static String getString(String key, String def) {
		load();
		return props.getProperty(key, def);
	}

	static void set(String key, Object value) {
		load();
		props.setProperty(key, String.valueOf(value));
	}
}
