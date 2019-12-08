package store_email.dto;

import java.util.Locale;

public class UserInfoDto {
    private String email;
    private Locale locale;

    public UserInfoDto() { }

    public UserInfoDto(String email, Locale locale) {
        this.email = email;
        this.locale = locale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
