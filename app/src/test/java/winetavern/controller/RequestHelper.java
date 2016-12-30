package winetavern.controller;

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

    static RequestBuilder buildPostAdminRequest(String url) {
        return post(url)
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));
    }

    static RequestBuilder buildPostAdminRequest(String url, @NonNull Map<String, String> params) {
        MockHttpServletRequestBuilder request = post(url)
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        if(params == null) {
            return request;
        }

        for(String paramName : params.keySet()) {
            request = request.param(paramName, params.get(paramName));
        }

        return request;
    }

    static RequestBuilder buildGetAdminRequest(String url) {
        return get(url)
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

    }

}
