package com.test.buildintegrationtest.core.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.test.buildintegrationtest.api.services.SampleService;
import com.test.buildintegrationtest.core.services.SampleServiceImpl;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SampleCardListDataSourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private Resource resource;

  @Before
  public void setUp() throws Exception {
    context.registerService(ComponentVariationRetrievalService.class,
        mock(ComponentVariationRetrievalService.class));
    context.registerService(ComponentUiFrameworkViewRetrievalService.class,
        mock(ComponentUiFrameworkViewRetrievalService.class));
    context.registerService(ThemeRetrievalService.class, mock(ThemeRetrievalService.class));

    // Cards resolve the containing page's theme and UI Framework while they are constructed.
    Theme theme = mock(Theme.class);
    when(theme.getUiFramework()).thenReturn(mock(UiFramework.class));
    ThemeProviderService themeProviderService = mock(ThemeProviderService.class);
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);
    when(themeProviderService.getThemeForComponent(any())).thenReturn(theme);
    context.registerService(ThemeProviderService.class, themeProviderService);

    context.addModelsForClasses(SampleCardListDataSource.class);

    // Cards resolve their containing page, so the datasource resource must live under a page.
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content");
    resource = context.create().resource("/content/page/jcr:content/sample-cards");
  }

  @Test
  public void testGetCardElementsWhenSampleServiceIsMissing() {
    SampleCardListDataSource dataSource = resource.adaptTo(SampleCardListDataSource.class);
    assertNotNull(dataSource);
    assertEquals(0, dataSource.getCardElements().size());
  }

  @Test
  public void testGetCardElements() {
    context.registerService(SampleService.class, new SampleServiceImpl());
    SampleCardListDataSource dataSource = resource.adaptTo(SampleCardListDataSource.class);
    assertNotNull(dataSource);
    assertEquals(2, dataSource.getCardElements().size());
  }
}
