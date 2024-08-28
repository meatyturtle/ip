import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;


public class Sigma {
    public static ArrayList<Task> items = new ArrayList<>();

    public static String getItemsFromFile(String path) throws IOException {
        // there's gotta be a better way to do this
        StringBuilder itemsFromFile = new StringBuilder();
        int i = 1;
        for (String line: Files.readAllLines(Paths.get(path))) {
            itemsFromFile.append(i).append(". ").append(line).append("\n");
            i++;
        }
        return itemsFromFile.toString();
    }

    // convert lines read from file to Task objects
    public static void readTasksFromFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("data/sigma.txt"));
            // handle empty file case
            if (lines.isEmpty()) {
                return;
            }
            for (String line : lines) {
                // parse each line
                String[] parts = line.split(" ", 2); // Split at the first space
                String status = parts[0];
                String taskName = parts[1];
                boolean isDone = status.equals("[X]");
                Task task = new Task(taskName, isDone);
                items.add(task);
            }
        } catch (IOException e) {
            System.err.println("error reading lines from file: " + e.getMessage());
        }
    }

    public static String toPrettyList(List<Task> itemsArray) throws IOException {
//        StringBuilder itemsFromFile = new StringBuilder();
//        for (String line: Files.readAllLines(Paths.get("data/sigma.txt"))) {
//            itemsFromFile.append(line);
//        }
//        StringBuilder itemsFromFile = new StringBuilder();
        int i = 1;
//        for (String line: Files.readAllLines(Paths.get("data/sigma.txt"))) {
//            itemsFromFile.append(i).append(". ").append(line).append("\n");
//            i++;
//        }
//        if (itemsArray.isEmpty()) {
//            return "";
//        }
        StringBuilder result = new StringBuilder(); // this is a terrible time complexity
        for (Task item : itemsArray) {
            result.append(i).append(". ").append(item.toString()).append("\n");
            i++;
        }
        return result.toString();
    }

    // update this also
    public static void handleMarkUnmark(String userInput) {
        Pattern pattern = Pattern.compile("(mark|unmark) (\\d+)");
        Matcher matcher = pattern.matcher(userInput);

        if (matcher.find()) {
            String action = matcher.group(1);
            int taskNumber = Integer.parseInt(matcher.group(2)) - 1;

            if (taskNumber >= 0 && taskNumber < items.size()) {
                Task task = items.get(taskNumber);
                if (action.equals("mark")) {
                    task.setStatus(true);
                    System.out.println("task marked as done:\n" + task);
                } else if (action.equals("unmark")) {
                    task.setStatus(false);
                    System.out.println("task unmarked:\n" + task);
                }
            }
        }
    }

    // need to update this
    public static void handleDelete(String userInput) {
        Pattern pattern = Pattern.compile("(delete) (\\d+)");
        Matcher matcher = pattern.matcher(userInput);

        if (matcher.find()) {
            int taskNumber = Integer.parseInt(matcher.group(2)) - 1;
            if (taskNumber >= 0 && taskNumber < items.size()) {
                Task task = items.get(taskNumber);
                items.remove(task);
                System.out.println("task removed:\n" + task.toString() + "\nNow you have " + items.size() + " tasks in the list");
            }
        }
    }

    public static String stringToDate(String date) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        return LocalDateTime.parse(date, formatter);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsedDateTime = LocalDateTime.parse(date, inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss");
        return parsedDateTime.format(outputFormatter);
    }

    public static void main(String[] args) throws IOException {
//        items = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        FileWriter writer = new FileWriter("data/sigma.txt", true);

        String welcomeMessage = "Hello! I'm Sigma \nWhat can I do for you? \n";
        System.out.println(welcomeMessage);

        // create the file
        try {
            File file = new File("data/sigma.txt");
            if (file.createNewFile()) {
                System.out.println("New file created: " + file.getName());
            } else {
//                 // file already exists, open the file
//                for (String line: Files.readAllLines(Paths.get("data/sigma.txt"))) {
//                    System.out.println(line);
//                    // extra: handle situation of file being corrupted
//                }
//                System.out.println(getItemsFromFile("data/sigma.txt"));
//                System.out.println(toPrettyList(items));
            }
        } catch (IOException e) {
            System.err.println("error occurred creating file or reading from file: " + e.getMessage());
        }

        // on start, transfer everything from the file to dynamic array
        // if its stupid but it works then its not stupid
        readTasksFromFile();
        System.out.println(toPrettyList(items));
        // this will update the 'items' ArrayList

        // run program
        while (scanner.hasNext()) {
            // could probably replace this with switch
            String userInput = scanner.nextLine();
            if (userInput.contains("list")) {
//                if (items.isEmpty()) {
//                    System.out.println("no tasks for today! good job!");
//                }
                System.out.println("Here are your sussy amogus tasks:\n" + toPrettyList(items));
                continue;
            }
            if (userInput.contains("bye")) {
                System.out.println("leaving so soon? dattebayo!");
                // save the file contents
                writer.flush();
                writer.close();
                break;
            }

            if (userInput.startsWith("mark") || userInput.startsWith("unmark")) {
                handleMarkUnmark(userInput);
                continue;
            }

            if (userInput.startsWith("delete")) {
                handleDelete(userInput);
                continue;
            }

            if (userInput.startsWith("todo")) {
                String description = userInput.substring(4).trim(); // trims the input away
                if (description.isEmpty()) { // handle empty input
                    System.out.println("todo...todo what? let's try this again");
                    continue;
                }
                Task task = new ToDo(description, false);
                items.add(task);
                try {
                    writer.write(task + "\n");
                } catch (IOException exception) {
                    System.err.println("an error occurred writing to file: " + exception.getMessage());
                }
                System.out.println("added todo task:\n [T][ ] " + description);
                continue;
            }

            if (userInput.startsWith("deadline")) {
                Pattern pattern = Pattern.compile("deadline (.+) /by (.+)");
                Matcher matcher = pattern.matcher(userInput);
                if (matcher.find()) {
                    String description = matcher.group(1);
                    String by = matcher.group(2);
                    String dateBy = stringToDate(by);
                    Task task = new Deadline(description, false, dateBy);
                    items.add(task);
                    try {
                        writer.write(task + "\n");
                    } catch (IOException exception) {
                        System.err.println("an error occurred writing to file");
                    }
                    System.out.println("added deadline task:\n  [D][ ] " + description + " (by: " + dateBy + ")");
                } else {
                    System.out.println("is the deadline in the room with us? let's try again");
                }
                continue;
            }

            if (userInput.startsWith("event")) {
                Pattern pattern = Pattern.compile("event (.+) /from (.+) /to (.+)");
                Matcher matcher = pattern.matcher(userInput);
                if (matcher.find()) {
                    String description = matcher.group(1);
                    String from = matcher.group(2);
                    String fromDate = stringToDate(from);
                    String to = matcher.group(3);
                    String toDate = stringToDate(to);
                    Task task = new Event(description, false, fromDate, toDate);
                    items.add(task);
                    try {
                        writer.write(task + "\n");
                    } catch (IOException e) {
                        System.err.println("an error occurred writing to file: " + e.getMessage());
                    }
                    System.out.println("added event task:\n  [E][ ] " + description + " (from: " + fromDate + " to: " + toDate + ")");
                } else {
                    System.out.println("bro really thinks bro can make an empty event and get away with it, lets try again");
                }
                continue;
            }
            System.out.println("erm, what the sigma? i don't recognise that command.");
            writer.close(); // do i even need to check for this
        }
    }
}
