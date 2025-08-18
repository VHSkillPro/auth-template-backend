package com.vhskillpro.backend.permission;

import static org.assertj.core.api.Assertions.assertThat;

import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.modules.permission.PermissionService;
import com.vhskillpro.backend.modules.permission.PermissionV1Controller;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("PermissionV1Controller schema alias classes coverage")
class PermissionV1ControllerSchemaTypesCoverageTests {

  @Test
  @DisplayName("Instantiate inner classes used only for OpenAPI schema so they are covered")
  void instantiateSchemaAliasClasses() throws Exception {
    PermissionService permissionService = Mockito.mock(PermissionService.class);
    PermissionV1Controller controller = new PermissionV1Controller(permissionService);

    Class<?> pagedAlias =
        Class.forName(
            "com.vhskillpro.backend.modules.permission.PermissionV1Controller$PagedApiResponsePermissionDTO");
    Constructor<?> pagedCtor = pagedAlias.getDeclaredConstructor(PermissionV1Controller.class);
    pagedCtor.setAccessible(true);
    Object pagedInstance = pagedCtor.newInstance(controller);

    Class<?> dataAlias =
        Class.forName(
            "com.vhskillpro.backend.modules.permission.PermissionV1Controller$DataApiResponsePermissionDTO");
    Constructor<?> dataCtor = dataAlias.getDeclaredConstructor(PermissionV1Controller.class);
    dataCtor.setAccessible(true);
    Object dataInstance = dataCtor.newInstance(controller);

    assertThat(pagedInstance).isInstanceOf(PagedApiResponse.class);
    assertThat(dataInstance).isInstanceOf(DataApiResponse.class);
  }
}
