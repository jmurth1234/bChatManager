package net.rymate.bchatmanager.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * SLAPI = Saving/Loading API API for Saving and Loading Objects.
 * 
 * Lazily borrowed from the Bukkit Wiki so I can persist my storage maps :3
 * 
 * @author Tomsik68
 */
public class SLAPI {

    public static void save(Object obj, String path) throws Exception {
	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
		path));
	oos.writeObject(obj);
	oos.flush();
	oos.close();
    }

    public static Object load(String path) throws Exception {
	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
	Object result = ois.readObject();
	ois.close();
	return result;
    }
}
