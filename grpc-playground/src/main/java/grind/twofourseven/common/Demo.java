package grind.twofourseven.common;

import grind.twofourseven.interactive.FlowControlService;
import grind.twofourseven.patterns.service.BankService;
import grind.twofourseven.patterns.service.TransferService;

public class Demo {
    public static void main(String[] args) {
        GrpcServer.create(new BankService(),
                new TransferService(),
                new FlowControlService()).start().await();
    }
}
