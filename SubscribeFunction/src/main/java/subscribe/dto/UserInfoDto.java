package subscribe.dto;

import java.util.Locale;

public class UserInfoDto {
    private String email;
    private Locale locale;
    private String timeZone;

    public UserInfoDto() { }

    public UserInfoDto(String email, Locale locale, String timeZone) {
        this.email = email;
        this.locale = locale;
        this.timeZone = timeZone;
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

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
