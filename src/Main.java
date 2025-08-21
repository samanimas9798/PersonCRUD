import com.samanimas.enumeration.Gender;
import com.samanimas.model.Person;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main class encompasses the database and manages its functions via CSV file
 * The functions include adding, showing, updating, deleting, and finding persons in the database
 */

public class Main {

    private static final String FILE_PATH = "./database";
    private static final String FILE_NAME = "Database.csv";

    /**
     * Encompasses the user interface and how the database can be interacted with by the user
     * @param args
     */
    public static void main(String args[]) {
        Path database;

        try {
            database = initializeDatabase();

        } catch (IOException e) {
            throw new RuntimeException("Unable to create a file database. The program will now end.");
        }



        Scanner scanner = new Scanner(System.in);
        ArrayList<Person> persons;

        try {
            persons = loadData(database);
        } catch (IOException e) {
            persons = new ArrayList<>();
            System.out.println("Failed to load existing records, starting fresh");

        }



        while (true) {
            System.out.println("========================================================== ");
            System.out.println("(1) Add Person");
            System.out.println("(2) Show All Persons");
            System.out.println("(3) Update Person");
            System.out.println("(4) Delete Person");
            System.out.println("(5) Find Person");
            System.out.println("(99) Exit Program");
            System.out.println("========================================================== ");
            System.out.print("Choose option: ");


            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Try again.");
                scanner.nextLine();
                continue;
            }

            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 99) {
                System.out.println("Thank you for using this application!");
                break;
            } else if (option == 1) {

                System.out.println("Adding a new person...");

                System.out.print("Enter ID: ");
                String idInput = scanner.nextLine().trim();

                if (!idInput.matches("\\d+")) {

                    System.out.println("Invalid ID. Try again.");
                    continue;

                }

                int id = Integer.parseInt(idInput);

                if (persons.stream().anyMatch(p -> p.getId() == id )) {
                    System.out.println("ID is already used. Try again.");
                    continue;

                }

                System.out.print("First Name: ");
                String firstName = scanner.nextLine().trim();

                System.out.print("Last Name: ");
                String lastName = scanner.nextLine().trim();

                System.out.print("Birthdate (YYYY-MM-DD): ");
                String birthdateInput = scanner.nextLine().trim();
                if (!birthdateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    System.out.println("Invalid birthdate. Try again. Please follow the format YYYY-MM-DD.");
                    continue;
                }
                LocalDate birthdate = LocalDate.parse(birthdateInput);


                System.out.print("Gender (0 – Male, 1 – Female): ");
                String genderInput = scanner.nextLine().trim();
                Gender gender;

                if (genderInput.equals("0")) {
                    gender = Gender.MALE;
                }
                else if (genderInput.equals("1")) {
                    gender = Gender.FEMALE;
                }
                else {
                    System.out.println("Invalid input. Try again.");
                    continue;
                }

                persons.add(new Person(id,firstName,lastName,birthdate,gender));
                System.out.println("PERSON ADDED!");

                try {
                    saveData(database, persons);
                } catch (IOException e) {
                    System.out.println("Error saving list: " + e.getMessage());
                }

            }
            else if (option == 2) {
                if (persons.isEmpty()) {
                    System.out.println("No persons added.");
                }
                else {
                    System.out.println("Showing person list... ");
                    for (Person person : persons) {
                        System.out.println(person.getId() + " - " + person.getFirstName() + " " + person.getLastName() + " - " + person.getGender() + " - "+ person.getBirthdate());
                    }

                    try {
                        saveData(database, persons);
                    } catch (IOException e) {
                        System.out.println("Error saving list: " + e.getMessage());
                    }

                }

            }
            else if (option == 3) {
                System.out.print("Enter Person ID to update: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Try again.");
                    scanner.nextLine();
                    continue;
                }

                String idInput = scanner.nextLine().trim();
                int updateId = Integer.parseInt(idInput);

