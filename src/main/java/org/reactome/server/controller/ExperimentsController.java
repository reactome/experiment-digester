package org.reactome.server.controller;

import org.reactome.server.exception.NotFoundException;
import org.reactome.server.model.ExperimentSummary;
import org.reactome.server.model.Experiment;
import org.reactome.server.util.DataContainer;
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

    @GetMapping(value = "/summaries", produces = "application/json")
    @ResponseBody
    public Collection<ExperimentSummary> getSummaries() {
        infoLogger.info("Request for all experiment summaries");
        Collection<ExperimentSummary> summaries = dataContainer.getAllSummaries();
        if (summaries == null || summaries.isEmpty()) throw new NotFoundException("No experiment summaries found");
        return summaries;
    }

//    @GetMapping(value = "/submit/{id}", produces = "application/json")
//    @ResponseBody
//    public Experiment submit(@PathVariable Integer id,
//                             @RequestParam(name = "included", required = false) List<Integer> includedColumns,
//                             HttpServletRequest request) {
//        infoLogger.info("Request for experiment {} - Included columns: {}", id, includedColumns);
//
//        Experiment experiment = dataContainer.getExperimentById(id);
//        if (experiment == null) throw new NotFoundException("No experiment found");
//
//        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
//        URI newUri = builder.build().toUri();
//        infoLogger.info(newUri.toString()+"/experiments/sample/" + id );
//
//        return experiment;
//    }

    @GetMapping(value = "/{id}/sample", produces = "text/plain")
    @ResponseBody
    public String getSample(@PathVariable Integer id,
                            @RequestParam(name = "included", required = false) List<Integer> includedColumns) {
        infoLogger.info("Request for sample based on experiment {} - Included columns: {}", id, includedColumns);

        Experiment experiment = dataContainer.getExperimentById(id);
        if (experiment == null) throw new NotFoundException("No experiment found");
        return experiment.extractSample(includedColumns);
    }

}
