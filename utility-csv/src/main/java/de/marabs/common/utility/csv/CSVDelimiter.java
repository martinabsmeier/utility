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
package de.marabs.common.utility.csv;

import java.io.Serializable;

/**
 * {@code CSVDelimiter} represents the possible delimiters of a csv document.
 * 
 * @author Martin Absmeier
 */
public enum CSVDelimiter implements Serializable {

	/** Comma delimiter {@code ,} */
	COMMA(","),
	/** Semikolon delimiter {@code ;} */
	SEMIKOLON(";"),
	/** Tabulator delimiter {@code /t} */
	TABULATOR("\t"),
	/** Colon delimiter {@code :} */
	COLON(":"),
	/** Space delimiter {@code ' '} */
	SPACE(" ");

	private final String value;

	CSVDelimiter(String value) {
		this.value = value;
	}

	/**
	 * Return the delimiter as string.
	 * 
	 * @return The delimiter as string.
	 */
	public String getValue() {
		return value;
	}

}