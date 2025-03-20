package client;

import Events.AllEvents;
import Events.Event;
import organizer.OrganizerLogic;
import seller.Seller;
import seller.SellerDao;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ClientLogic {

    SellerDao sellerDao = new SellerDao();
    AllEvents allEvents = new AllEvents();
    OrganizerLogic organizerLogic = new OrganizerLogic();
    ClientDao clientDao = new ClientDao();
    private int numberOfClients = 0;
    File file = new File("Klients.txt");
    Scanner scannerf = null;
    Scanner scanerI = new Scanner(System.in);
    List<Event> clientEvents = new ArrayList<>();

    public void app(){
        try (FileInputStream fis = new FileInputStream(file)){
            scannerf = new Scanner(fis);
            fillEventList();
            String line;
        while (true) {
            FileReader fr = new FileReader(file);

            //Logika gdy w naszym pliku nie ma zapisanego żadnego klienta
            //-------------------------------------------------------------------------------------------------------------
            if (file.length() == 0) {
                System.out.println("Jesteś pierwszym klientem w naszym sklepie. Miło cię widzieć\nPodaj swoje imie");
                String name = scanerI.nextLine();
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(1 + ";" + name + "\n");
                    System.out.println("Witamy " + name);

                }
            }
            //-------------------------------------------------------------------------------------------------------------
            //Liczenie liczby klientów
            //-------------------------------------------------------------------------------------------------

            while (scannerf.hasNextLine()) {
                line = scannerf.nextLine();
                String buf[];
                buf = line.split(";");
                int index = Integer.parseInt(buf[0]);
                String name = buf[1];
                Client client = new Client(index, name);
                clientDao.save(client);
                numberOfClients++;
            }

            //--------------------------------------------------------------------------------------------------
            System.out.println("Jeśli masz już konto i chcesz się zalogować kliknij 1");
            System.out.println("Jesli chcesz się zarejestrować kliknij 2");
            int choice = Integer.parseInt(scanerI.nextLine());
                switch (choice) {

                    case 1: {
                        System.out.println("Podaj swoje imie");
                        String name = scanerI.nextLine();
                        Client client = clientDao.get(name).orElseThrow(() -> new RuntimeException("Klient o imieniu " + name + "Nie istnieje"));
                        clearPage();
                        System.out.println("Witaj " + name);
                        int logout = 0;

                        while (logout == 0){
                            System.out.println("Co chcesz zrobić");
                            System.out.println("Usuń swoje konto kliknij 1");
                            System.out.println("Przejrzyj dostępne oferty kliknij 2");
                            System.out.println("Przejrzyj swoje oferty kliknij 3");
                            System.out.println("Przejrzyj zrealizowane wydarzenia kliknij 4");
                            System.out.println("Wyloguj się kliknij 0");
                            int choice2 = Integer.parseInt(scanerI.nextLine());
                            switch (choice2) {
                                case 0:{
                                    logout = 1;
                                    break;
                                }
                                case 1:
                                    deleteClient(client);
                                    break;
                                case 2: {
                                    fillEventList();
                                    organizerLogic.printEvents(allEvents);
                                    System.out.println("Jeśli jesteś zainteresowany rezerwacją którejśc z atrakcji kliknij numerek znajdujący się przy niej." +
                                            "Jeśli nie kliknij 0");

                                    int eventChoice = Integer.parseInt(scanerI.nextLine());

                                    if (eventChoice == 0) {
                                        break;
                                    } else {

                                        clientToEvent(eventChoice, client);
                                        System.out.println("Dziękujemy za wybór naszej usługi kliknij przycisk aby przejść dalej");
                                        scanerI.nextLine();


                                    }

                                    break;
                                }
                                case 3: {
                                    clientEvents.clear();
                                    fillEventList();
                                    clearPage();
                                    fillEventParams();
                                    printYourEvents(client);

                                    System.out.println("Możesz oczywiście zrezygnowac z swojego zamówienia, lub potwoerdzić swoją obecność na swoim wydarzeniu. " +
                                            "Aby ty zrobić wybierz interesujące cię wydarzenie, lub kliknij 0 aby wyjść");

                                    int eventIndex = Integer.parseInt(scanerI.nextLine());
                                    if(eventIndex == 0){
                                        break;
                                    }
                                    Event event = clientEvents.get(eventIndex - 1);
                                    System.out.println();
                                    System.out.println("Wybrano wydarzenie " + event.getName());
                                    System.out.println("Jeśli chcesz potwierdzić udział w wydarzeniu kliknij 1");
                                    System.out.println("Jeśli chesz zrezygnować z wydarzenia kliknij 2");
                                    System.out.println("Aby wyjść kliknij 0");
                                    int choice1 = Integer.parseInt(scanerI.nextLine());
                                    switch (choice1) {
                                        case 1: {
                                            event.confirmedClients.add(client.getId());
                                            System.out.println("Dziękujemy za dokonanie potwierdzenia, kliknij dowolny przycisk aby kontynuować");
                                            event.confirmedClients.add(client.getId());
                                            File file = new File("ComfirmedClients.txt");
                                            try (FileWriter fw = new FileWriter(file, true)) {
                                                fw.write(event.getIdEvent() + ";" + client.getId() + "\n");
                                            } catch (IOException e) {
                                                throw new RuntimeException(e.getMessage());
                                            }
                                            scanerI.nextLine();
                                            break;
                                        }
                                        case 2: {
                                            String sEventId = String.format("%d", event.getIdEvent());
                                            String sClientId = String.format("%d", client.getId());
                                            String removeLine = sEventId + ";" + sClientId;
                                            List<String> lines = Files.readAllLines(Path.of("EventAttendees.txt"));
                                            lines.remove(removeLine);
                                            Files.write(Path.of("EventAttendees.txt"), lines);
                                            System.out.println("Twoja rezerwacja została odwołana. Zapraszamy ponownie.\nKliknij dowolny przycisk aby kontynuować");
                                            scanerI.nextLine();
                                            break;
                                        }
                                        case 0:
                                            break;
                                    }


                                }

                                case 4:{
                                    fillTookPlace();
                                    for(Event event : clientEvents){
                                        if(event.isTookPlace()){
                                            System.out.println(event.getName());
                                        }
                                    }
                                    System.out.println();
                                    System.out.println();
                                    System.out.println("Kliknij dowolny przeycisk aby przejśc dalej");
                                    scanerI.nextLine();
                                    break;

                                }
                            }
                        }
                            break;
                    }
                    case 2: {


                        System.out.println("Witaj w naszym sklepie GIFCIOR\nPodaj swoje imie - ");
                        String name = scanerI.nextLine();
                        numberOfClients = clientDao.clients.size() + 1;
                        Client client = new Client(numberOfClients, name);
                        clientDao.save(client);


                        break;
                    }
                }


        saveClientToFile();
        }


        } catch (FileNotFoundException e){
            System.out.println("Nie znaleziono pliku o takiej nazwie " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Błąd odczytu pliku - sprawdz czy nie jest on otwarty " + e.getMessage());
        }finally {
            if(scannerf != null){
                scannerf.close();
            }
        }


    }

    private synchronized void saveClientToFile() throws IOException {

        try(RandomAccessFile raFille = new RandomAccessFile(file, "rw")){
            FileChannel channel = raFille.getChannel();
            FileLock lock = channel.lock();
        }

        try (FileWriter fw = new FileWriter(file)){
            for(Client c : clientDao.clients){
                fw.write(c.getId() + ";" + c.getName() + "\n");
            }

        }
    }
    private void deleteClient(Client client) throws IOException {
        clientDao.delete(client);
        int id = client.getId();
        for(Client c : clientDao.clients){
            if(c.getId() > id){
                c.setId(c.getId() - 1);
            }
        }

    }

    private void clearPage(){
        for(int i = 0; i<20; i++){
            System.out.println();
        }
    }

    private void clientToEvent(int choice, Client client){
        Event event = allEvents.events.get(choice-1);
        event.interestedId.add(client.getId());

        File file = new File("EventAttendees.txt");

        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(choice + ";" + client.getId() +"\n");
            fw.close();



        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    private void fillEventList(){

        allEvents.events.clear();


        try(FileReader fr = new FileReader("Events.txt")){
            Scanner scanner = new Scanner(fr);
            String line;
            File file = new File("Events.txt");
            if(file.length() == 0 ){
                return;
            }
            while (scanner.hasNextLine()){
                line = scanner.nextLine();
                String [] buf;
                buf = line.split(";");
                int index = Integer.parseInt(buf[0]);
                String name = buf[2];
                Event event = new Event(index, name);
                allEvents.events.add(event);
            }

        } catch (IOException e) {
            System.out.println("Błąd z odczytem pliku. Jest on otwarty przez inną aplikacje lub jest pusty");
        }
    }

    private void printYourEvents(Client client){
        File file = new File("EventAttendees.txt");
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            Scanner scanner = new Scanner(fr);
            String line;
            int counter = 1;
            while (scanner.hasNextLine()){
                line = scanner.nextLine();
                String [] buff;
                buff = line.split(";");
                int eventId = Integer.parseInt(buff[0]);
                int clientId = Integer.parseInt(buff[1]);
                if (clientId == client.getId()){
                    Event event = allEvents.events.get(eventId-1);
                    clientEvents.add(event);
                    String info;
                    if(event.getParameters() == null){
                        info = "Jescze nie wybrano terminu. Prosimy o cierpliwość";
                    }
                    else {
                        info = event.getParameters();
                    }
                    System.out.println(counter + ".  " +  event.getName() + " - " + info);
                    counter++;
                }

            }

        } catch (FileNotFoundException e) {

            throw new RuntimeException(e);
        }
    }

    private void fillEventParams(){
        File file = new File("EventData.txt");
        try {

            FileReader fr = new FileReader(file);
            Scanner scanner = new Scanner(fr);
            String line;
            while (scanner.hasNextLine()){
                line = scanner.nextLine();
                String [] buff;
                buff = line.split(";");
                String eventName = buff[0];
                String eventData = buff[1];
                Optional <Event> event = allEvents.events.stream().filter(event1 -> event1.getName().equals(eventName)).findFirst();
                if(event.isPresent()) {
                    event.get().setParameters(eventData);
                }
                else {
                    System.out.println("Nieoczekiwany błąd w wyszukiwaniu wydarzenia");
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillTookPlace(){
        File file = new File("TookPlace.txt");

        try(FileReader fr = new FileReader(file)){

            Scanner scanner = new Scanner(fr);
            while (scanner.hasNextLine()){
                int index = Integer.parseInt(scanner.nextLine());
                Event event = allEvents.events.get(index-1);
                event.tookPlace();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
