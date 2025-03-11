package com.harsh.internshala.automation.weatherwebapplication.Service;
import com.harsh.internshala.automation.weatherwebapplication.Model.User;
import com.harsh.internshala.automation.weatherwebapplication.Model.UserPrincipal;
import com.harsh.internshala.automation.weatherwebapplication.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service
public class MyUserDetailService implements UserDetailsService {
    private UserRepository userRepository;
    public MyUserDetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("User Not Found");
        }
        return new UserPrincipal(user);
    }
}
