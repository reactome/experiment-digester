package org.reactome.server.data.model;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ExperimentInfo {
    private final String name;
    private final String url;

    private ExperimentInfo(String url) {
        this(null, url);
    }

    private ExperimentInfo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public static ExperimentInfo createExperimentInfo(String entry) {
        ExperimentInfo rtn = null;
        String[] info = entry.split("]");
        if(info.length == 1) {
            rtn = new ExperimentInfo(info[0]);
        } else if(info.length == 2) {
            rtn = new ExperimentInfo(info[0].replaceAll("\\[", ""), info[1]);
        }
        return rtn;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "ExperimentInfo{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
