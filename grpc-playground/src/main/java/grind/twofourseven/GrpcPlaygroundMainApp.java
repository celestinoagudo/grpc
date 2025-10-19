package grind.twofourseven;

import grind.twofourseven.model.Address;
import grind.twofourseven.model.School;
import grind.twofourseven.model.Student;
import grind.twofourseven.model.auto.BodyStyle;
import grind.twofourseven.model.auto.Car;
import grind.twofourseven.model.auto.Dealer;
import grind.twofourseven.model.library.Book;
import grind.twofourseven.model.library.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class GrpcPlaygroundMainApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcPlaygroundMainApp.class);

    public static void main(String[] args) {

        var address = Address.newBuilder().setStreet("123 Main Street").setCity("Atlanta")
                .setState("GA").build();

        var student = Student.newBuilder().setName("Sam").setAddress(address).build();

        var school = School.newBuilder()
                .setId(1)
                .setName("High School")
                .setAddress(address.toBuilder().setStreet("234 Main Street"))
                .build();

        var book1 = Book.newBuilder().setAuthor("Tobbey Maguire").setTitle("Spider Man 3").setPublicationYear(1995)
                .build();

        var book2 = Book.newBuilder().setAuthor("Tony Stark").setTitle("Iron Man 3").setPublicationYear(2002)
                .build();

        var library = Library.newBuilder().setName("Marvel Library").addAllBooks(List.of(book1, book2)).build();

        var car1 = Car.newBuilder().setMake("BMW").setModel("132").setYear(2000)
                .setBodyStyle(BodyStyle.COUPE)
                .build();

        var car2 = Car.newBuilder().setMake("Challenger").setModel("286").setYear(2000)
                .setBodyStyle(BodyStyle.SUV)
                .build();

        var car3 = Car.newBuilder().setMake("Jaguar").setModel("143").setYear(2000)
                .setBodyStyle(BodyStyle.SEDAN)
                .build();

        var dealer = Dealer.newBuilder().putAllInventory(Map.ofEntries(Map.entry(1, car1), Map.entry(2, car2), Map.entry(3, car3))).build();

        LOGGER.info("Address: {}", address);
        LOGGER.info("Student: {}", student);
        LOGGER.info("School: {}", school);
        LOGGER.info("Library: {}", library);
        LOGGER.info("Dealer: {}", dealer);
        LOGGER.info("Dealer Contains Inventory ? {}", dealer.containsInventory(1));
        LOGGER.info("Model: {}", dealer.getInventoryOrThrow(1).getModel());
        LOGGER.info("Body Style: {}", dealer.getInventoryOrThrow(3).getBodyStyle());
//        LOGGER.info("Model: {}", dealer.getInventoryOrThrow(4).getModel()); //throws illegal argument exception
    }
}