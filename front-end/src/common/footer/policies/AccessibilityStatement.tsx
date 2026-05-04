import "./Policies.css";

import { Link } from "react-router-dom";

const AccessibilityStatement = () => {
  return (
    <div className="policy-container">
      <h1>Accessibility statement</h1>
      <p>
        This accessibility statement applies to content published on the domain:&nbsp;
        <Link target="_blank" rel="noreferrer" to={"/"}>
          https://assurance-platform.goldstandard.org
        </Link>
        . It does not apply to any other content or websites on any other Gold Standard domain. This website is managed
        by CACI Ltd on behalf of Gold Standard. We want as many people as possible to be able to use this website. For
        example, that means you should be able to:
      </p>
      <p>
        <div className="list-container">
          <ul className="list">
            <li>change colours using browser or device settings</li>
            <li>zoom in up to 200% without the text spilling off the screen</li>
            <li>navigate most of the website using a keyboard or speech recognition software</li>
            <li>listen to most of the website using a modern screen reader</li>
          </ul>
        </div>
      </p>
      <p>We’ve also made the website text as simple as possible to understand. </p>
      <p>
        <a target="_blank" rel="noreferrer" href="https://mcmw.abilitynet.org.uk/">
          AbilityNet
        </a>{" "}
        has advice on making your device easier to use if you have a disability.
      </p>
      <p>
        This website is designed to comply with the{" "}
        <a
          target="_blank"
          rel="noreferrer"
          href="https://www.etsi.org/deliver/etsi_en/301500_301599/301549/03.02.01_60/en_301549v030201p.pdf"
        >
          technical standard for websites and mobile apps, EN 301 549 v.3.2.1.
        </a>{" "}
        This closely follows level ‘AA’ of the{" "}
        <a target="_blank" rel="noreferrer" href="https://www.w3.org/TR/WCAG21/">
          Web Content Accessibility Guidelines (WCAG) version 2.1.
        </a>
      </p>
      <div>
        <p>
          <strong>Compliance status</strong>
        </p>
      </div>
      <p>
        This website is partially compliant with{" "}
        <a target="_blank" rel="noreferrer" href="https://www.w3.org/TR/WCAG21/">
          technical standard EN 301 549 v.3.2.1
        </a>{" "}
        and the{" "}
        <a target="_blank" rel="noreferrer" href="https://www.w3.org/TR/WCAG21/">
          Web Content Accessibility Guidelines (WCAG) 2.1 Level AA.
        </a>{" "}
        See ‘Non-accessible content’ for more details.{" "}
      </p>
      <p>The website was last tested on 6 January 2025.</p>
      <div>
        <p>
          <strong>Preparation of this statement</strong>
        </p>
      </div>
      <p>This statement was updated on 22 October 2025.</p>
      <p>
        The statement is based on an internal review of all web pages in the platform by a qualified accessibility
        expert, using a combination of manual and automated testing.
      </p>
      <div>
        <p>
          <strong>Feedback</strong>
        </p>
      </div>
      <p>
        We welcome your feedback on the accessibility of the Gold Standard Assurance Platform website. Please let us
        know if you encounter accessibility barriers by email at{" "}
        <a href="mailto:smandon-jones@caci.co.uk">smandon-jones@caci.co.uk</a>.
      </p>
      <p>
        We try to respond to feedback within 15 business days from the date of receipt of the enquiry by the responsible
        department.
      </p>
      <div>
        <p>
          <strong>Compatibility with browsers and assistive technology</strong>
        </p>
      </div>
      <p>
        The Gold Standard Assurance Platform website is designed to be compatible with the following most used assistive
        technologies:
      </p>
      <p>
        <div className="list-container">
          <ul className="list">
            <li>the latest version of Google Chrome, Microsoft Edge, Mozilla Firefox and Apple Safari browsers;</li>
            <li>in combination with the latest versions of NVDA, VoiceOver and TalkBack.</li>
          </ul>
        </div>
      </p>
      <div>
        <p>
          <strong>Technical specifications</strong>
        </p>
      </div>
      <p>
        The accessibility of the Gold Standard Assurance Platform website relies on the following technologies to work
        with the particular combination of web browser and any assistive technologies or plugins installed on your
        computer:
      </p>
      <p>
        <div className="list-container">
          <ul className="list">
            <li>HTML</li>
            <li>WAI-ARIA</li>
            <li>CSS</li>
            <li>JavaScript</li>
          </ul>
        </div>
      </p>
      <div>
        <p>
          <strong>Non-accessible content</strong>
        </p>
      </div>
      <p>
        Despite our best efforts to ensure the accessibility of the Gold Standard Assurance Platform website, we are
        aware of some limitations, which we are working to fix. Below is a description of known limitations and
        potential solutions. Please contact us if you encounter an issue not listed below.
      </p>
      <p>Known limitations for the Gold Standard Assurance Platform website:</p>
      <p>
        <div className="list-container">
          <ul className="list">
            <li>
              <b>Mobile accessibility</b> - The website is not designed for mobile devices, text will run off the screen
              and may overlap with other text.
            </li>
            <li>
              <b>Colour contrast</b> - The standard website colour scheme does not meet the minimum contrast
              requirements for colour accessibility.
            </li>
            <li>
              <b>Screen readability</b> - Some content may not be presented appropriately on screen readers. We plan to
              fix this in 2026.
            </li>
            <li>
              <b>Keyboard navigation</b> - Tab indexes and keyboard navigation controls may not be sorted appropriately.
              This may mean some elements are not focusable or navigation becomes stuck. We plan to fix this in 2026.
            </li>
            <li>
              <b>Magnification scrolling issues</b> - On some pages, the header bar is in a fixed location and can be
              scrolled away from, particularly when the pages are magnified. We plan to fix this in 2026.
            </li>
          </ul>
        </div>
      </p>
    </div>
  );
};

export default AccessibilityStatement;