                if (persons.stream().noneMatch(p -> p.getId() == updateId )) {

                    System.out.println("ID not found. Try again.");

                }
                else if (persons.stream().anyMatch(p -> p.getId() == updateId)) {
                    Person person = persons.stream().filter(p -> p.getId() == updateId).findFirst().orElse(null);

                    System.out.print("Enter new first name (" + person.getFirstName() + "): ");
                    String firstName = scanner.nextLine().trim();

                    System.out.print("Enter new last name (" + person.getLastName() + "): ");
                    String lastName = scanner.nextLine().trim();


                    System.out.print("Enter new gender (" + person.getGender() + ") (0 - Male, 1 - Female): ");
                    String genderInput = scanner.nextLine().trim();
                    Gender gender;
                    if (genderInput.equals("0")) {
                        gender = Gender.MALE;
                    }
                    else if (genderInput.equals("1")) {
                        gender = Gender.FEMALE;
                    }
                    else {
                        System.out.println("Invalid input. Try again.");
                        continue;
                    }


                    System.out.print("Enter new birthdate (" + person.getBirthdate() + "): ");
                    String birthdateInput = scanner.nextLine().trim();
                    if (!birthdateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        System.out.println("Invalid birthdate. Try again. Please follow the format YYYY-MM-DD.");
                        continue;
                    }
                    LocalDate birthdate = LocalDate.parse(birthdateInput);

                    person.setFirstName(firstName);
                    person.setLastName(lastName);
                    person.setBirthdate(birthdate);
                    person.setGender(gender);


                    System.out.println("PERSON UPDATED!");

                    try {
                        saveData(database, persons);
                    } catch (IOException e) {
                        System.out.println("Error saving list: " + e.getMessage());
                    }


                }



            }
            else if (option == 4) {

                System.out.print("Enter Person ID to delete: ");

                if (!scanner.hasNextInt()) {

                    System.out.println("Invalid input. Try again.");
                    scanner.nextLine();
                    continue;

                }

                int deleteId = scanner.nextInt();
                scanner.nextLine();

                if (persons.stream().noneMatch(p -> p.getId() == deleteId )) {
                    System.out.println("ID not found. Try again.");


                } else if (persons.stream().anyMatch(p -> p.getId() == deleteId)) {
                    Person person = persons.stream().filter(p -> p.getId() == deleteId).findFirst().orElse(null);
                    System.out.print("Confirm deletion of " + person.getFirstName() + " " + person.getLastName() + " " + "(Y/N)?" + " ");
                    String deleteChoice = scanner.nextLine().trim();
                    if (deleteChoice.equals("Y") || deleteChoice.equals("y")) {
                        System.out.println("PERSON DELETED!");
                        persons.remove(person);

                        try {
                            saveData(database, persons);
                        } catch (IOException e) {
                            System.out.println("Error saving list: " + e.getMessage());
                        }
                    }
                    else if (deleteChoice.equals("N") || deleteChoice.equals("n")) {
                        System.out.println("Deletion cancelled. Returning to main menu...");


                    } else {
                        System.out.println("Invalid input. Try again.");

                    }
                }

            }
            else if (option == 5) {

                System.out.print("Enter Person ID to find: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Try again.");
                    scanner.nextLine();
                    continue;
                }

                int findId = scanner.nextInt();
                scanner.nextLine();


                if (persons.stream().noneMatch(p -> p.getId() == findId )) {

                    System.out.println("Person not found. Try again.");

                }

                else if (persons.stream().anyMatch(p -> p.getId() == findId)) {
                    Person person = persons.stream().filter(p -> p.getId() == findId).findFirst().orElse(null);
                    System.out.println("Person found: ");
                    System.out.println(person.getFirstName() + " " + person.getLastName() + " - " + person.getGender() + " - " + person.getBirthdate());
                }
            }
        }

    }
    /**
     * This creates the file wherein the data of the persons are saved
     * @return The path to the database file
     * @throws IOException If the file cannot be created
     */
    private static Path initializeDatabase() throws IOException {
        Path filePath = Paths.get(FILE_PATH, FILE_NAME);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);

        }
        return filePath;
    }
    /**
     * This reads the content from the database file
     * @param dbFile The path to the database file
     * @return The strings representing the person data
     * @throws IOException If an error occurs during file reading
     */

    private static List<String> readData(Path dbFile) throws IOException {
        List<String> fileContent = Files.readAllLines(dbFile);

        if (fileContent.size() <= 1) {
            return new ArrayList<>();
        }
        return fileContent.subList(1,fileContent.size());
    }

    /**
     *  This loads the data from the database file and aligns it with objects in the Person class
     *
     * @param dbFile The path to the database file
     * @return The list of persons loaded from the file
     * @throws IOException If an error occurs during file reading
     */


    private static ArrayList<Person> loadData(Path dbFile) throws IOException {
        ArrayList<Person> persons = new ArrayList<>();
        List<String> lines = readData(dbFile);

        for (String line : lines) {
            String[] column = line.split(",");
            if (column.length == 5) {
                int id = Integer.parseInt(column[0].trim());
                String firstName = column[1].trim();
                String lastName = column[2].trim();
                Gender gender = column[3].trim().equals("0") ? Gender.MALE : Gender.FEMALE;
                LocalDate birthdate = LocalDate.parse(column[4].trim());

                Person person = new Person (id,firstName,lastName, birthdate, gender);
                persons.add(person);
            }
        }
        return persons;
    }

    /**
     * This saves all the data of the persons into the file.
     * @param database The path to the database file
     * @param persons The list of Person objects to save
     * @throws IOException If an error occurs while writing to the file
     */
    private static void saveData(Path database, List<Person> persons) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("ID, FirstName, LastName, Gender, Birthdate");

        for (Person person : persons) {
            lines.add(person.getId() + "," + person.getFirstName() + "," + person.getLastName() + "," + (person.getGender() == Gender.MALE ? "0" : "1") + "," + person.getBirthdate());
        }
        Files.write(database, lines);
    }


}




