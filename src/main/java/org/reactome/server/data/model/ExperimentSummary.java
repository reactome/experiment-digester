package org.reactome.server.data.model;

import java.net.URL;
import java.util.Map;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ExperimentSummary {
    private final Integer id;
    private final String name;
    private final String description;
    private final String resource;
    private final URL url;
    private final String timestamp;
    private final Integer numberOfGenes;

    private final Map<String, Integer> tissuesMap;

    public ExperimentSummary(Experiment experiment) {
        id = experiment.getId();
        name = experiment.getName();
        description = experiment.getDescription();
        resource = experiment.getResource();
        url = experiment.getUrl();
        timestamp = experiment.getTimestamp();
        numberOfGenes = experiment.getNumberOfRows() - 1; // Account for the header row
        tissuesMap = experiment.getTissuesIndex();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getResource() {
        return resource;
    }

    public URL getUrl() {
        return url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Integer getNumberOfGenes() {
        return numberOfGenes;
    }

    public Map<String, Integer> getTissuesMap() {
        return tissuesMap;
    }

    @Override
    public String toString() {
        return "ExperimentSummary{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", resource='" + resource + '\'' +
                ", url=" + url +
                ", timestamp='" + timestamp + '\'' +
                ", numberOfGenes=" + numberOfGenes +
                ", tissuesMap=" + tissuesMap +
                '}';
    }
}
