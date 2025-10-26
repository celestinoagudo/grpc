package grind.twofourseven;

import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import grind.twofourseven.model.Address;
import grind.twofourseven.model.School;
import grind.twofourseven.model.Student;
import grind.twofourseven.model.auto.BodyStyle;
import grind.twofourseven.model.auto.Car;
import grind.twofourseven.model.auto.Dealer;
import grind.twofourseven.model.common.Librarian;
import grind.twofourseven.model.inheritance.Credentials;
import grind.twofourseven.model.inheritance.Email;
import grind.twofourseven.model.inheritance.Phone;
import grind.twofourseven.model.known.WellKnown;
import grind.twofourseven.model.library.Book;
import grind.twofourseven.model.library.Library;
import grind.twofourseven.model.versioning.v1.Television;
import grind.twofourseven.model.versioning.v2.Type;
import grind.twofourseven.parser.V1Parser;
import grind.twofourseven.parser.V2Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class GrpcPlaygroundMainApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcPlaygroundMainApp.class);

    public static void main(String[] args) throws InvalidProtocolBufferException {

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

        var dealer = Dealer.newBuilder().putAllInventory(Map.ofEntries(Map.entry(1, car1), Map.entry(2, car2),
                Map.entry(3, car3))).build();

        LOGGER.info("Address: {}", address);
        LOGGER.info("Student: {}", student);
        LOGGER.info("School: {}", school);
        LOGGER.info("Library: {}", library);
        LOGGER.info("Dealer: {}", dealer);
        LOGGER.info("Dealer Contains Inventory ? {}", dealer.containsInventory(1));
        LOGGER.info("Model: {}", dealer.getInventoryOrThrow(1).getModel());
        LOGGER.info("Body Style: {}", dealer.getInventoryOrThrow(3).getBodyStyle());
//        LOGGER.info("Model: {}", dealer.getInventoryOrThrow(4).getModel()); //throws illegal argument exception

        //DEFAULT VALUES
        var schoolDefaultValue = School.newBuilder().build();
        LOGGER.info("School Id: {}", schoolDefaultValue.getId()); //primitive 0
        LOGGER.info("School Name: {}", schoolDefaultValue.getName()); //empty String NOT null
        LOGGER.info("School City: {}", schoolDefaultValue.getAddress().getCity()); //empty String NOT null
        LOGGER.info("Is Default Instance? {}", schoolDefaultValue.getAddress()
                .equals(Address.getDefaultInstance())); //true
        LOGGER.info("Has Address ? {}", schoolDefaultValue.hasAddress()); //false

        var libraryDefaultValues = Library.newBuilder().build();
        LOGGER.info("Books ? {}", libraryDefaultValues.getBooksList()); //empty String

        var dealerDefaultValues = Dealer.newBuilder().build();
        LOGGER.info("Inventory ? {}", dealerDefaultValues.getInventoryMap()); //empty String

        var carDefaultValues = Car.newBuilder().build();
        LOGGER.info("Body Type ? {}", carDefaultValues.getBodyStyle());

        //One Of
        var email = Email.newBuilder().setAddress("admin@gmail.com").setPassword("admin").build();
        var phone = Phone.newBuilder().setNumber(1234567891).setCode(63).build();
        var credentials = Credentials.newBuilder().setEmail(email)
                .setPhone(phone).build(); //will be set to last type set
        login(credentials);

        //Importing modules.
        var libraryWithLibrarian = Library.newBuilder()
                .setLibrarian(Librarian.newBuilder().setName("Ayra Erika").setAge(32).build());

        var wellKnown = WellKnown.newBuilder().setAge(Int32Value.of(32))
                .setLoginTime(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond())).build();

        LOGGER.info("Library with Librarian ? {}", libraryWithLibrarian);
        LOGGER.info("Well Known ? {}", wellKnown);
        LOGGER.info("Well Known Date Time ? {}", Instant.ofEpochSecond(wellKnown.getLoginTime().getSeconds()));
        LOGGER.info("Well Known Has Login Time ? {}", wellKnown.hasLoginTime());

        //version compatibility
        var television1 = Television.newBuilder().setBrand("Sony").setYear(2025).build();
        V1Parser.parse(television1.toByteArray());

        //V2 Version Compatibility
        var television2 = grind.twofourseven.model.versioning.v2.Television.newBuilder().setBrand("Samsung")
                .setModel(123).setType(Type.HD).build();
        V1Parser.parse(television2.toByteArray());
        V2Parser.parse(television2.toByteArray());
        V2Parser.parse(television1.toByteArray());
    }

    private static void login(Credentials credentials) {
        switch (credentials.getLoginTypeCase()) {
            case EMAIL -> LOGGER.info("Of Type Email: {}", credentials.getEmail().getAddress());
            case PHONE -> LOGGER.info("Of Type Phone: {}", credentials.getPhone().getNumber());
            default -> throw new IllegalArgumentException("Unsupported Login Type!");
        }

    }
}