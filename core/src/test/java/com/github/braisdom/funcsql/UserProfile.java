package com.github.braisdom.funcsql;

import com.github.braisdom.funcsql.annotations.BelongsTo;
import com.github.braisdom.funcsql.annotations.RelatedTo;
import com.github.braisdom.funcsql.annotations.Table;

@Table("user_profiles")
@RelatedTo(base = User.class, name = "userProfiles", foreignKey = "user_id")
@BelongsTo(base = User.class, name = "user", primaryKey = "id", foreignKey = "user_id")
public class UserProfile {

    private int id;
    private String name;
    private int userId;

    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public UserProfile setUser(User user) {
        this.user = user;
        return this;
    }
}
