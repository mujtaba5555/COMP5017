package cw1a;

import java.util.*;

/**
 * Implements a hash table using separate chaining.
 */
public class ContactsHashChained implements IContactDB {
    private final int initialTableCapacity = 1019;
    private List<LinkedList<Contact>> table;
    private int numEntries;
    private int totalVisited;

    public ContactsHashChained() {
        table = new ArrayList<>(initialTableCapacity);
        for (int i = 0; i < initialTableCapacity; i++) {
            table.add(new LinkedList<>());
        }
        numEntries = 0;
        totalVisited = 0;
    }

    private int hash(String s) {
        int hash = 5381;
        for (int i = 0; i < s.length(); i++) {
            hash = ((hash << 5) + hash) + s.charAt(i);
        }
        return Math.abs(hash) % table.size();
    }

    @Override
    public int getNumEntries() {
        return numEntries;
    }

    @Override
    public void resetTotalVisited() {
        totalVisited = 0;
    }

    @Override
    public int getTotalVisited() {
        return totalVisited;
    }

    @Override
    public int size() {
        return numEntries;
    }

    @Override
    public boolean isEmpty() {
        return numEntries == 0;
    }

    @Override
    public void clearDB() {
        for (int i = 0; i < table.size(); i++) {
            table.set(i, new LinkedList<>());
        }
        numEntries = 0;
    }

    @Override
    public boolean containsName(String name) {
        assert name != null && !name.isEmpty();
        int index = hash(name);
        LinkedList<Contact> bucket = table.get(index);
        for (Contact contact : bucket) {
            if (contact.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Contact get(String name) {
        assert name != null && !name.isEmpty();
        int index = hash(name);
        LinkedList<Contact> bucket = table.get(index);

        int initialVisits = totalVisited;
        int visitCount = 0;
        for (Contact contact : bucket) {
            visitCount++;
            if (contact.getName().equals(name)) {
                totalVisited += visitCount;
                System.out.println("Finding " + index + ": " + name);
                System.out.println("Visiting bucket " + index + ": " + contact.toString());
                System.out.println("Number of buckets visited = " + (totalVisited - initialVisits));
                return contact;
            }
        }

        totalVisited += visitCount;
        System.out.println("Finding " + index + ": " + name);
        System.out.println(name + " not found.");
        System.out.println("Number of buckets visited = " + (totalVisited - initialVisits));
        return null;
    }

    @Override
    public Contact put(Contact contact) {
        assert contact != null;
        int index = hash(contact.getName());
        LinkedList<Contact> bucket = table.get(index);

        int initialVisits = totalVisited;
        int visitCount = 0;

        for (Contact c : bucket) {
            visitCount++;
            if (c.getName().equals(contact.getName())) {
                bucket.remove(c);
                bucket.add(contact);
                totalVisited += visitCount;
                System.out.println("Finding " + index + ": " + contact.getName());
                System.out.println("Visiting bucket " + index + ": " + c.toString());
                System.out.println("Number of buckets visited = " + (totalVisited - initialVisits));
                return c;
            }
        }

        bucket.add(contact);
        numEntries++;
        totalVisited += visitCount + 1;

        System.out.println("Finding " + index + ": " + contact.getName());
        System.out.println("Number of buckets visited = " + (totalVisited - initialVisits));
        return null;
    }

    @Override
    public Contact remove(String name) {
        assert name != null && !name.isEmpty();
        int index = hash(name);
        LinkedList<Contact> bucket = table.get(index);

        int initialVisits = totalVisited;
        int visitCount = 0;

        Iterator<Contact> iterator = bucket.iterator();
        while (iterator.hasNext()) {
            visitCount++;
            Contact contact = iterator.next();
            if (contact.getName().equals(name)) {
                iterator.remove();
                numEntries--;
                totalVisited += visitCount;
                System.out.println("Finding " + index + ": " + name);
                System.out.println("Visiting bucket " + index + ": " + contact.toString());
                System.out.println("Number of buckets visited = " + (totalVisited - initialVisits));
                return contact;
            }
        }

        totalVisited += visitCount;
        System.out.println("Finding " + index + ": " + name);
        System.out.println(name + " not found.");
        System.out.println("Number of buckets visited = " + (totalVisited - initialVisits));
        return null;
    }

    @Override
    public void displayDB() {
        System.out.println("Capacity: " + table.size() + " Size: " + numEntries);
        for (int i = 0; i < table.size(); i++) {
            LinkedList<Contact> bucket = table.get(i);
            if (!bucket.isEmpty()) {
                System.out.print("Bucket " + i + ": ");
                System.out.println(bucket);
            }
        }
    }
}
