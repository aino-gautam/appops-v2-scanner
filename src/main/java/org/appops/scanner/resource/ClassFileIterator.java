
package org.appops.scanner.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.appops.scanner.Filter;

/**
 * Used to iterate classes in a folder recursively. So can be used to scan folders
 * containing compiled classes
 * 
 * @author Debasish Gautam Anand
 *
 */
public class ClassFileIterator implements ResourceIterator {

	/** files. */
	private List<File> files;

	/** The index. */
	private int index = 0;

	/**
	 * Instantiates a new class file iterator.
	 * 
	 * @param file
	 * @param filter
	 */
	public ClassFileIterator(File file, Filter filter) {
		files = new ArrayList<File>();
		try {
			init(files, file, filter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 
	 * Recursive method to fetch all classes in a folder and its sub-folders recursively
	 *  
	 * @param list
	 * @param dir
	 * @param filter
	 * @throws Exception
	 */
	private static void init(List<File> list, File dir, Filter filter) throws Exception {
		File[] files = dir.listFiles(); // gets a list of files in directory
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				init(list, files[i], filter); // recursion till all files are added to the list
			} else {
				if (filter == null || filter.accepts(files[i].getAbsolutePath())) {
					list.add(files[i]);
				}
			}
		}
	}

	/**
	 * returns the file as a stream and moves to next one 
	 *  @see org.appops.scanner.resource.ResourceIterator#next() 
	 *  
	 *  */
	@Override
	public final InputStream next() {
		if (index >= files.size()) {
			return null;
		}
		File fp = (File) files.get(index++);
		try {
			return new FileInputStream(fp);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/* @see org.appops.scanner.resource.ResourceIterator#close() */
	@Override
	public void close() {
		// DO Nothing
	}
}
