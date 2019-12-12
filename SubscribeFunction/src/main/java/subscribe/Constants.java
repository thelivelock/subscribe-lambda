package subscribe;

public enum Constants {
    USERS_TABLE("scoring-service-users");

    private String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
};
