package com.test.buildintegrationtest.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.content.table.KestrosTableCellImpl;
import io.kestros.cms.components.basic.testing.BaseDataSourceTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the sample Table datasource returns its sample headers, rows, cells and ids.
 */
public class SampleTableDataSourceTest extends BaseDataSourceTest {

  private SampleTableDataSource dataSource;

  @Before
  public void setUp() {
    context.addModelsForClasses(SampleTableDataSource.class);

    // Elements resolve their containing page, so the datasource resource must live under a page.
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content");
    dataSource = context.create().resource("/content/page/jcr:content/sample-table")
        .adaptTo(SampleTableDataSource.class);
    assertNotNull(dataSource);
  }

  @Test
  public void testGetHeaderElements() {
    List<KestrosTableHeader> headers = dataSource.getHeaderElements();
    assertEquals(2, headers.size());
    assertEquals("Name", headers.get(0).getText());
    assertEquals("sample-header-name", headers.get(0).getResource().getName());
    assertEquals("Role", headers.get(1).getText());
    assertEquals("sample-header-role", headers.get(1).getResource().getName());
  }

  @Test
  public void testGetRowElements() {
    List<KestrosTableRow> rows = dataSource.getRowElements();
    assertEquals(2, rows.size());
    assertRow(rows.get(0), 0, "Ada Lovelace", "Mathematician");
    assertRow(rows.get(1), 1, "Alan Turing", "Computer Scientist");
  }

  private void assertRow(final KestrosTableRow row, final int rowIndex, final String... values) {
    assertEquals("sample-row-" + rowIndex, row.getResource().getName());
    List<KestrosTableCell> cells = row.getCellElements();
    assertEquals(values.length, cells.size());
    for (int cellIndex = 0; cellIndex < values.length; cellIndex++) {
      KestrosTableCell cell = cells.get(cellIndex);
      assertEquals("sample-cell-" + rowIndex + "-" + cellIndex, cell.getResource().getName());
      assertEquals(values[cellIndex], ((KestrosTableCellImpl) cell).getText());
    }
  }
}
