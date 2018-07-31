package org.reactome.server.data;

import org.reactome.server.exception.NotFoundException;
import org.reactome.server.data.model.ExperimentSummary;
import org.reactome.server.data.model.Experiment;
import org.reactome.server.util.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@Component
@Scope("singleton")
public class DataContainer {
    private static final Logger logger = LoggerFactory.getLogger(DataContainer.class.getName());

    private Map<Integer, Experiment> experiments;

    public DataContainer() {
    }

    public Map<Integer,Experiment> getExperiments() {
        if (experiments == null) {
            String msg = "Experiments have not been initialized.";
            logger.error(msg, msg);
            System.err.println(getClass().getName() + " [ERROR] : " + msg);
            throw new NotFoundException(msg);
        }
        return experiments;
    }

    public Experiment getExperimentById(Integer id) {
        return getExperiments().get(id);
    }

    public List<ExperimentSummary> getAllSummaries() {
        return getExperiments().values().stream()
                                        .map(ExperimentSummary::new)
                                        .collect(Collectors.toList());
    }

    /**
     * Initialise the Experiments data by loading the content of the binary file
     *
     * @param fileName the binary file containing the data structures for the analysis
     */
    public void setFileName(String fileName) {
        if (experiments != null) {
            logger.warn("Attempt to load the content file when previously loaded");
            return;
        }
        try {
            experiments = SerializationUtil.get().loadExperiment(new File(fileName)).stream()
                    .collect(Collectors.toMap(Experiment::getId, Function.identity(), (a, b) -> a));

            logger.info(String.format("%s experiments loaded successfully", experiments.size()));
        } catch (FileNotFoundException e) {
            String msg = String.format("%s has not been found. Please check the settings or run the Importer tool again.", fileName);
            logger.error(msg, e);
            System.exit(1);
        }
    }
}
