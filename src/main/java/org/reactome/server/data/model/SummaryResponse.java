package org.reactome.server.data.model;

import java.util.Collection;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SummaryResponse {
    private final Collection<ExperimentSummary> summaries;

    public SummaryResponse(Collection<ExperimentSummary> summaries) {
        this.summaries = summaries;
    }

    public Collection<ExperimentSummary> getSummaries() {
        return summaries;
    }
}
