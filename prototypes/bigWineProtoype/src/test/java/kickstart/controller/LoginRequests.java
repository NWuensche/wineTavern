package kickstart.controller;

import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

/**
 * Created by nwuensche on 03.11.16.
 */
public class LoginRequests {

    static public RequestBuilder getLoginRequest(String username, String password) {
        RequestBuilder requestBuilder = formLogin().user(username).password(password);
        return requestBuilder;
    }
}
