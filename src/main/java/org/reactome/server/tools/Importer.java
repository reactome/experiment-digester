package org.reactome.server.tools;

import com.martiansoftware.jsap.*;
import org.reactome.server.data.model.Experiment;
import org.reactome.server.data.model.ExperimentInfo;
import org.reactome.server.util.FileUtil;
import org.reactome.server.util.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class Importer {
    static final String OMIT = "omit";

    private static final Logger logger = LoggerFactory.getLogger(Importer.class.getName());

    private GXAParser parser;
    private final List<Experiment> allExperiments = new ArrayList<>();

    private Integer experimentId;

    public static String handleNullValues;

    public static void main(String[] args) throws Exception {

        SimpleJSAP jsap = new SimpleJSAP(
                Importer.class.getName(),
                "Imports a list of experiments from Expression Atlas",
                new Parameter[]{
                        new FlaggedOption("experiments", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'e', "experiments", "The list of experiments (urls) to import, comma separated optionally with names").setList(true).setListSeparator(',')
                        , new FlaggedOption("output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'o', "output", "The full path of the output binary file")
                        , new FlaggedOption("nulls", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'n', "nulls", "How empty (null) values are handled, e.g \"0.0\" will replace ane empty value with zeroes. \"" + OMIT + "\" will omit those lines with an empty value.")

                }
        );
        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        new Importer().start(config);
    }

    public void start(JSAPResult config) {
        List<ExperimentInfo> experiments = Arrays.stream(config.getStringArray("experiments"))
                .map(ExperimentInfo::createExperimentInfo)
                .collect(Collectors.toList());
        File file = new File(config.getString("output"));

        handleNullValues = config.getString("nulls");
        if (handleNullValues.equalsIgnoreCase(OMIT)) {
            logger.info("Lines with empty (null) values will be ommited");
        } else {
            logger.info("Empty (null) values will be replaced with \"" + handleNullValues + "\"");
        }

        if (FileUtil.validFile(file)) {
            logger.info(file + " is a valid file name");
            start(experiments, file);
        } else {
            System.exit(1);
        }
    }

    public void start(List<ExperimentInfo> experiments, File file) {
        this.processAll(experiments, file);
    }

    private void processAll(List<ExperimentInfo> experiments, File file) {
        logger.info(String.format("Importing %s experiment(s) from GXA", experiments.size()));
        parser = new GXAParser();

        experimentId = 0;
        for (ExperimentInfo exp : experiments) {
            try {
                processSingleExperiment(exp.getName(), new URL(exp.getUrl()));
            } catch (MalformedURLException e) {
                logger.error(String.format("Import of single experiment failed. Malformed URL: %s", exp));
                e.printStackTrace();
            }
        }

        storeExperiments(file);
    }

    private void processSingleExperiment(String name, URL target) {
        logger.info(String.format("Importing %s...", (name == null || name.isEmpty()) ? target.toString() : "[" + name + "] " + target.toString()));
        try (InputStream is = target.openConnection().getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            Experiment experiment = parser.createExperiment(++experimentId, reader, name, target);
            if (experiment.getIgnoredRows() > 0) {
                logger.info(">> " + experiment.getIgnoredRows() + " lines where omitted form the original source!");
            }
            if (experiment.getEmptyColumns().size() > 0) {
                logger.info(">> " + experiment.getEmptyColumns().size() + " empty columns where omitted form the original source: " + experiment.getEmptyColumns());
            }
            allExperiments.add(experiment);
        } catch (IOException e) {
            logger.error(String.format("Import of single experiment failed. Unable to resolve: %s", target));
            e.printStackTrace();
        }
    }

    private void storeExperiments(File file) {
        try {
            SerializationUtil.get().storeExperiment(allExperiments, file);
            logger.info(String.format(allExperiments.size() + " experiment(s) stored in %s", file.toString()));
        } catch (FileNotFoundException e) {
            logger.error("Import failed. Experiments are not saved properly to " + file.toString());
            e.printStackTrace();
        }
    }
}
