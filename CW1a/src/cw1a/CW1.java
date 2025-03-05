package cw1a;

import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Main class for managing the contact database.
 */
public class CW1 {
    private static Scanner keyboard;
    private static String fileName = "no file chosen";

    // Using ContactsHashChained instead of ContactsHashOpen
    private static IContactDB db = new ContactsHashChained();

    private static boolean acceptable(char ch) {
        return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' ||
                ch == ' ' || ch == ',' || ch == '@' || ch == '.' ||
                ch == '-';
    }

    private static boolean allAcceptable(String s) {
        int i = 0;
        while (i != s.length() && acceptable(s.charAt(i))) i++;
        return i == s.length();
    }

    private static String readAcceptable() {
        String s = keyboard.nextLine().trim();
        while (s == null || s.equals("") || !allAcceptable(s)) {
            System.out.print("All characters must be acceptable -- try again: ");
            s = keyboard.nextLine().trim();
        }
        return s;
    }

    public static void main(String[] args) {
        String option, name, emailAddress;
        Contact resp;
        System.out.println("Starting");
        keyboard = new Scanner(System.in);
        loadFile();

        System.out.print("D)isplay  P)ut  G)et  C)ontains  S)ize  R)emove  Q)uit? ");
        option = readAcceptable();
        while (option.charAt(0) != 'Q' && option.charAt(0) != 'q') {
            switch (option.charAt(0)) {
                case 'D':
                case 'd':
                    db.displayDB();
                    break;
                case 'P':
                case 'p':
                    System.out.print("Name? ");
                    name = readAcceptable();
                    System.out.print("Affiliation? ");
                    emailAddress = readAcceptable();
                    Contact contact = new Contact(name, emailAddress);
                    resp = db.put(contact);
                    System.out.print(name);
                    if (resp == null) {
                        System.out.println(" : new contact added");
                    } else {
                        System.out.println(" : contact overridden");
                        System.out.println("Previous was " + resp.toString());
                    }
                    break;
                case 'S':
                case 's':
                    System.out.println("Size " + db.size());
                    break;
                case 'G':
                case 'g':
                    System.out.print("Name? ");
                    name = readAcceptable();
                    resp = db.get(name);
                    if (resp != null) {
                        System.out.println(resp.toString());
                    } else {
                        System.out.println(name + " not found");
                    }
                    break;
                case 'C':
                case 'c':
                    System.out.print("Name? ");
                    name = readAcceptable();
                    System.out.print(name);
                    if (db.containsName(name)) {
                        System.out.println(" found");
                    } else {
                        System.out.println(" not found");
                    }
                    break;
                case 'R':
                case 'r':
                    System.out.print("Name? ");
                    name = readAcceptable();
                    resp = db.remove(name);
                    System.out.print(name);
                    if (resp != null) {
                        System.out.println(" deleted");
                    } else {
                        System.out.println(" not found");
                    }
                    break;
                default:
                    System.out.println("Unknown option");
            }
            System.out.println();
            System.out.print("D)isplay  P)ut  G)et  C)ontains  S)ize  R)emove  Q)uit? ");
            option = readAcceptable();
        }
    }

    public static File getDataFile(String startFolder) {
        File f;
        JFileChooser fc = new JFileChooser(startFolder);
        fc.setDialogTitle("Choose a file containing data");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int fcReturnValue = fc.showOpenDialog(null);

        if (fcReturnValue != JFileChooser.APPROVE_OPTION) {
            System.out.println("No file selected.");
            return null;
        } else {
            return fc.getSelectedFile();
        }
    }

    private static void loadFile() {
        String cvsSplitBy = ",";
        File file;
        Scanner text;
        int totalVisited;
        Contact contact;
        db.resetTotalVisited();

        try {
            file = getDataFile(".");
            if (file != null) {
                FileInputStream streamIn = new FileInputStream(file);
                text = new Scanner(streamIn);
                while (text.hasNextLine()) {
                    String line = text.nextLine();
                    String[] parts = line.split(cvsSplitBy);
                    if (parts.length < 3) continue;
                    String surname = parts[0].trim();
                    String firstNames = parts[1].trim();
                    String affiliation = parts[2].trim();
                    contact = new Contact(surname + ", " + firstNames, affiliation);
                    db.put(contact);
                }
                text.close();
                totalVisited = db.getTotalVisited();
                System.out.println("Total number of buckets visited = " + totalVisited);
                System.out.printf("Average number of buckets visited =  %.2f", totalVisited / (double) db.getNumEntries());
                System.out.println();
            } else {
                System.out.println("No file selected. Exiting program.");
                System.exit(0);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Can't open chosen file.");
        }
    }
}

