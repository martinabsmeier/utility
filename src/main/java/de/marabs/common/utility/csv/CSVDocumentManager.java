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

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Simplifying the access to csv documents.
 *
 * @author Martin Absmeier
 */
@Data
@Builder
public class CSVDocumentManager implements Serializable {
    private static final long serialVersionUID = -2120653294656008151L;

    @Builder.Default
    private CSVDelimiter delimiter = CSVDelimiter.COMMA;
    @Builder.Default
    private transient Charset encoding = StandardCharsets.UTF_8;

    /**
     * Read a csv file from specified {@code filePath} and {@code hasHeader} option.
     *
     * @param filePath   the path to the cv file
     * @param withHeader true if csv file has a header, false otherwise
     * @return {@link CSVDocument}
     * @throws IOException if an error occurred
     */
    public CSVDocument readDocument(String filePath, boolean withHeader) throws IOException {
        Path path = Paths.get(filePath);

        CSVRow header = null;
        Scanner scanner = new Scanner(path, encoding.name());
        if (withHeader) {
            header = readHeader(scanner);
        }

        List<CSVRow> rows = new ArrayList<>();
        while (scanner.hasNextLine()) {
            List<CSVCell> cells = Arrays.stream(scanner.nextLine().split(delimiter.getValue()))
                .map(token -> CSVCell.builder().value(token).build())
                .collect(Collectors.toList());
            rows.add(CSVRow.builder().cells(cells).build());
        }
        scanner.close();

        return CSVDocument.builder()
            .fileName(path.getFileName().toString())
            .header(header)
            .rows(rows)
            .build();
    }

    // #################################################################################################################
    private CSVRow readHeader(Scanner scanner) {
        if (scanner.hasNextLine()) {
            List<CSVCell> cells = Arrays.stream(scanner.nextLine().split(delimiter.getValue()))
                .map(token -> CSVCell.builder().value(token).build())
                .collect(Collectors.toList());

            return CSVRow.builder().cells(cells).build();
        }
        return null;
    }
}