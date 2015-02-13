package org.jenkinsci.plugins.jgiven;

import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class JGivenReportArchiverTest extends ScenarioTest<GivenJenkins<?>, WhenJenkins<?>, ThenJenkins<?>> {

    @ScenarioState
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();


    @Test
    public void No_jGiven_results() throws Exception {
        given()
                .a_freestyle_project()
                .with().a_publish_jgiven_reports_recorder()
                .and().without_jgiven_results();

        when().the_project_is_built();

        then().the_build_is_successful()
                .and().no_jGiven_report_is_generated();
    }

    @Test
    public void generate_jGiven_reports() throws Exception {
        given()
                .a_freestyle_project()
                .with().a_publish_jgiven_reports_recorder()
                .and().with_jgiven_results();

        when().the_project_is_built();

        then().the_build_is_successful()
                .and().a_jGiven_report_is_generated();
    }

}