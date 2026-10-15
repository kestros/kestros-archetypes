package com.test2.buildintegrationtest2.application.datasources;

import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.content.table.KestrosTableCellImpl;
import io.kestros.cms.components.basic.core.content.table.KestrosTableHeaderImpl;
import io.kestros.cms.components.basic.core.content.table.KestrosTableRowImpl;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Sample Table datasource.
 *
 * <p>Feeds programmatic (synthetic) data into the Kestros Table component
 * ({@code /libs/kestros/commons/components/content/table}). Instead of authoring header/row/cell
 * child resources, this datasource builds them in code: a header row plus two data rows.</p>
 *
 * <p>It is registered to the Table component by the application module, via the
 * {@code apps/kestros/commons/components/content/table/datasources/build-integration-test2-sample-table} node (see
 * the {@code classPath} property on that node). Authors select it on a Table component instance,
 * which stores the datasource name in the {@code kes:datasource} property. The component's common
 * view resolves the selected datasource through {@code TableDataSourceComponent}.</p>
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SampleTableDataSource extends BaseSampleContainerDataSource
    implements KestrosTable {

  /**
   * Table column headers.
   *
   * @return Header cells to render in the Table component's {@code thead}.
   */
  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    KestrosTableHeader name = buildOrNull(
        () -> new KestrosTableHeaderImpl(SampleContent.TABLE_HEADER_NAME, this, "header",
            "sample-header-name"), "sample table header name");
    if (name != null) {
      headers.add(name);
    }
    KestrosTableHeader role = buildOrNull(
        () -> new KestrosTableHeaderImpl(SampleContent.TABLE_HEADER_ROLE, this, "header",
            "sample-header-role"), "sample table header role");
    if (role != null) {
      headers.add(role);
    }
    return headers;
  }

  /**
   * Table data rows, each built from a list of cell values in {@link SampleContent}.
   *
   * @return Rows to render in the Table component's {@code tbody}.
   */
  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    int rowIndex = 0;
    for (final List<String> rowData : SampleContent.TABLE_ROWS) {
      final int currentRow = rowIndex;
      KestrosTableRow row = buildOrNull(() -> {
        List<KestrosTableCell> cells = new ArrayList<>();
        int cellIndex = 0;
        for (final String cellValue : rowData) {
          cells.add(new KestrosTableCellImpl(cellValue, this, "cell",
              "sample-cell-" + currentRow + "-" + cellIndex));
          cellIndex++;
        }
        return new KestrosTableRowImpl(cells, this, "row", "sample-row-" + currentRow);
      }, "sample table row " + currentRow);
      if (row != null) {
        rows.add(row);
        rowIndex++;
      }
    }
    return rows;
  }
}
