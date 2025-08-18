package com.vhskillpro.backend.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.modules.user.UserService;
import com.vhskillpro.backend.modules.user.UserV1Controller;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("UserV1Controller schema alias classes coverage")
class UserV1ControllerSchemaTypesCoverageTests {

  @Test
  @DisplayName("Instantiate inner classes used only for OpenAPI schema so they are covered")
  void instantiateSchemaAliasClasses() throws Exception {
    UserService userService = Mockito.mock(UserService.class);
    UserV1Controller controller = new UserV1Controller(userService);

    Class<?> pagedAlias =
        Class.forName(
            "com.vhskillpro.backend.modules.user.UserV1Controller$PagedApiResponseUserDTO");
    Constructor<?> pagedCtor = pagedAlias.getDeclaredConstructor(UserV1Controller.class);
    pagedCtor.setAccessible(true);
    Object pagedInstance = pagedCtor.newInstance(controller);

    Class<?> dataAlias =
        Class.forName(
            "com.vhskillpro.backend.modules.user.UserV1Controller$DataApiResponseUserDTO");
    Constructor<?> dataCtor = dataAlias.getDeclaredConstructor(UserV1Controller.class);
    dataCtor.setAccessible(true);
    Object dataInstance = dataCtor.newInstance(controller);

    assertThat(pagedInstance).isInstanceOf(PagedApiResponse.class);
    assertThat(dataInstance).isInstanceOf(DataApiResponse.class);
  }
}
