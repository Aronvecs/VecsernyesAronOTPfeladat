import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import Classes.Costumer;
import Classes.Payment;
import Classes.TopCostumer;
import Validators.CostumerValidatorFactory;
import Validators.PaymentValidatorBase;
import Validators.PaymentValidatorFactory;


public class Main {
    static List<Costumer> costumers = new ArrayList<>();
    static List<Payment> payments = new ArrayList<>();
    static List<TopCostumer> topCostumers = new ArrayList<>();
    static int paymentslines = 0;
    static int costuemrlines = 0;

    public static void main(String[] args) throws IOException {
        Path path = Path.of("src", "CSVFiles", "costumer.csv");
        ReadingCostumerCSVFile(path);
        path = Path.of("src", "CSVFiles", "payments.csv");
        ReadingPaymentCSVFile(path);

        for (Costumer c : costumers){
            System.out.println(c.toString());
        }
        System.out.println(costuemrlines);
        for (Payment c : payments){
            System.out.println(c.toString());
        }
        System.out.println(paymentslines);

        CostumersPurchases();
        path = Path.of("src", "CSVFiles", "report01.csv");
        Readingreport01CSVFile(path);

        topCostumers.sort(Comparator.comparing(TopCostumer::getSum).reversed());
        var firstNElementsList = topCostumers.stream().limit(2).collect(Collectors.toList());

        for (TopCostumer c : firstNElementsList){
            System.out.println(c.toString());
        }
        TheTop2Costumer(firstNElementsList);
        WebshopsIncome();
    }
    private static void TheTop2Costumer(List<TopCostumer> costumers) throws IOException {
        File file = new File("src\\CSVFiles","top.csv");
        file.createNewFile();
        PrintWriter outputfile = new PrintWriter(file);
        outputfile.println("Name,Address,PurchaseSum");
        for (TopCostumer c : costumers){
            outputfile.print(c.getName()+","+c.getAddress()+","+c.getSum()+"\n");
        }
        outputfile.close();

    }
    private static void WebshopsIncome() throws IOException {
        File file = new File("src\\CSVFiles","report02.csv");
        file.createNewFile();
        PrintWriter outputfile = new PrintWriter(file);
        outputfile.println("Webshop,TransferPurchases,BankCardPurchases");
        payments.stream()
                .collect(Collectors.groupingBy(Payment::getWebshopID,
                        Collectors.groupingBy(Payment::getPayingMethod, Collectors.summingInt(Payment::getSum))))
                .forEach((id, paymentMethod)->{
                    System.out.println(id+paymentMethod);
                    outputfile.print(id+",");
                    paymentMethod.forEach((s,d)->{
                        if (d==null){
                            outputfile.print(0+",");
                        }
                        else{
                            outputfile.print(d+",");
                        }
                    });
                    outputfile.println();
                });
        outputfile.close();
    }
    private static void CostumersPurchases() throws IOException {
        File file = new File("src\\CSVFiles","report01.csv");
        file.createNewFile();
        PrintWriter outputfile = new PrintWriter(file);
        outputfile.println("Name,Address,PurchaseSum");
        payments.stream()
                .collect(Collectors.groupingBy(Payment::getCostumerId,
                        Collectors.summingInt(Payment::getSum)))
                .forEach((id,sumTargetCost)->{
                    outputfile.print(Objects.requireNonNull(FindCostumerByID(id)).getName()
                            +","+ Objects.requireNonNull(FindCostumerByID(id)).getAddress() +
                            ","+ sumTargetCost+"\n");
                });
        outputfile.close();
    }
    private static Costumer FindCostumerByID(String id){
        for (Costumer c : costumers){
            if (Objects.equals(c.getId(), id)){
                return c;
            }
        }
        return null;
    }
    private static void ReadingPaymentCSVFile(Path path) throws IOException {
        Files.lines(path)
                .skip(1)
                .map(Main::getPayment)
                .forEach((n)->{
                    if (n != null){
                        payments.add(n);
                    }
                });
    }
    private static void ReadingCostumerCSVFile(Path path) throws IOException {
        Files.lines(path)
                .skip(1)
                .map(Main::getCostumer)
                .forEach((n)->{
                    if (n != null){
                        costumers.add(n);
                    }
                });
    }
    private static void Readingreport01CSVFile(Path path) throws IOException {
        Files.lines(path)
                .skip(1)
                .map(line->{
                    String[] split = line.split(",");
                    return new TopCostumer(split[0], split[1], Integer.valueOf(split[2]));
                })
                .forEach((n)->{
                    if (n != null){
                        topCostumers.add(n);
                    }
                });
    }
    private static Payment getPayment(String line) {
        paymentslines++;
        String[] fields = line.split(",");
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(fields[6]);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Payment c = new Payment(fields[0], fields[1], fields[2], Integer.valueOf(fields[3]), fields[4], fields[5], date);
        var validatorFactory = new PaymentValidatorFactory();
        var validators = validatorFactory.GetValidators();

        return ValidationCheck(validators, c);
    }

    private static Payment ValidationCheck(Iterable<PaymentValidatorBase> validators, Payment c) {
        int acceptedValidatiors = 0;
        File file = new File("src\\CSVFiles", "append.log");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (var validator : validators) {
            if (validator.Validate(c)){
                acceptedValidatiors++;
            }
            else{
                try {
                    fr.write("In the line: "+paymentslines +" "+validator.NotValid(c)+"\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (acceptedValidatiors == size(validators)) {
            return c;
        }

        return null;
    }

    private static Costumer getCostumer(String line) {
        costuemrlines++;
        String[] fields = line.split(",");
        Costumer c = new Costumer(fields[1], fields[2], fields[3], fields[0]);
        var validatorFactory = new CostumerValidatorFactory();
        var validators = validatorFactory.GetValidators();

        int acceptedValidatiors = 0;
        File file = new File("src\\CSVFiles","append.log");//filename
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (var validator : validators) {
            if (validator.Validate(c)){
                acceptedValidatiors++;
            }
            else{
                try {
                    fr.write("In the line: "+costuemrlines +" "+validator.NotValid(c)+"\n");//data
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (acceptedValidatiors == size(validators)) {
            return c;
        }

        return null;
    }


    public static int size(Iterable data) {

        if (data instanceof List) {
            return ((List<?>) data).size();
        }
        int counter = 0;
        for (Object i : data) {
            counter++;
        }
        return counter;
    }


}