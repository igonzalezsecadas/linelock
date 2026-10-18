public class Entry {
    public String service;
    public String user;
    private String password;

    public Entry() {
    }

    public Entry(String service, String user, String password) {
        this.service = service;
        this.user = user;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Entry{service='" + service + "', user='" + user + "'}";
    }
}
