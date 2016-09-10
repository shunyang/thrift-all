namespace java com.yangyang.thrift.service


enum DemoEnum{
    A,
    B,
    C
}

struct DemoParam{
    1:required i32 id,
    2:optional string name,
    3:optional DemoEnum demoEnum,
}

struct DemoResult{
    1:required string code,
    2:required string result,
}

service HealthService{
    string ping()
}

service HelloService{

    string hello(1:string msg),

    DemoResult test(1:DemoParam param),
}