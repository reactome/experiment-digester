package org.reactome.server.data.model;

import org.apache.commons.text.WordUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class Experiment {
    private Integer id;
    private String name;
    private String description;
    private String resource;
    private URL url;
    private String timestamp;

    private final Map<String, Integer> headerIndex;
    private Map<String, Integer> tissuesIndex;

    private final List<List<String>> data;
    private Integer keyColumn = 0;
    private Integer ignoredRows = 0;

    private List<String> emptyColumns;

    public Experiment(Integer id, URL url, String definedName) {
        this();
        this.url = url;
        this.id = id;
        this.name = definedName;
    }

    //This is required by Kryo
    public Experiment() {
        headerIndex = new TreeMap<>();
        tissuesIndex = new TreeMap<>();
        data = new ArrayList<>();
        emptyColumns = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getIgnoredRows() {
        return ignoredRows;
    }

    public void setIgnoredRows(Integer ignoredRows) {
        this.ignoredRows = ignoredRows;
    }

    public int getNumberOfColumns() {
        return !data.isEmpty() ? data.get(0).size() : 0;
    }

    public int getNumberOfRows() {
        return data.size();
    }

    public Map<String, Integer> getHeaderIndex() {
        return headerIndex;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void insertDataRow(List<String> row) {
        this.data.add(row);
    }

    public void insertHeader(List<String> headerRow) {
        headerRow = headerRow.stream()
                .map(item -> WordUtils.capitalizeFully(item.replaceAll(", ", " - ")))
                .collect(Collectors.toList());

        this.data.add(0, headerRow);

        for (int i = 0; i < headerRow.size(); i++) {
            headerIndex.put(headerRow.get(i), i);
        }
    }

    public void setKeyColumn(int columnIndex) {
        keyColumn = columnIndex;
    }

    public void setKeyColumn(String columnName) {
        keyColumn = headerIndex.getOrDefault(columnName, 0);
    }

    public Integer getKeyColumn() {
        return keyColumn;
    }

    public Map<String, Integer> getTissuesIndex() {
        return tissuesIndex;
    }

    public void setTissuesIndex(final List<String> filterOutColumns) {
        tissuesIndex = headerIndex.entrySet().stream()
                .filter(entry -> !filterOutColumns.contains(entry.getKey().toLowerCase()))
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new));
    }

    public List<String> getEmptyColumns() {
        return emptyColumns;
    }

    public void setEmptyColumns() {
        emptyColumns = detectEmptyColumnNames();
        emptyColumns.forEach(name -> tissuesIndex.remove(name));
    }

    private List<String> detectEmptyColumnNames() {
        List<String> toBeRemoved = new ArrayList<>();

        for (String key : headerIndex.keySet()) {
            Integer columnIndex = headerIndex.get(key);
            boolean isColumnEmpty = true;
            for (int rowIndex = 1; rowIndex < data.size(); rowIndex++) {
                List<String> row = data.get(rowIndex);
                String value = row.get(columnIndex);
                if (value != null && !value.isEmpty()) {
                    isColumnEmpty = false;
                    break;
                }
            }

            if (isColumnEmpty) {
                toBeRemoved.add(key);
            }
        }

        return toBeRemoved;
    }

    public String extractSample(List<Integer> includedColumns, boolean removeRowsWithEmptyValues) {
        if (includedColumns == null || includedColumns.isEmpty()) {
            // In case no columns are specified assume all of them should be included
            includedColumns = new ArrayList<>(tissuesIndex.values());
        }
        // Prevent duplicates and invalid column indexes
        includedColumns = includedColumns.stream()
                .distinct()
                .filter(c -> tissuesIndex.containsValue(c))
                .collect(Collectors.toList());

        StringBuilder builder = new StringBuilder("#"); // header should start with #

        //Add header removing the name of the first column and adding the sample name
        builder.append(getName());
        List<String> header = data.get(0);
        builder.append(
                includedColumns.stream()
                        .map(header::get)
                        .collect(Collectors.joining("\t", "\t", System.lineSeparator()))
        );

        for (int r = 1; r < data.size(); r++) { //Rows
            List<String> row = data.get(r);
            boolean rowContainsNulls = includedColumns.stream()
                    .map(row::get)
                    .anyMatch(v -> v == null || v.isEmpty());
            if (rowContainsNulls && removeRowsWithEmptyValues) {
                // Nothing for now
            } else {
                builder.append(
                        includedColumns.stream()
                                .map(row::get)
                                .collect(Collectors.joining("\t", row.get(getKeyColumn()) + "\t", System.lineSeparator()))
                );
            }
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", resource='" + resource + '\'' +
                ", url='" + url.toString() + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", columns=" + getNumberOfColumns() +
                ", rows=" + getNumberOfRows() +
                ", keyColumn=" + getKeyColumn() +
                ", emptyColumns=" + emptyColumns +
                '}';
    }
}
