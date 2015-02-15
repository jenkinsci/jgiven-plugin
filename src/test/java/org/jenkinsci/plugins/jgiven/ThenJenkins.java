package org.jenkinsci.plugins.jgiven;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import hudson.model.AbstractBuild;

import static org.assertj.core.api.Assertions.assertThat;

public class ThenJenkins<SELF extends ThenJenkins<SELF>> extends JenkinsStage<SELF> {
    @ExpectedScenarioState
    private AbstractBuild<?, ?> build;

    public SELF the_build_is_successful() throws Exception {
        jenkinsRule.assertBuildStatusSuccess(build);
        return self();
    }

    public SELF no_JGiven_report_is_generated() {
        assertThat(build.getActions(JgivenReportAction.class)).isEmpty();
        return self();
    }

    public SELF a_JGiven_report_is_generated() {
        assertThat(build.getActions(JgivenReportAction.class)).isNotEmpty();
        return self();
    }
}
