package cw1a;

/**
 *
 * @author DL 2025-01
 */
public class ContactsHashOpen implements IContactDB {  
    private final int initialTableCapacity = 1019; // Prime number for better hash distribution
    private Contact[] table;
    private int tableCapacity;
    private int numEntries;
    private int totalVisited = 0;

    private static final double maxLoadFactor = 50.0; // Load factor at which resizing occurs
    private static final Contact TOMBSTONE = new Contact("TOMBSTONE", "DELETED"); // Marker for deleted entries

    public int getNumEntries() { return numEntries; }
    public void resetTotalVisited() { totalVisited = 0; }
    public int getTotalVisited() { return totalVisited; }

    public ContactsHashOpen() {
        System.out.println("Hash Table with Quadratic Probing & Tombstone Deletion");
        this.tableCapacity = initialTableCapacity;
        table = new Contact[tableCapacity];
        clearDB();
    }

    /**
     * Empties the database.
     */
    public void clearDB() {
        for (int i = 0; i != table.length; i++) {
            table[i] = null;
        }
        numEntries = 0;
    }

    /**
     * 
     */
    private int hash(String s) {
        assert s != null && !s.trim().equals(""); 
        
        int hash = 5381;  // Prime constant
        for (int i = 0; i < s.length(); i++) {
            hash = ((hash << 5) + hash) + s.charAt(i);  // hash * 33 + s[i]
        }
        
        return Math.abs(hash) % table.length;  // Ensure positive hash within bounds
    }

    private double loadFactor() {
        return (double) numEntries / (double) table.length * 100.0; 
    }

    /**
     * Quadratic Probing implementation in `findPos()`, now correctly reusing tombstones
     */
    private int findPos(String name) {
        assert name != null && !name.trim().equals("");
        int pos = hash(name);
        int i = 1; // Quadratic probing index
        int firstTombstone = -1; // Track the first available tombstone

        System.out.println("Finding " + pos + ": " + name);

        while (table[pos] != null) {
            if (table[pos] == TOMBSTONE) {
                if (firstTombstone == -1) {
                    firstTombstone = pos; // Save first tombstone location for reuse
                }
            } else if (name.equals(table[pos].getName())) {
                return pos; // Name found
            }

            System.out.println("Visiting bucket " + pos + ": " + table[pos]);
            pos = (pos + (i * i)) % table.length; // Quadratic probing
            i++;
        }

        System.out.println("Number of buckets visited = " + i);
        totalVisited += i;

        return (firstTombstone != -1) ? firstTombstone : pos; // Use tombstone if available
    }

    public boolean containsName(String name) {
        assert name != null && !name.equals("");
        int pos = findPos(name);
        return table[pos] != null && table[pos] != TOMBSTONE;
    }

    @Override
    public Contact get(String name) {
        assert name != null && !name.trim().equals("");
        int pos = findPos(name);       
        if (table[pos] == null || table[pos] == TOMBSTONE) {
            return null; // Not found
        } 
        return table[pos];
    }

    public int size() { return numEntries; }
    @Override
    public boolean isEmpty() { return numEntries == 0; }

    private Contact putWithoutResizing(Contact contact) {
        String name = contact.getName();
        int pos = findPos(name);
        Contact previous;
        assert table[pos] == null || name.equals(table[pos].getName()) || table[pos] == TOMBSTONE;
        previous = table[pos]; // Store previous contact

        if (previous == null || previous == TOMBSTONE) { // New or replacing a tombstone
            table[pos] = contact;
            numEntries++;
        } else {
            table[pos] = contact; // Overwrite existing
        }
        return previous;
    }

    public Contact put(Contact contact) {
        assert contact != null;
        Contact previous;
        String name = contact.getName();
        assert name != null && !name.trim().equals("");
        previous = putWithoutResizing(contact);

        if (previous == null && loadFactor() > maxLoadFactor) resizeTable();
        return previous;
    }

    /**
     * Removes a contact by replacing it with a tombstone instead of null.
     */
    public Contact remove(String name) {
        assert name != null && !name.trim().equals("");
        int pos = findPos(name);

        if (table[pos] == null || table[pos] == TOMBSTONE) {
            System.out.println(name + " not found.");
            return null; // Name does not exist
        }

        Contact removed = table[pos]; // Store removed contact
        table[pos] = TOMBSTONE; // Mark position as deleted
        numEntries--;

        System.out.println(name + " removed successfully.");
        return removed;
    }

    public void displayDB() {
        System.out.println("Capacity " + table.length + " Size " + numEntries
                + " Load Factor " + loadFactor() + "%");

        for (int i = 0; i != table.length; i++) {
            if (table[i] != null && table[i] != TOMBSTONE) 
                System.out.println(i + " " + table[i].toString());
            else if (table[i] == TOMBSTONE)
                System.out.println(i + " " + "TOMBSTONE");
            else
                 System.out.println(i + " " + "_____");
        }
    }

    private void resizeTable() {
        System.out.println("RESIZING");
        Contact[] oldTable = table;
        int oldTableCapacity = tableCapacity;
        tableCapacity = oldTableCapacity * 2;
        System.out.println("Resizing to " + tableCapacity);
        table = new Contact[tableCapacity];

        clearDB();
        numEntries = 0;
        for (int i = 0; i != oldTableCapacity; i++) {
            if (oldTable[i] != null && oldTable[i] != TOMBSTONE) { 
                putWithoutResizing(oldTable[i]); // Rehash only valid entries
            }
        }
    }
}
