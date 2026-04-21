package restaurant.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import restaurant.entity.Role;
import restaurant.entity.User;
import restaurant.entity.UserRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.getUserRoles() != null) {
            for (UserRole userRole : user.getUserRoles()) {
                String roleName = userRole.getRole().getName();
                authorities.add(new SimpleGrantedAuthority(roleName));
            }
        }
        return authorities;
    }

    public Integer getRestaurantId() {
        if (user.getUserRoles() != null) {
            for (UserRole ur : user.getUserRoles()) {
                if (ur.getRestaurant() != null) {
                    return ur.getRestaurant().getRestaurantId();
                }
            }
        }
        return null;
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public String getPhone() {
        return user.getPhone();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public String getAvatar() {return user.getAvatar();}

    public int getPoints() {
        return user.getPoints();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}