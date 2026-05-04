package org.goldstandard.assurance.api.service.document.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.util.Offsets;
import org.goldstandard.assurance.api.util.SheetLocations;

public class SheetTextChunker {

  private final StringBuilder sheetAsString;
  private final SheetLocations sheetLocations;

  private static final String CELL_SEPARATOR = " |END| ";

  public SheetTextChunker() {
    sheetAsString = new StringBuilder();
    sheetLocations = new SheetLocations();
  }

  public void addCell(Cell cell) {
    int startOffset = sheetAsString.length();
    int endOffset = getNewSheetStringLengthWithCell(sheetAsString, cell);
    var offset = Offsets.builder().start(startOffset).end(endOffset).build();

    sheetLocations.put(offset, getCellReference(cell));
    sheetAsString.append(cell).append(CELL_SEPARATOR);
  }

  public boolean addingNewCellWillExceedChunkSize(Cell cell, int chunkSize) {
    return getNewSheetStringLengthWithCell(sheetAsString, cell) > chunkSize;
  }

  public ExtractedText completeChunk() {
    var currentChunk =
        ExtractedText.builder()
            .text(sheetAsString.toString())
            .sheetLocations(new SheetLocations(sheetLocations))
            .build();
    sheetAsString.setLength(0);
    sheetLocations.clear();
    return currentChunk;
  }

  public boolean hasChunk() {
    return !sheetAsString.isEmpty();
  }

  private static String getCellReference(Cell cell) {
    return new CellReference(cell).formatAsString();
  }

  private static int getNewSheetStringLengthWithCell(StringBuilder sheetAsString, Cell cell) {
    return sheetAsString.length() + cell.toString().length() + CELL_SEPARATOR.length();
  }
}
