package org.reactome.server.tools;

import org.reactome.server.data.model.Experiment;

import java.io.BufferedReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class GXAParser {

    private static String METADATA_PREFIX = "#";
    private static String QUERY_PREFIX = "# Query:";
    private static String SELECTED_COLUMNS_PREFIX = "# Selected columns:";
    private static String TIMESTAMP_PREFIX = "# Timestamp:";
    private static String HEADER_SIGNATURE = "Gene";

    Pattern queryPattern = Pattern.compile("experiment\\s*(.*)");

    private int cursor;
    private boolean headerFound = false;
    private Experiment experiment = null;
    private Integer ignoredColumns;

    public Experiment createExperiment(Integer id, BufferedReader reader, String name, URL target) {
        if(reader == null) throw new IllegalArgumentException();

        cursor = 0;
        ignoredColumns = 0;
        headerFound = false;
        experiment = new Experiment(id, target, name); // Name defined by user

        reader.lines().forEach(this::readLine);

        experiment.setKeyColumn("Gene Id"); // The second column is the primary column of the sample
        experiment.setTissuesIndex(Arrays.asList("gene id",  "gene name"));
        experiment.setIgnoredColumns(ignoredColumns);
        return experiment;
    }

    private void readLine(String line) {
        cursor++;
        if(line.startsWith(METADATA_PREFIX)) {
            processMetaData(line);
        } else {
            processData(line);
        }
    }

    private void processMetaData(String line) {
        if(cursor == 1) {                               // Get resource from the first line
            experiment.setResource(line.substring(METADATA_PREFIX.length()).trim());
        } else if(line.startsWith(QUERY_PREFIX)) {      // Get description and name
            experiment.setDescription(line.substring(QUERY_PREFIX.length()).trim());

            Matcher matcher = queryPattern.matcher(line);
            if(matcher.find() && experiment.getName() == null) { //If name has not been set by the user
                experiment.setName(matcher.group(1));
            }
        } else if(line.startsWith(TIMESTAMP_PREFIX)) {  // Get timestamp
            experiment.setTimestamp(line.substring(TIMESTAMP_PREFIX.length()).trim());
        }
    }

    private void processData(String line) {
        String[] items = line.split("\t", -1);

        List<String> row = Arrays.asList(items);

        if(!headerFound && isHeader(line)) {
            headerFound = true;
            experiment.insertHeader(row);
        } else {
            if (Importer.handleNullValues.equalsIgnoreCase(Importer.OMIT)) {
                // Ignore lines with empty values
                if (row.stream().anyMatch(v -> v == null || v.isEmpty())) {
                    ignoredColumns++;
                } else {
                    experiment.insertDataRow(row);
                }
            } else {
                // Fill empty values with what the user has selected (0 by default)
                row = row.stream()
                        .map(v -> (v == null || v.isEmpty()) ? Importer.handleNullValues : v)
                        .collect(Collectors.toList());
                experiment.insertDataRow(row);
            }
        }
    }

    private boolean isHeader(String line) {
        boolean rtn = false;

        if(line.contains(HEADER_SIGNATURE)) { // Assume it is the header line if it contains this string
            rtn = true;
        }
        return rtn;
    }
}
