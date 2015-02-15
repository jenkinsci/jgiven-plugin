package org.jenkinsci.plugins.jgiven;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;

import java.io.IOException;

public class GivenJenkins<SELF extends GivenJenkins<SELF>> extends JenkinsStage<SELF> {
    public SELF a_freestyle_project() throws IOException {
        project = jenkinsRule.createFreeStyleProject();
        return self();
    }

    public SELF a_publish_jgiven_reports_recorder() throws IOException {
        project.getPublishersList().add(new JgivenReportGenerator(ImmutableList.<JgivenReportGenerator.ReportConfig>of(new JgivenReportGenerator.HtmlReportConfig())));
        return self();
    }

    public SELF without_jgiven_results() throws  Exception {
        return self();
    }

    public SELF with_jgiven_results() throws IOException {
        project.getBuildersList().add(new JgivenResultBuilder(Resources.toString(Resources.getResource(this.getClass(), "/org.jenkinsci.plugins.jgiven.JGivenReportArchiverTest.json"), Charsets.UTF_8)));
        return self();
    }

    private static class JgivenResultBuilder extends Builder {
        private String contents;

        JgivenResultBuilder(String contents) {
            this.contents = contents;
        }

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
            build.getWorkspace().child("report.json").write(contents, Charsets.UTF_8.name());
            return true;
        }
    }
}
