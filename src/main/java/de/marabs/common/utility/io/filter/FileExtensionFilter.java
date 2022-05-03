/*
 * Copyright 2022 Martin Absmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.marabs.common.utility.io.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * TODO Insert description !
 * 
 * @author Martin Absmeier
 */
public class FileExtensionFilter extends FileFilter {

	private String[] permittedExtension;

	/** Standard Constructor */
	public FileExtensionFilter(String[] permittedExtension) {
		super();
		this.permittedExtension = permittedExtension;
	}

	@Override
	public boolean accept(File file) {
		String fileName = file.getName().toLowerCase();
		return file.isDirectory() || checkFileName(fileName);
	}

	@Override
	public String getDescription() {
		return "no description available";
	}

	/* ----------------------------------------------------------------- */

	private boolean checkFileName(String fileName) {
		for (int i = 0; i < permittedExtension.length; i++) {
			String ext = permittedExtension[i];
			if (fileName.endsWith(ext)) {
				return true;
			}
		}

		return false;
	}
}
