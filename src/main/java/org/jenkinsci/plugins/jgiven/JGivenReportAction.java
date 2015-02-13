package org.jenkinsci.plugins.jgiven;

import com.google.common.base.Preconditions;
import hudson.FilePath;
import hudson.Functions;
import hudson.model.DirectoryBrowserSupport;
import hudson.model.Run;
import jenkins.model.RunAction2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.jenkinsci.plugins.jgiven.JGivenReportArchiver.REPORTS_DIR;

public class JGivenReportAction implements RunAction2 {

    private transient Run<?, ?> run;
    private List<Report> reports;

    public JGivenReportAction(Run<?, ?> run, List<JGivenReportArchiver.ReportConfig> reportConfigs) {
        Preconditions.checkNotNull(reportConfigs);
        this.run = run;
        reports = new ArrayList<Report>();
        for (JGivenReportArchiver.ReportConfig reportConfig : reportConfigs) {
            reports.add(new Report(reportConfig));
        }
    }

    public static class Report {
        private final String url;
        private final String name;

        public Report(JGivenReportArchiver.ReportConfig reportConfig) {
            this.url = reportConfig.getReportUrl();
            this.name = reportConfig.getReportName();
        }


        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return "jgiven";
    }

    public String getReportUrl(Report report) {
        return String.format("%s/report/%s", Functions.getActionUrl(run.getUrl(), this), report.getUrl());
    }

    public DirectoryBrowserSupport doReport() {
        return new DirectoryBrowserSupport(run, new FilePath(new File(run.getRootDir(), REPORTS_DIR)), Messages.JGivenReportAction_jgiven_reports(), "clipboard.png", true);
    }

    @Override
    public void onAttached(Run<?, ?> r) {
        this.run = r;
    }

    @Override
    public void onLoad(Run<?, ?> r) {
        this.run = r;
    }
}
