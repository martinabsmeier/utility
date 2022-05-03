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

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code CSVRow} represents a row of a {@link CSVDocument}.
 *
 * @author Martin Absmeier
 */
@Data
@Builder
public class CSVRow implements Serializable {
	private static final long serialVersionUID = 9176513247966201544L;

	@Builder.Default
	private List<CSVCell> cells = new ArrayList<>();

}