package grind.twofourseven.common;

import grind.twofourseven.patterns.service.BankService;

public class Demo {
    public static void main(String[] args) {
        GrpcServer.create(new BankService()).start().await();
    }
}
