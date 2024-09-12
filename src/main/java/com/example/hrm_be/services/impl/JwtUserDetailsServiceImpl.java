package com.example.hrm_be.services.impl;


import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.services.UserService;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JwtUserDetailsServiceImpl implements UserDetailsService {

  @Autowired private UserService userService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    com.example.hrm_be.models.dtos.User loggInUser = userService.getByEmail(email);
    if (loggInUser != null && !loggInUser.getRoles().isEmpty()) {
      return new User(
          loggInUser.getEmail(),
          loggInUser.getPassword(),
          loggInUser.getRoles().stream()
              .map(r -> new SimpleGrantedAuthority(r.getType().getValue()))
              .collect(Collectors.toList()));
    } else {
      throw new UsernameNotFoundException(HrmConstant.ERROR.AUTH.NOT_FOUND);
    }
  }
}
