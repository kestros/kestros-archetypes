package com.test.buildintegrationtest.application.datasources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;

/**
 * Base test class for datasource models. Registers the services a container datasource resolves
 * while it builds its elements (component variations, UI-framework views, and the theme), so a
 * concrete datasource test only has to register its own model and build the resource to adapt.
 *
 * <p>Extend this, then in your own {@code @Before} call
 * {@code context.addModelsForClasses(YourDataSource.class)} and create the resource.</p>
 */
public abstract class BaseDataSourceTest {

  @Rule
  public final SlingContext context = new SlingContext();

  @Before
  public void registerDataSourceServices() throws Exception {
    context.registerService(ComponentVariationRetrievalService.class,
        mock(ComponentVariationRetrievalService.class));
    context.registerService(ComponentUiFrameworkViewRetrievalService.class,
        mock(ComponentUiFrameworkViewRetrievalService.class));
    context.registerService(ThemeRetrievalService.class, mock(ThemeRetrievalService.class));

    // Elements resolve the containing page's theme and UI Framework as they are constructed.
    Theme theme = mock(Theme.class);
    when(theme.getUiFramework()).thenReturn(mock(UiFramework.class));
    ThemeProviderService themeProviderService = mock(ThemeProviderService.class);
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);
    when(themeProviderService.getThemeForComponent(any())).thenReturn(theme);
    context.registerService(ThemeProviderService.class, themeProviderService);
  }
}
