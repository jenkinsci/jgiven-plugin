package org.jenkinsci.plugins.jgiven;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.JenkinsRule;

public class JenkinsStage<SELF extends JenkinsStage<SELF>> extends Stage<SELF> {
    @ScenarioState
    protected JenkinsRule jenkinsRule;

    @ScenarioState
    protected FreeStyleProject project;
}
