namespace java com.yangyang.thrift.api
struct UserRequest
{
    1:string id
}
struct UserResponse
{
    1:string code
    2:map<string,string> params
}
service UserService
{
    UserResponse userInfo(1:UserRequest request)
}