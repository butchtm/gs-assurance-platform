const fs = require("fs")
const path = require("path")
const { createHtmlReport } = require("axe-html-reporter")

const inputDir = "cypress/fixtures/a11y"
const outputDir = "cypress/reports/a11y-html"
const reportFileName1 = "unique-a11y-report.html"
const reportFileName2 = "grouped-a11y-report.html"

let allViolations = []
let groupedViolationsMap = new Map()

fs.readdirSync(inputDir).forEach((file) => {
  if (file.endsWith(".json")) {
    const filePath = path.join(inputDir, file)
    const data = JSON.parse(fs.readFileSync(filePath, "utf-8"))
    const pageName = data.page

    if (Array.isArray(data.violations)) {
      data.violations.forEach((v) => {
        // For unique report
        const taggedViolation = {
          ...v,
          description: `[${pageName}] ${v.description}`,
          nodes: v.nodes.map((n) => ({
            ...n,
            html: `<!-- ${pageName} -->\n${n.html}`
          }))
        }
        allViolations.push(taggedViolation)

        // For grouped report
        const key = `${v.id}-${v.description}`
        if (!groupedViolationsMap.has(key)) {
          groupedViolationsMap.set(key, {
            ...v,
            nodes: [] // remove page-specific info
          })
        }
      })
    }
  }
})

// First report: Unique violations with page URLs
createHtmlReport({
  results: { violations: allViolations },
  options: {
    projectKey: "Unique A11y Violations with Page URLs",
    outputDir,
    reportFileName: reportFileName1
  }
})

// Second report: Grouped violations (no page URLs)
createHtmlReport({
  results: { violations: Array.from(groupedViolationsMap.values()) },
  options: {
    projectKey: "Grouped A11y Violations Overview",
    outputDir,
    reportFileName: reportFileName2
  }
})

console.log(`✅ Unique report generated at: ${path.join(outputDir, reportFileName1)}`)
console.log(`✅ Grouped report generated at: ${path.join(outputDir, reportFileName2)}`)
