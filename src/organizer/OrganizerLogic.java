package organizer;

import Events.AllEvents;
import Events.Event;
import seller.Seller;

import java.io.*;
import java.util.Optional;
import java.util.Scanner;

public class OrganizerLogic {
    Scanner scanner = new Scanner(System.in);
    AllEvents allEvents = new AllEvents();

    public void app() {
        fillEventList();
        fillConfirmedClients();
        fillEventParams();

        while (true){
        System.out.println("Witamy! Organizatorem którego wydarzenia jesteś?");
        System.out.println();
        printEvents(allEvents);

        int choice = Integer.parseInt(scanner.nextLine());
        fillReadyToRealize();

        Event event = allEvents.events.get(choice - 1);
        int logout = 0;
        System.out.println("Witaj organizatorze numer " + choice);
        while (logout == 0) {
            fillConfirmedClients();
            fillEventParams();
            fillInterestedClients();
            System.out.println("Przed  tobą lista zainteresowanych osób twojego wydarzenia: ");

            for (int i = 0; i < event.interestedId.size(); i++) {
                System.out.print(event.interestedId.get(i) + ", ");
            }
            if (!event.getReadyToRealize()) {
                System.out.println("\nSprzedawca nie wyraził jeszcze zgody na realizacje twojego wydarzenia. Prosimy o cierpliwość");
            } else {
                fillConfirmedClients();
                System.out.println("\nTwoje wydarzenie jest gotowe do realizacji!");
                System.out.println("A tutaj lista osób które potwierdziły przybycie");
                //-------------------------------------------
                System.out.print(event.getName() + " - ");
                for (int i = 0; i < event.confirmedClients.size(); i++) {
                    System.out.print(event.confirmedClients.get(i) + ", ");
                }
                System.out.println();
                System.out.println();
            }
            //--------------------------------------------
            if (event.getParameters() == null) {
                System.out.println("Czy chcesz przejść do ustalenia daty?\nJeśli tak kliknij 1\nJeśli nie kliknij 2\nJeśli chcesz sie wylogować kliknij 0");
                int realize = Integer.parseInt(scanner.nextLine());
                switch (realize) {
                    case 0: {
                        logout = 1;
                        break;
                    }
                    case 1: {
                        System.out.println("Kiedy ma się odbyć? (Podaj date w systemie DD.MM.RRRR, godz XX:XX) ");
                        String data = scanner.nextLine();
                        event.setParameters(data);
                        saveData(event, data);
                        System.out.println("Dziękujemy za dokonanie informacji. Zapraszamy ponownie!");
                        System.out.println("Aby przejść dalej kliknij dowolny przycisk");
                        System.out.println();
                        System.out.println();
                        scanner.nextLine();
                        break;
                    }
                    case 2:
                        break;
                }
            } else {
                System.out.println("Czy chcesz już potwierdzić wydarzenie?\nJeśli tak kliknij 1\nJeśli nie kliknij 2\nWyloguj się kliknij 0");
                int choice1 = Integer.parseInt(scanner.nextLine());
                switch (choice1) {
                    case 0:{
                        logout = 1;
                    }
                    case 1: {
                        event.organizerAccept();
                        System.out.println("Zamówienie jest gotowe do realizacji. Dziękujemy");

                        saveAccept(event);

                        System.out.println("Kliknij dowolny przycisk aby kontynuować");
                        scanner.nextLine();
                        break;
                    }
                    case 2:
                        break;
                }
            }
        }
        }
    }

    public void printEvents(AllEvents allEvents){
        int counter = 1;
        for(Event event : allEvents.events){
            System.out.println(counter + ". " + event.getName());
            counter++;
        }
    }

    private void fillEventList(){
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

    private void fillInterestedClients(){
        File file = new File("EventAttendees.txt");

        for(Event event : allEvents.events){
            event.interestedId.clear();
        }

        try(FileReader fr = new FileReader(file)){
        Scanner scanner = new Scanner(fr);
        String line;
        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            String[] buff;
            buff = line.split(";");
            int eventId = Integer.parseInt(buff[0]);
            int index = Integer.parseInt(buff[1]);
            Event event = allEvents.events.get(eventId-1);
            event.interestedId.add(index);

        }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveData( Event event, String when){
        File file = new File("EventData.txt");
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(event.getName() + ";" + when + "\n" );
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    private void fillReadyToRealize(){
        File file = new File("ReadyToRealize.txt");
        try {
            FileReader fr = new FileReader(file);
            Scanner scanner = new Scanner(fr);
            while (scanner.hasNextLine()){
                int index = Integer.parseInt(scanner.nextLine());
                Event event = allEvents.events.get(index-1);
                event.setReadyToRealize();

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void fillParameters(){
        File file = new File("EventData.txt");
        try(FileReader fr = new FileReader(file)) {

            Scanner scanner = new Scanner(fr);
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String [] buff;
                buff = line.split(";");
                String name = buff[0];
                String parameters = buff[1];
                Optional<Event> optionalEvent = allEvents.events.stream().filter(event -> event.getName().equals(name)).findFirst();
                Event event = optionalEvent.get();
                event.setParameters(parameters);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
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

    private void saveAccept(Event event){
        File file = new File("OrganizerAcceptedEvents.txt");

        try(FileWriter fw = new FileWriter(file)) {

            fw.write(event.getIdEvent() + "\n");
            fw.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
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
}
