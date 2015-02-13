package org.jenkinsci.plugins.jgiven;

import com.tngtech.jgiven.report.ReportGenerator;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractDescribableImpl;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JGivenReportArchiver extends Recorder implements SimpleBuildStep {

    public static final String REPORTS_DIR = "jgiven-reports";
    private List<ReportConfig> reportConfigs;

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @DataBoundConstructor
    public JGivenReportArchiver(List<ReportConfig> reportConfigs) {
        this.reportConfigs = reportConfigs != null ? new ArrayList<ReportConfig>(reportConfigs) : Collections.<ReportConfig>emptyList();
    }

    private String jgivenResults;

    public List<ReportConfig> getReportConfigs() {
        return Collections.unmodifiableList(reportConfigs);
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        File reportRootDir = reportRootDir(run);
        File jgivenJsons = new File(reportRootDir, "json");
        int numFiles = workspace.copyRecursiveTo(jgivenResults, new FilePath(jgivenJsons));
        if (numFiles > 0) {
            for (ReportConfig reportConfig : reportConfigs) {
                generateReport(reportRootDir, jgivenJsons, reportConfig);
            }
            run.addAction(new JGivenReportAction(run, reportConfigs));
        }
    }

    private void generateReport(File reportRootDir, File jGivenJsons, ReportConfig reportConfig) throws IOException, InterruptedException {
        try {
            reportConfig.reportGenerator(jGivenJsons, reportRootDir).generate();
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File reportRootDir(Run<?, ?> run) {
        return new File(run.getRootDir(), REPORTS_DIR);
    }

    public String getJgivenResults() {
        return jgivenResults;
    }

    @DataBoundSetter
    public void setJgivenResults(String jgivenResults) {
        this.jgivenResults = jgivenResultsFromString(jgivenResults);
    }

    private static String jgivenResultsFromString(String jgivenResults) {
        return StringUtils.isBlank(jgivenResults) ? "**/*.json" : jgivenResults;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.JGivenReportArchiver_display_name();
        }

        public FormValidation doCheckJgivenResults(
                @AncestorInPath AbstractProject project,
                @QueryParameter String value) throws IOException {
            if (project == null) {
                return FormValidation.ok();
            }
            return FilePath.validateFileMask(project.getSomeWorkspace(), jgivenResultsFromString(value));
        }
    }

    public static abstract class ReportConfig extends AbstractDescribableImpl<ReportConfig> {
        private ReportGenerator.Format format;

        public ReportGenerator.Format getFormat() {
            return format;
        }

        ReportConfig(ReportGenerator.Format format) {
            this.format = format;
        }

        public String getReportDirectory() {
            return getFormat().name().toLowerCase();
        }

        public String getReportUrl() {
            return getReportDirectory();
        }

        abstract String getReportName();

        public ReportGenerator reportGenerator(File sourceDir, File reportRootDir) {
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.setSourceDirectory(sourceDir);
            reportGenerator.setFormat(getFormat());
            reportGenerator.setTargetDirectory(new File(reportRootDir, getReportDirectory()));
            return reportGenerator;
        }
    }

    public static abstract class BaseHtmlReportConfig extends ReportConfig {
        BaseHtmlReportConfig(ReportGenerator.Format format) {
            super(format);
        }

        public String getReportUrl() {
            return String.format("%s/index.html", getReportDirectory());
        }

        private String customCssFile;

        public String getCustomCssFile() {
            return customCssFile;
        }

        @DataBoundSetter
        public void setCustomCssFile(String customCssFile) {
            this.customCssFile = customCssFile;
        }

        @Override
        public ReportGenerator reportGenerator(File sourceDir, File reportRootDir) {
            ReportGenerator reportGenerator = super.reportGenerator(sourceDir, reportRootDir);
            if (StringUtils.isNotBlank(customCssFile)) {
                reportGenerator.setCustomCssFile(new File(customCssFile));
            }
            return reportGenerator;
        }

        public abstract static class BaseHtmlDescriptor extends Descriptor<ReportConfig> {
            public FormValidation doCheckCustomCssFile(@QueryParameter String value) {
                if (StringUtils.isEmpty(value)) {
                    return FormValidation.ok();
                }
                File file = new File(value);
                return file.exists() ? FormValidation.ok() : FormValidation.error(Messages.JGivenReportArchiver_custom_css_file_does_not_exist());
            }
        }
    }

    public static class HtmlReportConfig extends BaseHtmlReportConfig {
        @DataBoundConstructor
        public HtmlReportConfig() {
            super(ReportGenerator.Format.HTML);
        }

        public String getReportName() {
            return Messages.JGivenReport_html_name();
        }

        @Extension
        public static class DescriptorImpl extends BaseHtmlDescriptor {
            @Override
            public String getDisplayName() {
                return Messages.JGivenReport_html_name();
            }
        }
    }

    public static class Html5ReportConfig extends BaseHtmlReportConfig {
        @DataBoundConstructor
        public Html5ReportConfig() {
            super(ReportGenerator.Format.HTML5);
        }

        public String getReportName() {
            return Messages.JGivenReport_html5_name();
        }

        @Extension
        public static class DescriptorImpl extends BaseHtmlDescriptor {
            @Override
            public String getDisplayName() {
                return Messages.JGivenReport_html5_name();
            }
        }
    }

    public static class TextReportConfig extends ReportConfig {
        @DataBoundConstructor
        public TextReportConfig() {
            super(ReportGenerator.Format.TEXT);
        }

        @Override
        public String getReportName() {
            return Messages.JGivenReport_text_name();
        }

        @Extension
        public static class DescriptorImpl extends Descriptor<ReportConfig> {
            @Override
            public String getDisplayName() {
                return Messages.JGivenReport_text_name();
            }
        }
    }

    public static class AsciiDocReportConfig extends ReportConfig {
        @DataBoundConstructor
        public AsciiDocReportConfig() {
            super(ReportGenerator.Format.ASCIIDOC);
        }

        public String getReportName() {
            return Messages.JGivenReport_asciidoc_name();
        }

        @Extension
        public static class DescriptorImpl extends Descriptor<ReportConfig> {
            @Override
            public String getDisplayName() {
                return Messages.JGivenReport_asciidoc_name();
            }
        }
    }
}
