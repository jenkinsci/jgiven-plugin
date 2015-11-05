package org.jenkinsci.plugins.jgiven;

import groovy.lang.Closure;
import javaposse.jobdsl.dsl.Context;

import java.util.ArrayList;
import java.util.List;

public class JgivenDslContext implements Context {
    List<JgivenReportGenerator.ReportConfig> reportConfigs = new ArrayList<JgivenReportGenerator.ReportConfig>();
    String resultFiles = "";
    boolean excludeEmptyScenarios;

    public void html() {
        reportConfigs.add(new JgivenReportGenerator.HtmlReportConfig());
    }

    public void html(Closure<?> closure) {
        JgivenReportGenerator.HtmlReportConfig reportConfig = new JgivenReportGenerator.HtmlReportConfig(closure);
        reportConfigs.add(reportConfig);
    }

    public void results(String glob) {
        resultFiles = glob;
    }

    public void excludeEmptyScenarios(boolean excludeEmptyScenarios) {
        this.excludeEmptyScenarios = excludeEmptyScenarios;
    }

    public void excludeEmptyScenarios() {
        excludeEmptyScenarios(true);
    }
}
