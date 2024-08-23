import java.util.*;
import java.util.regex.*;

public class Sigma {
    public static ArrayList<Task> items;

    public static String toPrettyList(List<Task> itemsArray) {
        StringBuilder result = new StringBuilder(); // this is a terrible time complexity
        for (int i = 0; i < itemsArray.size(); i++) {
            result.append(i + 1).append(". ").append(itemsArray.get(i).toString()).append("\n");
        }
        return result.toString();
    }

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

    public static void handleDelete(String userInput) {
        Pattern pattern = Pattern.compile("(delete) (\\d+)");
        Matcher matcher = pattern.matcher(userInput);

        if (matcher.find()) {
            int taskNumber = Integer.parseInt(matcher.group(2)) - 1;
            if (taskNumber >= 0 && taskNumber < items.size()) {
                Task task = items.get(taskNumber);
                items.remove(task);
                System.out.println("task removed:\n" + task + "\nNow you have " + items.size() + " tasks in the list");
            }
        }
    }

    public static void main(String[] args) throws SigmaException {
        items = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        String welcomeMessage = "Hello! I'm Sigma \nWhat can I do for you? \n";
        System.out.println(welcomeMessage);

        while (scanner.hasNext()) {
            String userInput = scanner.nextLine();
            if (userInput.contains("list")) {
                System.out.println("Here are your sussy amogus tasks:\n" + toPrettyList(items));
                continue;
            }
            if (userInput.contains("bye")) {
                System.out.println("leaving so soon? dattebayo!");
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
                System.out.println("added todo task:\n [T][ ] " + description);
                continue;
            }

            if (userInput.startsWith("deadline")) {
                Pattern pattern = Pattern.compile("deadline (.+) /by (.+)");
                Matcher matcher = pattern.matcher(userInput);
                if (matcher.find()) {
                    String description = matcher.group(1);
                    String by = matcher.group(2);
                    Task task = new Deadline(description, false, by);
                    items.add(task);
                    System.out.println("added deadline task:\n  [D][ ] " + description + " (by: " + by + ")");
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
                    String to = matcher.group(3);
                    Task task = new Event(description, false, from, to);
                    items.add(task);
                    System.out.println("added event task:\n  [E][ ] " + description + " (from: " + from + " to: " + to + ")");
                } else {
                    System.out.println("bro really thinks bro can make an empty event and get away with it");
                }
                continue;
            }
            System.out.println("erm, what the sigma? i don't recognise that command.");
        }
    }
}
