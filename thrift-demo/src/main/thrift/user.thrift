namespace java com.yangyang.thrift.api

struct User {
    1: required string name
    2: required i32 age
}

service UserService{
    User findUser(),
    void saveUser(1:User user)
}

