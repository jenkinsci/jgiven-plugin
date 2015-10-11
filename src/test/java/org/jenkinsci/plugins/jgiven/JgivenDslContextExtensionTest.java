package org.jenkinsci.plugins.jgiven;

import com.google.common.collect.Iterables;
import com.tngtech.jgiven.report.ReportGenerator;
import hudson.model.FreeStyleProject;
import javaposse.jobdsl.plugin.ExecuteDslScripts;
import javaposse.jobdsl.plugin.RemovedJobAction;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.assertj.core.api.Assertions.assertThat;

public class JgivenDslContextExtensionTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void html_Report_can_be_configured() throws Exception {
        FreeStyleProject seedJob = j.createFreeStyleProject();

        seedJob.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation(Boolean.TRUE.toString(), null,
                "freeStyleJob('test-job') { publishers { jgivenReports {  results 'some.json'; html { customCss '/some/css' } }  } }"), false, RemovedJobAction.DELETE));

        j.buildAndAssertSuccess(seedJob);

        FreeStyleProject createdJob = (FreeStyleProject) j.getInstance().getItem("test-job");
        JgivenReportGenerator jgivenReportGenerator = createdJob.getPublishersList().get(JgivenReportGenerator.class);

        assertThat(jgivenReportGenerator.getJgivenResults()).isEqualTo("some.json");

        JgivenReportGenerator.HtmlReportConfig reportConfig = (JgivenReportGenerator.HtmlReportConfig) Iterables.getOnlyElement(jgivenReportGenerator.getReportConfigs());
        assertThat(reportConfig.getFormat()).isEqualTo(ReportGenerator.Format.HTML);
        assertThat(reportConfig.getCustomCssFile()).isEqualTo("/some/css");
    }
}