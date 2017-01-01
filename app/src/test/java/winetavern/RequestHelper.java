package winetavern;

import lombok.NonNull;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import winetavern.model.user.Roles;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * @author Niklas Wünsche
 */
public interface RequestHelper {

    static MockHttpServletRequestBuilder buildPostAdminRequest(String url) {
        return post(url)
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));
    }

    static MockHttpServletRequestBuilder buildGetAdminRequest(String url) {
        return get(url)
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

    }

}
