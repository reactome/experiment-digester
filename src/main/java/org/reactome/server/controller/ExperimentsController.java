package org.reactome.server.controller;

import org.reactome.server.data.DataContainer;
import org.reactome.server.data.model.Experiment;
import org.reactome.server.data.model.ExperimentSummary;
import org.reactome.server.data.model.SummaryResponse;
import org.reactome.server.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */

@RestController
@RequestMapping("/experiments")
public class ExperimentsController {

    private static final Logger infoLogger = LoggerFactory.getLogger("infoLogger");

    @Autowired
    private DataContainer dataContainer;

    @GetMapping(value = "/testme")
    @ResponseStatus(value = HttpStatus.OK)
    public String testMe() {
        return "Experiments loaded " + dataContainer.getExperiments().size();
    }

    @GetMapping(value = "/summaries", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SummaryResponse getSummaries() {
        infoLogger.info("Request for all experiment summaries");
        Collection<ExperimentSummary> summaries = dataContainer.getAllSummaries();
        if (summaries == null || summaries.isEmpty()) throw new NotFoundException("No experiment summaries found");
        return new SummaryResponse(summaries);
    }

    @GetMapping(value = "/{id}/sample", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getSample(@PathVariable Integer id,
                            @RequestParam(name = "omitNulls", required = false, defaultValue = "false") boolean omitNulls,
                            @RequestParam(name = "included", required = false) List<Integer> includedColumns) {
        infoLogger.info("Request for sample based on experiment {} - Included columns: {} {}", id, includedColumns, omitNulls ? "- Nulls omitted" : "");

        Experiment experiment = dataContainer.getExperimentById(id);
        if (experiment == null) throw new NotFoundException("No experiment found");
        return experiment.extractSample(includedColumns, omitNulls);
    }
}
