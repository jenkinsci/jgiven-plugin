package org.jenkinsci.plugins.jgiven.JgivenReportGenerator

import lib.FormTagLib

f = namespace(FormTagLib)

f.entry(title: _('jgiven.json.files'), field: 'jgivenResults') {
  f.textbox()
}

f.entry(title: _('exclude.empty.scenarios'), field: 'excludeEmptyScenarios') {
    f.checkbox()
}

f.section(title: _('reports.to.generate')) {
    f.block {
        f.repeatableHeteroProperty(field: 'reportConfigs', hasHeader: true, oneEach: true)
    }
}
