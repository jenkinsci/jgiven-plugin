package org.jenkinsci.plugins.jgiven;

import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class JgivenReportGeneratorTest {
    @Test
    public void when_no_report_is_configured_then_html_is_added_by_default() {
        JgivenReportGenerator jgivenReportGenerator = new JgivenReportGenerator(new ArrayList<JgivenReportGenerator.ReportConfig>());

        assertThat(jgivenReportGenerator.getReportConfigs()).hasSize(1);
        assertThat(jgivenReportGenerator.getReportConfigs().iterator().next()).isInstanceOf(JgivenReportGenerator.HtmlReportConfig.class);
    }
}