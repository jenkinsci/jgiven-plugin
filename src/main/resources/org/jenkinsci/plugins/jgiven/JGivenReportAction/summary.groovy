package org.jenkinsci.plugins.jgiven.JGivenReportAction

import lib.JenkinsTagLib

t = namespace(JenkinsTagLib)

t.summary(icon: 'clipboard.png') {
    span(_('jgiven.reports'))

    ul(style: 'list-style-type: none; margin: 0;') {
        my.reports.each { report ->
            li {
                a(href: my.getReportUrl(report), report.name)
            }
        }
    }
}
