package org.jenkinsci.plugins.jgiven;

import hudson.model.AbstractProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

import static org.assertj.core.api.Assertions.assertThat;

public class CompatibilityTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @LocalData
    public void old_Data_should_load() {
        AbstractProject p = (AbstractProject) j.getInstance().getItem("old");
        JgivenReportGenerator trigger = (JgivenReportGenerator) p.getPublishersList().get(JgivenReportGenerator.class);
        assertThat(trigger.getReportConfigs()).hasSize(1);

    }
}
