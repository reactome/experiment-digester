package org.reactome.server.tools;

import org.reactome.server.data.model.Experiment;

import java.io.*;
import java.net.URL;
import java.util.*;
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

    public Experiment createExperiment(Integer id, BufferedReader reader, URL target) {
        if(reader == null) throw new IllegalArgumentException();

        cursor = 0;
        headerFound = false;
        experiment = new Experiment(id, target);

        reader.lines().forEach(this::readLine);

        experiment.setKeyColumn("Gene Name"); // The second column is the primary column of the sample
        experiment.setTissuesIndex(Arrays.asList("Gene ID",  "Gene Name"));
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
            if(matcher.find()) {
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
            // Fill empty values with 0
            row = row.stream()
                     .map(v -> (v == null || v.isEmpty()) ? "0" : v)
                     .collect(Collectors.toList());

            experiment.insertData(row);
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
