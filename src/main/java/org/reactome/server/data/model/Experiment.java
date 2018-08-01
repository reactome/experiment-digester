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

    private Map<String, Integer> headerIndex = null;
    private Map<String, Integer> tissuesIndex = null;

    private List<List<String>> data = null;

    private Integer keyColumn = 0;



    public Experiment(Integer id, URL url) {
        this();
        this.url = url;
        this.id = id;
    }

    //This is required by Kryo
    public Experiment() {
        headerIndex = new TreeMap<>();
        tissuesIndex = new TreeMap<>();
        data = new ArrayList<>();
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

    public int getNumberOfColumns() {
        return !data.isEmpty() ? data.get(0).size() : 0;
    }

    public int getNumberOfRows() {
        return data.size();
    }

    public Map<String, Integer> getHeaderIndex() {
        return  headerIndex;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void insertData(List<String> row) {
        this.data.add(row);
    }

    public void insertHeader(List<String> headerRow) {
        this.data.add(0, headerRow);

        for (int i = 0; i <headerRow.size(); i++) {
            headerIndex.put(WordUtils.capitalizeFully(headerRow.get(i)), i);
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
                                             .filter(entry -> !filterOutColumns.contains(entry.getKey()))
                                             .sorted(Map.Entry.comparingByKey())
                                             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new));
    }

    public String extractSample(List<Integer> includedColumns){
        if(includedColumns == null || includedColumns.isEmpty()) {
            // In case no columns are specified assume all of them should be included
            includedColumns = new ArrayList<>(tissuesIndex.values());
        }
        // Prevent duplicates and invalid column indexes
        includedColumns = includedColumns.stream()
                                         .distinct()
                                         .filter(c -> tissuesIndex.containsValue(c))
                                         .collect(Collectors.toList());

        StringBuilder builder = new StringBuilder("#"); // header should start with #
        for (int r = 0; r < data.size() ; r++) { //Rows

            List<String> row = data.get(r);
            builder.append(
                    includedColumns.stream()
                            .map(c -> row.get(c))
                            .collect(Collectors.joining("\t", row.get(getKeyColumn()) + "\t", System.lineSeparator()))
            );
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
                '}';
    }
}
