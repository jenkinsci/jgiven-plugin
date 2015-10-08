package org.jenkinsci.plugins.jgiven.JgivenReportGenerator.HtmlReportConfig

import lib.FormTagLib

f = namespace(FormTagLib)

f.entry(field: 'title', title: _('custom.title')) {
    f.textbox()
}
f.entry(field: 'customCssFile', title: _('custom.css.file')) {
    f.textbox()
}
f.entry(field: 'customJsFile', title: _('custom.js.file')) {
    f.textbox()
}