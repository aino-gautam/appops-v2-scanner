package org.appops.scanner;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.base.Preconditions;

/**
 * This class is responsible for locating all class files on the path specified
 * and returning them as an array of URLs
 * 
 * It can accept three types of parameters to decide how to locate resources.
 * 
 * If an array of URL's pointing to folders or jar's are provided it takes first
 * precendence and other two options are not used
 * 
 * If array of URL's are null it uses the array of strings that should contain
 * path elements to scan.
 * 
 * If both the above are null it tries to retrieve java.classpath system
 * property. If this property is null it uses java.class.path property
 
 * @author Debasish Gautam Anand
 *
 */
public class ClasspathDiscoverer extends Discoverer {

	/** The filter. */
	private Filter filter;
	private String[] arrayPath;
	private URL[] externalUrls;

	/**
	 * Instantiates a new classpath reader.
	 */
	public ClasspathDiscoverer() {
		filter = new FilterImpl();
	}

	public ClasspathDiscoverer(String[] paths, boolean b) {
		filter = new FilterImpl();
		arrayPath = paths;
	}

	public ClasspathDiscoverer(URL[] urls) {
		externalUrls = urls;
		filter = new FilterImpl();
	}

	/**
	 * Uses java.class.path system-property to fetch URLs
	 * 
	 * @return the URL[]
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public final URL[] findResources() {

		if (externalUrls != null) {
			return externalUrls;
		}

		List<URL> list = new ArrayList<URL>();

		if (arrayPath == null) {
			String classpath = System.getProperty("java.classpath");

			if (classpath == null || classpath.isEmpty())
				classpath = System.getProperty("java.class.path");

			System.out.println("Used classpath ----- " + classpath);

			StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);

			list = getUrlListFromTokenizer(tokenizer);

			return list.toArray(new URL[list.size()]);

		} else {
			list = getUrlListFromArrayPath(arrayPath);
		}

		return list.toArray(new URL[list.size()]);
	}

	private List<URL> getUrlListFromArrayPath(String[] arrayPath2) {

		Preconditions.checkNotNull("array of path cannot be null ", arrayPath2);

		List<URL> urlList = new ArrayList<URL>();

		for (String path : arrayPath2) {
			urlList.add(getUrlFromPath(path));
		}

		return urlList;
	}

	private URL getUrlFromPath(String path) {
		try {
			path = java.net.URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		File fp = new File(path);
		if (!fp.exists())
			throw new RuntimeException("File in java.class.path does not exist: " + fp);
		try {
			return fp.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<URL> getUrlListFromTokenizer(StringTokenizer tokenizer) {

		List<URL> urlList = new ArrayList<URL>();

		while (tokenizer.hasMoreTokens()) {
			String path = tokenizer.nextToken();
			urlList.add(getUrlFromPath(path));
		}

		return urlList;
	}

	/* @see org.appops.scanner.Discoverer#getFilter() */
	public final Filter getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 */
	public final void setFilter(Filter filter) {
		this.filter = filter;
	}
}
