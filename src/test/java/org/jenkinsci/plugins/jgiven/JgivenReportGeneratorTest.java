package org.jenkinsci.plugins.jgiven;

import com.google.common.collect.ImmutableList;
import com.tngtech.jgiven.report.ReportGenerator;
import com.tngtech.jgiven.report.ReportGenerator.Config;
import org.jenkinsci.plugins.jgiven.JgivenReportGenerator.ReportConfig;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class JgivenReportGeneratorTest {
    @Test
    public void when_no_report_is_configured_then_html_is_added_by_default() {
        JgivenReportGenerator jgivenReportGenerator = new JgivenReportGenerator(new ArrayList<ReportConfig>());

        assertThat(jgivenReportGenerator.getReportConfigs()).hasSize(1);
        assertThat(jgivenReportGenerator.getReportConfigs().iterator().next()).isInstanceOf(JgivenReportGenerator.HtmlReportConfig.class);
    }

    @Test
    public void excludeEmptyScenarios_is_set_into_the_Configuration() throws Exception {
        ReportConfig config = mock(ReportConfig.class);
        given(config.getReportDirectory()).willReturn("testDir");
        Config jgivenConfig = mock(Config.class);
        given(config.getJgivenConfig(null)).willReturn(jgivenConfig);
        ReportGenerator reportGenerator = mock(ReportGenerator.class);

        JgivenReportGenerator jgivenReportGenerator = new JgivenReportGenerator(ImmutableList.of(config));
        jgivenReportGenerator.setExcludeEmptyScenarios(true);
        jgivenReportGenerator.configureReportGenerator(new File("."), new File("."), config, reportGenerator, null);

        then(reportGenerator).should().setConfig(jgivenConfig);
        then(jgivenConfig).should().setExcludeEmptyScenarios(true);
    }
}