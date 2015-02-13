package org.jenkinsci.plugins.jgiven.JGivenReportAction

import lib.JenkinsTagLib

t = namespace(JenkinsTagLib)

t.summary(icon: 'clipboard.png') {
    span(_('jgiven.reports.available'))

    ul {
        my.reports.each { report ->
            li {
                a(href: my.getReportUrl(report), report.name)
            }
        }
    }
}
