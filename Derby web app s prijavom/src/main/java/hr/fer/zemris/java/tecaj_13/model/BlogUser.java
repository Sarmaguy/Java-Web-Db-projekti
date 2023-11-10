package hr.fer.zemris.java.tecaj_13.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "BlogUser.all", query = "select user from BlogUser as user"),
        @NamedQuery(name = "BlogUser.getUser", query = "select user from BlogUser as user where user.nick=:nick")
})
@Entity
@Table(name="blog_users")
public class BlogUser {

    private Long id;
    private String firstName;
    private String lastName;
    private String nick;
    private String email;
    private String passwordHash;
    private List<BlogEntry> entries = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }


    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    public List<BlogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<BlogEntry> entries) {
        this.entries = entries;
    }

    @Column(nullable = false, length = 25)
    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    @Column(nullable = false, length = 25)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @Column(nullable = false, length = 25, unique = true)
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
    @Column(nullable = false, length = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Column(nullable = false, length = 50)
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogUser blogUser = (BlogUser) o;
        return id.equals(blogUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
