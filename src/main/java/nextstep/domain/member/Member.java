package nextstep.domain.member;

public class Member {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private MemberRole role;

    public Member() {
    }

    public Member(Long id, String username, String password, String name, String phone, MemberRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }

    public Member(Long id, String username, String password, String name, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = MemberRole.MEMBER;
    }

    public Member(String username, String password, String name, String phone) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = MemberRole.MEMBER;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public MemberRole getRole() {return role; }


    public boolean checkWrongPassword(String password) {
        return !this.password.equals(password);
    }
}
