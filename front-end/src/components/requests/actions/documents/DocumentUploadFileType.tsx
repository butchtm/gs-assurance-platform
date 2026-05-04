import "./DocumentUploadFileType.css";

const DocumentUploadFileType = () => {
  return (
    <div className="types-container">
      <h1>Allowed File Types</h1>
      <ul className="list">
        <li>Archives - .zip, .rar, .7z</li>
        <li>Images- .gif, .png, .bmp, . jpg, . jpeg, .jpe, .tiff, .tif, .dng, .jfif</li>
        <li>Documents - .doc, .docx, .pdf, .txt, .htm, .html</li>
        <li>Spreadsheets - .xls, .xlsx, .xlsm, .xlsb, .csv</li>
        <li>Presentations - .ppt, .pptx</li>
        <li>GIS / shapefile - .shp, .shx, .dbf</li>
        <li>Email - .msg, .eml</li>
        <li>Media / video - .mov, . mp4, .mpeg, spv</li>
        <li>Config & misc - .ini, .xml, .tmp, .sav</li>
      </ul>
    </div>
  );
};
export default DocumentUploadFileType;
