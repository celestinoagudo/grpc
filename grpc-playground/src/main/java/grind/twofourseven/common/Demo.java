package grind.twofourseven.common;

import grind.twofourseven.interactive.FlowControlService;
import grind.twofourseven.interactive.GuessingGameService;
import grind.twofourseven.input.trailer.service.BankService;
import grind.twofourseven.patterns.service.TransferService;

public class Demo {
    public static void main(String[] args) {
        GrpcServer.create(new BankService(),
                new TransferService(),
                new FlowControlService(),
                new GuessingGameService(),
                new grind.twofourseven.input.validation.service.BankService()
        ).start().await();
    }
}
