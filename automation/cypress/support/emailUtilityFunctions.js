const fs = require("fs")

class _EmailHelper {
  fillEmailTemplate = (template, data) => template.replace(/{{(.*?)}}/g, (_, key) => data[key.trim()] || "")

  formatTextChangelog = (changelogArray) =>
    changelogArray.map((entry) => `\t${entry.account}\t${entry.changedAt}\t${entry.fromStatus}\t${entry.toStatus}`).join("\n")

  getFormattedDate = () => {
    const now = new Date()
    const day = String(now.getDate()).padStart(2, "0")
    const month = String(now.getMonth() + 1).padStart(2, "0") // Months are 0-indexed
    const year = now.getFullYear()
    return `${day}/${month}/${year}`
  }

  getChangelogRowsForStatus = (targetStatus) => {
    const rows = this.changelogMap[targetStatus]
    return this.formatTextChangelog(rows)
  }

  recipientRules = {
    "Completeness Check": {
      "template 1": "PD",
      "template 4": "GS ARM Team",
      "template 1:vvb": "VVB"
    },
    "In Preparation": {
      "template 2": "PD"
    },
    "In Audit": {
      "template 8": "VVB",
      "template 2": "PD",
      "template 2:vvb": "VVB"
    },
    "In Assignment": {
      "template 3": "PD",
      "template 3:vvb": "VVB",
      "template 3:arm": "GS ARM Team"
    },
    "Generated": {
      "template 6": "PD",
      "template 6:vvb": "VVB"
    },
    "Reviewer Assigned": {
      "template 9": "Reviewer"
    }
    // Add more statuses/templates as needed
  }

  determineRecipient = (targetStatus, template) => this.recipientRules[targetStatus][template]
  determineFeedbackRound = (targetStatus) => {
    const basicList = ["In Assignments", "Reviewer Assigned"]

    if (basicList.includes(targetStatus)) {
      return 1
    } else {
      return 0
    }
  }

  determineLink = (emails) => {
    const firstTextBody = emails[0].textBody
    const linkMatch = firstTextBody.match(/https:\/\/[^\s"')]+/)
    return linkMatch ? linkMatch[0] : ""
  }

   determineLinkPOA = (emails) => {
    const firstTextBody = emails[0].textBody
    const linkMatch = firstTextBody.match(/POA\/VPA\s*\(https:\/\/[^\s"')]+\)/)
    return linkMatch ? linkMatch[0] : ""
  }

   determineLinkProjectActivity = (emails) => {
    const firstTextBody = emails[0].textBody
    const linkMatch = firstTextBody.match(/Project Activity\s*\(https:\/\/[^\s"')]+\)/)
    return linkMatch ? linkMatch[0] : ""
  }
  
  generateReviewDate = () => {
    const reviewDate = new Date()
    reviewDate.setDate(reviewDate.getDate() + 28)

    const day = String(reviewDate.getDate()).padStart(2, '0');
    const month = String(reviewDate.getMonth() + 1).padStart(2, '0');
    const year = reviewDate.getFullYear();

    const formattedDate = `${day}/${month}/${year}`

    return formattedDate
  }

  getValueFromPlatformInfo = (key) => {
    const filePath = "cypress/fixtures/platformInfo.json"
    const rawData = fs.readFileSync(filePath, "utf8")
    const jsonData = JSON.parse(rawData)
    return jsonData[key]
  }
}

const emailHelper = new _EmailHelper()
export default emailHelper
