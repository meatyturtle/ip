import java.util.Scanner;
import sigma.*;
@SuppressWarnings("FieldMayBeFinal")
/**
 * The main class from where the program is run
 *
 * @author Qiao Yi
 */
public class Sigma {
    private Ui ui;
    private Storage storage;
    private Parser parser;
    private TaskList taskList;

    /**
     * Constructor for Sigma
     * @param filePath The path to the file where past data is stored
     */
    public Sigma(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        taskList = new TaskList();
        parser = new Parser();
    }

    /**
     * Opens and runs the program
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        storage.readTasksFromFile();

        System.out.println(ui.welcome());
        System.out.println(TaskList.toPrettyList()); // kiv

        while (scanner.hasNext()) {
            String userInput = scanner.nextLine();
            if (userInput.equals("list")) {
                System.out.println(TaskList.toPrettyList());
                continue;
            }

            if (userInput.equals("greet")) {
                System.out.println(parser.greet());
                break;
            }

            if (userInput.equals("bye")) {
                parser.goodbye();
                break;
            }

            if (userInput.startsWith("mark") || userInput.startsWith("unmark")) {
                parser.handleMarkUnmark(userInput);
                continue;
            }
            if (userInput.startsWith("delete")) {
                taskList.handleDelete(userInput);
                continue;
            }
            if (userInput.startsWith("todo")) {
                parser.handleTodo(userInput);
                continue;
            }
            if (userInput.startsWith("deadline")) {
                parser.handleDeadline(userInput);
                continue;
            }
            if (userInput.startsWith("event")) {
                parser.handleEvent(userInput);
                continue;
            }

            if (userInput.startsWith("find")) {
                parser.handleFind(userInput);
                break;
            }
            ui.dontRecognise();
        }
        scanner.close();
    }

    public static void main(String[] args) {
        Sigma sigma = new Sigma("data/sigma.txt");
        sigma.run();
    }
}