package com.test2.buildintegrationtest2.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.table.KestrosTableCellImpl;
import io.kestros.cms.components.basic.core.content.table.KestrosTableHeaderImpl;
import io.kestros.cms.components.basic.core.content.table.KestrosTableRowImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SampleTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  private static final Logger LOG = LoggerFactory.getLogger(SampleTableDataSource.class);

  /**
   * Table column headers.
   *
   * @return Header cells to render in the Table component's {@code thead}.
   */
  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new KestrosTableHeaderImpl("Name", this, "header", "sample-header-name"));
      headers.add(new KestrosTableHeaderImpl("Role", this, "header", "sample-header-role"));
    } catch (final ComponentConfigurationException e) {
      LOG.error("Failed to build sample table headers. {}", e.getMessage());
    }
    return headers;
  }

  /**
   * Table data rows, each built from an in-code {@code String[]} of cell values.
   *
   * @return Rows to render in the Table component's {@code tbody}.
   */
  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    List<String[]> sampleData = Arrays.asList(
        new String[]{"Ada Lovelace", "Mathematician"},
        new String[]{"Alan Turing", "Computer Scientist"});
    int rowIndex = 0;
    for (final String[] rowData : sampleData) {
      try {
        List<KestrosTableCell> cells = new ArrayList<>();
        int cellIndex = 0;
        for (final String cellValue : rowData) {
          cells.add(new KestrosTableCellImpl(cellValue, this, "cell",
              "sample-cell-" + rowIndex + "-" + cellIndex));
          cellIndex++;
        }
        rows.add(new KestrosTableRowImpl(cells, this, "row", "sample-row-" + rowIndex));
        rowIndex++;
      } catch (final ComponentConfigurationException e) {
        LOG.error("Failed to build sample table row {}. {}", rowIndex, e.getMessage());
      }
    }
    return rows;
  }
}
