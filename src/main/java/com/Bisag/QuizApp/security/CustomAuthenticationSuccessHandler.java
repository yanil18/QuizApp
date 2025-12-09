package com.Bisag.QuizApp.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.Bisag.QuizApp.dto.Csuser;
import com.Bisag.QuizApp.entity.Portallog;
import com.Bisag.QuizApp.repository.CsuserRepo;
import com.Bisag.QuizApp.repository.PortallogRepo;
import com.Bisag.QuizApp.utils.CusAccessObjectUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {



    @Autowired
    private CsuserRepo userDAO;

    @Autowired
    private PortallogRepo portallogrepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
                Optional<Csuser> csuser = userDAO.findFirstByEmail(authentication.getName());

                System.out.println("Successful login for user: " + csuser.get().getEmail());

                String clientip = CusAccessObjectUtil.getIpAddress(request);

                System.out.println("Client IP Address: " + clientip);
                RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

                new Thread() {
                    @Override
                    public void run() {
                        Portallog portal = new Portallog();
            portal.setEmail(csuser.get().getEmail());
            portal.setMobileno(csuser.get().getMobileNo());
            portal.setIpaddress(clientip);
            portal.setUserid(csuser.get().getId());
            portallogrepo.save(portal);
                }

            }.start();
            try {
                redirectStrategy.sendRedirect(request, response, "/dash");
            } catch (IOException e) {
                    request.getSession().setAttribute("error", "!!! Contact to Administrator !!!");
               redirectStrategy.sendRedirect(request, response, "/");
            }

             if (response != null) {
            Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
            boolean firstHeader = true;
            for (String header : headers) {
                if (firstHeader) {
                    response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=strict"));
                    firstHeader = false;
                    continue;
                }
                response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=strict"));
            }
        }
    }
    
}
