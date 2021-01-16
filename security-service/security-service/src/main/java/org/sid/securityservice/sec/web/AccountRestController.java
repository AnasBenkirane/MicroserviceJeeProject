package org.sid.securityservice.sec.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.sid.securityservice.sec.JWTUtil;
import org.sid.securityservice.sec.entities.AppRole;
import org.sid.securityservice.sec.entities.AppUser;
import org.sid.securityservice.sec.service.AccountService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AccountRestController {
    private AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/users")
    @PostAuthorize("hasAuthority('USER')")
    public List<AppUser>appUsers(){
    return accountService.listUsers();
    }

    @GetMapping(path = "/roles")
    @PostAuthorize("hasAuthority('USER')")
    public List<AppRole>appRoles(){
        return accountService.listRoles();
    }

    @PostMapping(path = "/users")
    @PostAuthorize("hasAuthority('ADMIN')")
    AppUser saveUser(@RequestBody AppUser appUser){
        return accountService.addNewUser(appUser);
    }

    @PostMapping(path = "/roles")
    @PostAuthorize("hasAuthority('ADMIN')")
    AppRole saveRole(@RequestBody AppRole appRole){
        return accountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    void addRoleToUser(@RequestBody RoleUserForm roleUserForm){
         accountService.addRoleToUser(roleUserForm.getUsername(),roleUserForm.getRoleName());
    }

    @GetMapping(path = "refreshToken")
    public void refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception{
    String authToken= httpServletRequest.getHeader(JWTUtil.AUTH_HEADER);
    if (authToken!=null && authToken.startsWith(JWTUtil.PREFIX)){
        try {
            String jwt = authToken.substring(JWTUtil.PREFIX.length());
            Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT=jwtVerifier.verify(jwt);
            String username = decodedJWT.getSubject();
            AppUser appUser = accountService.loadUserByUsername(username);

            String JwtAccesToken = JWT.create()
                    .withSubject(appUser.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis()+JWTUtil.EXPIRE_ACESS_TOKEN))
                    .withIssuer(httpServletRequest.getRequestURL().toString())
                    .withClaim("roles",appUser.getAppRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toList()))
                    .sign(algorithm);

            Map<String,String> idToken=new HashMap<>();
            idToken.put("acces-token",JwtAccesToken);
            idToken.put("refresh-token",jwt);
            httpServletResponse.setContentType("application/json");
            new ObjectMapper().writeValue(httpServletResponse.getOutputStream(),idToken);

        }catch (Exception e){
            throw e;
        }
    }
    else {
        throw new RuntimeException("Refresh Token Required !!");
    }
    }

    @GetMapping(path = "/profile")
    public AppUser profile(Principal principal){
    return accountService.loadUserByUsername(principal.getName());
    }

}


@Data
class RoleUserForm{
    private String username;
    private String roleName;
}