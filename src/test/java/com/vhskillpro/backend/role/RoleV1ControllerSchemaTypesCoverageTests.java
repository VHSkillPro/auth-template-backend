package com.vhskillpro.backend.role;

import static org.assertj.core.api.Assertions.assertThat;

import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.modules.role.RoleService;
import com.vhskillpro.backend.modules.role.RoleV1Controller;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("RoleV1Controller schema alias classes coverage")
class RoleV1ControllerSchemaTypesCoverageTests {

  @Test
  @DisplayName("Instantiate inner classes used only for OpenAPI schema so they are covered")
  void instantiateSchemaAliasClasses() throws Exception {
    RoleService roleService = Mockito.mock(RoleService.class);
    RoleV1Controller controller = new RoleV1Controller(roleService);

    Class<?> pagedAlias =
        Class.forName(
            "com.vhskillpro.backend.modules.role.RoleV1Controller$PagedApiResponseRoleDTO");
    Constructor<?> pagedCtor = pagedAlias.getDeclaredConstructor(RoleV1Controller.class);
    pagedCtor.setAccessible(true);
    Object pagedInstance = pagedCtor.newInstance(controller);

    Class<?> dataAlias =
        Class.forName(
            "com.vhskillpro.backend.modules.role.RoleV1Controller$DataApiResponseRoleDetailDTO");
    Constructor<?> dataCtor = dataAlias.getDeclaredConstructor(RoleV1Controller.class);
    dataCtor.setAccessible(true);
    Object dataInstance = dataCtor.newInstance(controller);

    assertThat(pagedInstance).isInstanceOf(PagedApiResponse.class);
    assertThat(dataInstance).isInstanceOf(DataApiResponse.class);
  }
}
