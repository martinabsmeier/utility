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
package de.marabs.common.utility.io;

import java.io.File;

import javax.swing.JFileChooser;

import de.marabs.common.utility.io.filter.FileExtensionFilter;
import lombok.Builder;

/**
 * TODO Insert description !
 * 
 * @author Martin Absmeier
 */
@Builder
public class OpenFileDialog {

	private FileExtensionFilter filter;


	/**
	 * Opens a File with JFileChooser
	 *
	 * @return the selected file or null if the dialog is canceled
	 */
	public File withFileDialog() {
		return withFileDialog(System.getProperty("user.dir"));
	}	
	
	/**
	 * Opens a File with JFileChooser
	 * 
	 * @return the selected file or null if the dialog is canceled
	 */
	public File withFileDialog(String directoryPath) {
		File selectedFile = null;

		JFileChooser fc = new JFileChooser(directoryPath);
		fc.setFileFilter(this.filter);
		int state = fc.showOpenDialog(null);

		switch (state) {
			case JFileChooser.APPROVE_OPTION:
				selectedFile = fc.getSelectedFile();
			break;
			case JFileChooser.CANCEL_OPTION:
				selectedFile = null;
			break;
			default:
				selectedFile = null;
			break;
		}

		return selectedFile;
	}
}