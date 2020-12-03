/*

Copyright 2008 TOPdesk, the Netherlands

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package org.mangosdk.spi.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;


class Persistence {

	
	private final String name;
	private final String path;
	final Filer filer;
	final Logger logger;

	Persistence(String name, String root, Filer filer, Logger logger) {
		this.name = name;
		this.logger = logger;
		this.path = root + "META-INF/services/";
		this.filer = filer;
	}
	
	void writeLog() {
		try {
			String logContent = logger.getFileContent();
			if (logContent != null && !logContent.isEmpty()) {
				write("log" + System.currentTimeMillis() + ".log", logContent);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Collection<String> tryFind() {
		Collection<String> fileList;
		File dir = determineOutputLocation();
		if (dir == null) {
			fileList = Collections.emptyList();
		} else {
			fileList = listDiscoveredServiceFiles(dir.listFiles(ServiceFileFilter.INSTANCE));
		}
		return fileList;
	}

	private Collection<String> listDiscoveredServiceFiles(File[] list) {
		if (list == null) {
			return Collections.emptyList();
		}
		
		List<String> result = new ArrayList<String>();
		for (File file : list) {
			String fileName = file.getName();
			logger.note(LogLocation.LOG_FILE, "Discovered " + fileName);
			result.add(fileName);
		}
		return result;
	}

	private File determineOutputLocation() {
		FileObject resource;
		try {
			resource = filer.getResource(StandardLocation.CLASS_OUTPUT, "", path + "locator");
		} 
		catch (FileNotFoundException e) {
			// Could happen
			return null;
		}
		catch (IOException e) {
			logger.note(LogLocation.MESSAGER, "IOException while determining output location: " + e.getMessage());
//			System.out.println(e);
			return null;
		}
		catch (IllegalArgumentException e) {
			// Happens when the path is invalid. For instance absolute or relative to a path 
			// not part of the class output folder.
			//
			// Due to a bug in javac for Linux, this also occurs when no output path is specified 
			// for javac using the -d parameter.
			// See http://forums.sun.com/thread.jspa?threadID=5240999&tstart=45
			// and http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6647996
			
//			logger.toConsole("IllegalArgumentException: " + e.getMessage());
			return null;
		}
		
		URI uri = resource.toUri();
		if (uri.isAbsolute()) {
			return new File(uri).getParentFile();
		}
		return new File(uri.toString()).getParentFile();
	}
	
	Initializer getInitializer() {
		return new ServiceFileInitializer(filer, path, logger);
	}
	
	void write(String serviceName, String value) throws IOException {
		logger.note(LogLocation.BOTH, "Generating file '" + path + serviceName + "'");
		FileObject output = filer.createResource(StandardLocation.CLASS_OUTPUT, "", path + serviceName);
		Writer writer = output.openWriter();
		try {
			writer.write("# Generated by " + name + "\n");
			writer.write("# " + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US).format(new Date()) + "\n");
			writer.write(value);
		}
		finally {
			try {
				writer.close();
			}
			catch (IOException e) {
				// Ignore
			}
		}
	}
}
