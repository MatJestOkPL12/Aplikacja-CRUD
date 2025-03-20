package seller;

import Events.AllEvents;
import Events.Event;
import client.Client;

import java.io.*;
import java.util.Optional;
import java.util.Scanner;

public class SellerLogic {
    SellerDao sellerDao = new SellerDao();
    Scanner scanner = new Scanner(System.in);
    Scanner scannerF = null;
    AllEvents allEvents = new AllEvents();
    File file = new File("Sellers.txt");
    int numberOfSellers = 1;

    boolean firsttime = true;


    private int previousNumberOfEvents1 = 0;
    private int previousNumberOfEvents2 = 0;
    private int previousNumberOfEventAttendees = 0;
    public void app(){
            try(FileInputStream fis = new FileInputStream(file)) {
                if(firsttime) {
                    saveEvents();
                    saveSellers();
                    fillEventsToSeller();
                    fillInterestedClients();
                    fillNeededPeople();
                    firsttime = false;
                }


                scannerF = new Scanner(fis);
                Scanner scanner = new Scanner(System.in);
                String line;
                while (true){
                    FileReader fr = new FileReader(file);
                    if(file.length() == 0){
                        System.out.println("Jesteś pierwszym sprzedawcą na naszj stronie! Witamy serdecznie\nPowiedz prosze jak się nazywasz");
                        String name = scanner.nextLine();
                        Seller seller = new Seller(1, name);
                        sellerDao.save(seller);
                        System.out.println("Witaj " + name + ". Powiedz teraz ile atrakcji masz do zaoferowania ");
                        int numberOfOffers = Integer.parseInt(scanner.nextLine());
                        System.out.println("Świetnie! \nPowiedz co to za oferty");
                        for(int i = 0; i<numberOfOffers; i++){
                            String offerName = scanner.nextLine();
                            seller.eventName.add(offerName);
                            int id = allEvents.events.size() + 1;
                            Event event = new Event(id, offerName);
                            allEvents.events.add(event);
                        }
                        System.out.println("Dziękujemy za dodanie swojej oferty!");
                        saveSeller(seller);
                        saveNameOfEvents();

                    }

                    else{

                        while (scannerF.hasNextLine()){
                            String line2 = scannerF.nextLine();
                            String [] buff;
                            buff = line2.split(";");
                            String name = buff[1];
                            int index = Integer.parseInt(buff[0]);
                            Seller seller = new Seller(index, name);
                            sellerDao.save(seller);
                            numberOfSellers++;
                        }


                        System.out.println("Witamy!");
                        System.out.println("Aby zalogować się do swojego konta kliknij 1");
                        System.out.println("Aby utworzyć nowe kliknij 2");
                        int choice = Integer.parseInt(scanner.nextLine());
                        switch (choice){
                            case 1:{
                                int logout = 0;
                                saveEvents();
                                saveSellers();
                                fillInterestedClients();

                                System.out.println("Jak masz na imie?");
                                String name = scanner.nextLine();
                                Optional<Seller>  sellerBuf = sellerDao.sellers.stream().filter(seller1 -> seller1.getName().equals(name)).findFirst();
                                Seller seller = sellerBuf.get();

                                System.out.println("Witaj " + name);
                                while (logout == 0){
                                    System.out.println("Aby przejrzeć twoje oferty kliknij 1");
                                    System.out.println("Aby zaaktualizować twoje oferty klkinij 2 - (dodanie ilość potrzebnych osób)");
                                    System.out.println("Aby przekazać oferte do realizacji kliknij 3");
                                    System.out.println("Aby zobaczyć liste potwierdzonych klientów kliknij 4");
                                    System.out.println("Aby przejrzeć oferty gotowe do zrealizowania kliknij 5");
                                    System.out.println("Aby sie wylogować kliknij 0");
                                    int choice1 = Integer.parseInt(scanner.nextLine());
                                    switch (choice1){
                                        case 0:{
                                            logout = 1;
                                            break;
                                        }
                                        case 1:{
                                            System.out.println("Oto twoje oferty wraz z zainteresowanymi ludzmi");
                                                fillInterestedClients();
                                            for(int i = 0; i<seller.eventName.size(); i++){
                                                String eventName = seller.eventName.get(i);
                                                Optional<Event> oevent = allEvents.events.stream().filter(event1 -> event1.getName().equals(eventName)).findFirst();
                                                Event event = oevent.get();
                                                String info = maxNumberOfPeopleInfo(event);

                                                System.out.print(seller.eventName.get(i) + " ~ " + info + " - ");
                                                for(int j = 0; j<event.interestedId.size(); j++){
                                                    System.out.print(event.interestedId.get(j) + ", ");
                                                }
                                                System.out.println();
                                            }
                                            System.out.println("Kliknij dowolny przycisk aby przejść dalej");
                                            scanner.nextLine();
                                            System.out.println();
                                            System.out.println();
                                            break;
                                        }
                                        case 2:{
                                            System.out.println("Które wydarzenie chcesz zaktualizować? - Podaj numer ");
                                            for(int i = 0; i<seller.eventName.size(); i++){
                                                System.out.println((i+1) + " - " + seller.eventName.get(i));
                                            }
                                            int index = Integer.parseInt(scanner.nextLine());
                                            String eventName = seller.eventName.get(index-1);
                                            Optional<Event> eventO = allEvents.events.stream().filter(event -> eventName.equals(event.getName())).findFirst();
                                            Event event = eventO.get();
                                            completeNeededPeople(event);

                                            try(FileWriter fw = new FileWriter("NeededPeople.txt", true)) {
                                                fw.write(event.getIdEvent() + ";" + event.getNeeded() + "\n");
                                            }catch (IOException e ){
                                                e.printStackTrace();
                                            }

                                            System.out.println("Kliknij dowolny przycisk aby przejść dalej");
                                            scanner.nextLine();
                                            System.out.println();
                                            System.out.println();

                                            break;

                                        }
                                        case 3:{
                                            System.out.println();
                                            System.out.println("Którą ofertę chcesz przekazać do realizacji?");
                                            System.out.println();
                                            for(int i = 0; i<seller.eventName.size(); i++){
                                                String nameEvent = seller.eventName.get(i);
                                                System.out.println((i+1) + "  " + nameEvent);
                                            }
                                            int index = Integer.parseInt(scanner.nextLine());
                                            String nameEvent = seller.eventName.get(index-1);
                                            Optional<Event> optionalEvent = allEvents.events.stream().filter(event -> event.getName().equals(nameEvent)).findFirst();
                                            Event event = optionalEvent.get();
                                            event.setReadyToRealize();
                                            saveReadyToRealize(event);
                                            System.out.println();
                                            System.out.println("Poniżej klienci którzy potwierdzili swoje przybycie na dane wydarzenie");



                                            System.out.println();
                                            System.out.println("Wydarzenie zostało przekazane do realizacji\nKliknij dowolny przycisk, aby kontynuować");
                                            scanner.nextLine();
                                            break;
                                        }
                                        case 4:{
                                            fillConfirmedClients();
                                            printComfirmedClients(seller);
                                            System.out.println();
                                            System.out.println("Kliknij dowolny przycisk aby kontunuować");
                                            scanner.nextLine();
                                            break;
                                        }
                                        case 5:{
                                            fillOrganizerAccpet();
                                            for(int i = 0; i<seller.eventName.size(); i++){
                                                String eventName = seller.eventName.get(i);
                                                Optional<Event> oevent = allEvents.events.stream().filter(event1 -> event1.getName().equals(eventName)).findFirst();
                                                Event event = oevent.get();

                                                if(event.getOrganizerAccepted()){
                                                    System.out.println(event.getIdEvent() + ".  " + event.getName());
                                                }
                                            }
                                            System.out.println("Jeśli chcesz zfinalizować wydarzenie kliknij 1\nJeśli chcesz wyjść kliknij 0");
                                            int choice2 = Integer.parseInt(scanner.nextLine());
                                            switch (choice2){
                                                case 0: break;
                                                case 1:{
                                                    System.out.println();
                                                    System.out.println("Które wydarzenie chcesz zrealicować? ");
                                                    int index = Integer.parseInt(scanner.nextLine());
                                                    Event event = allEvents.events.get(index-1);
                                                    event.tookPlace();

                                                    File file = new File("TookPlace.txt");
                                                    try(FileWriter fw = new FileWriter(file)){

                                                        fw.write(event.getIdEvent() + "\n");

                                                    }
                                                    catch (IOException e){
                                                        throw new RuntimeException(e.getMessage());
                                                    }

                                                }
                                            }
                                            break;
                                        }
                                    }
                                }

                                break;}
                            case 2:{
                                System.out.println("Witamy serdecznie!");
                                System.out.println("Powiedz prosze jak masz na imie");
                                String name = scanner.nextLine();
                                Seller seller = new Seller(numberOfSellers, name);
                                sellerDao.save(seller);
                                saveSeller(seller);

                                System.out.println("Witaj " + name + ". Powiedz teraz ile atrakcji masz do zaoferowania ");
                                int numberOfOffers = Integer.parseInt(scanner.nextLine());
                                System.out.println("Świetnie! \nPowiedz co to za oferty");
                                for(int i = 0; i<numberOfOffers; i++){
                                    String offerName = scanner.nextLine();
                                    seller.eventName.add(offerName);
                                    int id = allEvents.events.size() + 1;
                                    Event event = new Event(id, offerName);
                                    allEvents.events.add(event);
                                }

                                saveNameOfEvents();


                            }
                        }
                    }

                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }

    public void saveNameOfEvents(){
        File fileName = new File("Events.txt");
        int counter = 1;
        try(FileWriter fw = new FileWriter(fileName)){

            for(Seller seller : sellerDao.sellers){
                for(int i = 0; i<seller.eventName.size(); i++){
                    fw.write(counter + ";" + seller.getId() + ";" + seller.eventName.get(i)+ "\n");
                    counter++;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveSeller(Seller seller){
        try(FileWriter fw = new FileWriter(file, true)){

            fw.write(seller.getId() + ";" + seller.getName() + "\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillInterestedClients(){


        File file = new File("EventAttendees.txt");
        int lines = lineCounter(file);

        if(lines < previousNumberOfEventAttendees){
            for(Event event : allEvents.events){
                event.interestedId.clear();
            }
            try(FileReader fr = new FileReader(file)) {

                Scanner scanner = new Scanner(fr);
                while (scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    String [] buff;
                    buff = line.split(";");
                    int eventId = Integer.parseInt(buff[0]);
                    int index = Integer.parseInt(buff[1]);
                    Event event = allEvents.events.get(eventId - 1);
                    event.interestedId.add(index);

                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            previousNumberOfEventAttendees = lines;
        }

        if(lines > previousNumberOfEventAttendees) {

            try (FileReader fr = new FileReader(file)) {
                Scanner scanner = new Scanner(fr);
                String line;
                int i = 0;
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if(i>=previousNumberOfEventAttendees) {
                        String[] buff;
                        buff = line.split(";");
                        int eventId = Integer.parseInt(buff[0]);
                        int index = Integer.parseInt(buff[1]);
                        Event event = allEvents.events.get(eventId - 1);
                        event.interestedId.add(index);
                    }
                    i++;
                }


            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            previousNumberOfEventAttendees = lines;
        }
        else {
            return;
        }


    }

    private void fillEventsToSeller() {



        File file = new File("Events.txt");
        int lines = lineCounter(file);

        if (lines > previousNumberOfEvents1)
        {
            try (FileReader fr = new FileReader(file)) {

                Scanner scanner = new Scanner(fr);
                String line;

                int i = 0;
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if(i >= previousNumberOfEvents1) {
                        String buf[];
                        buf = line.split(";");
                        int eventIndex = Integer.parseInt(buf[0]);
                        int sellerIndex = Integer.parseInt(buf[1]);
                        String name = buf[2];
                        Seller seller = sellerDao.sellers.get(sellerIndex - 1);
                        seller.eventName.add(name);
                    }
                     i++;
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            previousNumberOfEvents1 = lines;
    }
        else{
            return;
        }
    }

    private void saveSellers(){
        File file = new File("Sellers.txt");

        try(FileReader fr = new FileReader(file)){

            Scanner scanner = new Scanner(fr);
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String [] buff;
                buff = line.split(";");
                int index = Integer.parseInt(buff[0]);
                String name = buff[1];
                Seller seller = new Seller(index,name);
                sellerDao.save(seller);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveEvents(){
        File file = new File("Events.txt");



        int lines = lineCounter(file);
        if(lines > previousNumberOfEvents2) {

            try (FileReader fr = new FileReader(file)) {

                Scanner scanner = new Scanner(fr);
                int i = 0;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if(i>=previousNumberOfEvents2) {
                        String[] buff;
                        buff = line.split(";");
                        int idEvent = Integer.parseInt(buff[0]);
                        String name = buff[2];
                        Event event = new Event(idEvent, name);
                        allEvents.events.add(event);
                    }
                    i++;
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            previousNumberOfEvents2 = lines;
        }
    }

    private int lineCounter(File file){
        int lines = 0;
        try {
            FileReader fr = new FileReader(file);
            Scanner scanner = new Scanner(fr);
            while(scanner.hasNextLine()){
                lines++;
                scanner.nextLine();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    private String maxNumberOfPeopleInfo (Event event){
        String info;
        if(event.getNeeded() == 0){
            info = "Nie podano jeszcze informacji o ilości potrzebnych osób";
        }
        else{
            info = String.format("%d", event.getNeeded());
        }
        return info;
    }

    private void completeNeededPeople(Event event){


        System.out.println("Ile osób jest potrzebnych do zorganizowania wydarzenia \" " + event.getName() + " \" ?");
        int people = Integer.parseInt(scanner.nextLine());
        event.setNeeded(people);
    }

    private void fillNeededPeople(){
        File file = new File("NeededPeople.txt");
        try {

            FileReader fr = new FileReader(file);
            Scanner scanner = new Scanner(fr);

            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String [] buff;
                buff = line.split(";");
                int index = Integer.parseInt(buff[0]);
                int needed = Integer.parseInt(buff[1]);
                Event event = allEvents.events.get(index-1);
                event.setNeeded(needed);
            }

        } catch (FileNotFoundException e) {

            throw new RuntimeException(e);

        }
    }

    private void saveReadyToRealize(Event event){
        File file = new File("ReadyToRealize.txt");
        try {

            FileWriter fw = new FileWriter(file, true);
            fw.write(event.getIdEvent() + "\n");
            fw.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillConfirmedClients(){
        File file = new File("ComfirmedClients.txt");

        for(Event event : allEvents.events){
            event.confirmedClients.clear();
        }

        try(FileReader fr = new FileReader(file)) {

            Scanner scanner = new Scanner(fr);
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String [] buff;
                buff = line.split(";");
                int eventIndex = Integer.parseInt(buff[0]);
                int clientIndex = Integer.parseInt(buff[1]);
                Event event = allEvents.events.get(eventIndex-1);
                event.confirmedClients.add(clientIndex);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printComfirmedClients(Seller seller){
        for(int i = 0; i<seller.eventName.size(); i++){
            String name = seller.eventName.get(i);
            Optional<Event> optionalEvent = allEvents.events.stream().filter(event -> name.equals(event.getName())).findFirst();
            Event event = optionalEvent.get();
            System.out.print(event.getName() + " - ");
            for(int j = 0; j<event.confirmedClients.size(); j++){
                System.out.print(event.confirmedClients.get(j) + ", ");
            }
            System.out.println();
        }

    }

    private void fillOrganizerAccpet(){
        File file = new File("OrganizerAcceptedEvents.txt");
        try(FileReader fr = new FileReader(file)){

            Scanner scanner = new Scanner(fr);
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                int index = Integer.parseInt(line);
                Event event = allEvents.events.get(index-1);
                event.organizerAccept();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
