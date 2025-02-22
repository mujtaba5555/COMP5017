package cw1a;

/**
 *
 * @author DL 2025-01
 */
public class ContactsHashOpen implements IContactDB {  
    private final int initialTableCapacity = 1000;
    private Contact[] table;
    private int tableCapacity;
    private int numEntries;
    private int totalVisited = 0;

    private static final double maxLoadFactor = 70.0;
            
    public int getNumEntries(){ return numEntries; }
    public void resetTotalVisited() { totalVisited = 0; }
    public int getTotalVisited() { return totalVisited; }

    public ContactsHashOpen() {
        System.out.println("Hash Table with open addressing");
        this.tableCapacity = initialTableCapacity;
        table = new Contact[tableCapacity];
        clearDB();
    }

    /**
     * Empties the database.
     *
     * @pre true
     */
    public void clearDB() {
        for (int i = 0; i != table.length; i++) {
            table[i] = null;
        }
        numEntries = 0;
    }

    /**
     * Improved Hash Function using polynomial rolling hash (djb2-based)
     *
     * @param s The key (name) to hash
     * @return A hash value mapped to the table size
     */
    private int hash(String s) {
        assert s != null && !s.trim().equals(""); 
        
        int hash = 5381;  // Prime constant
        for (int i = 0; i < s.length(); i++) {
            hash = ((hash << 5) + hash) + s.charAt(i);  // hash * 33 + s[i]
        }
        
        return Math.abs(hash) % table.length;  // Ensure hash is positive and within bounds
    }

    private double loadFactor() {
        return (double) numEntries / (double) table.length * 100.0; 
        // Note: Need for cast to double
    }

    private int findPos(String name) {
        assert name != null && !name.trim().equals("");
        int pos = hash(name);
        int numVisited = 1;  
        System.out.println("Finding " + pos + ": " + name );

        while (table[pos] != null && !name.equals(table[pos].getName())) {
           System.out.println("Visiting bucket " + pos + ": " + table[pos] );
           numVisited++;
           pos = (pos + 1) % table.length; // linear probing
        }  

        System.out.println("Number of buckets visited = " + numVisited);
        totalVisited += numVisited;
      
        assert table[pos] == null || name.equals(table[pos].getName());
        return pos;
    }

    public boolean containsName(String name) {
        assert name != null && !name.equals("");
        int pos = findPos(name);
        return get(name) != null;
    }

    @Override
    public Contact get(String name) {
        assert name != null && !name.trim().equals("");
        Contact result;
        int pos = findPos(name);       
        if (table[pos] == null) {
            result = null; // Not found
        } else {
            result = table[pos];
        }
        return result;
    }

    public int size() { return numEntries; }
    @Override
    public boolean isEmpty() { return numEntries == 0; }

    private Contact putWithoutResizing(Contact contact) {
        String name = contact.getName();
        int pos = findPos(name);
        Contact previous;
        assert table[pos] == null || name.equals(table[pos].getName());
        previous = table[pos]; // Old value

        if (previous == null) { // New entry
            table[pos] = contact;
            numEntries++;
        } else {
            table[pos] = contact; // Overwriting 
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

    public Contact remove(String name) {
        assert name != null && !name.trim().equals("");
        int pos = findPos(name);
        System.out.println("Remove not yet implemented");
        return null;
    }

    public void displayDB() {
        System.out.println("Capacity " + table.length + " Size " + numEntries
                + " Load Factor " + loadFactor() + "%");

        for (int i = 0; i != table.length; i++) {
            if (table[i] != null) 
                System.out.println(i + " " + table[i].toString());
            else
                 System.out.println(i + " " + "_____");
        }
        
        Contact[] toBeSortedTable = new Contact[tableCapacity];
        int j = 0;
        for (int i = 0; i != table.length; i++) {
            if (table[i] != null) {
                toBeSortedTable[j] = table[i];
                j++;
            }
        }

        quicksort(toBeSortedTable, 0, j - 1);
        for (int i = 0; i != j; i++) {
            System.out.println(i + " " + " " + toBeSortedTable[i].toString());
        }
    }

    private void quicksort(Contact[] a, int low, int high) {
        assert a != null && 0 <= low && low <= high && high < a.length;
        int i = low, j = high;
        Contact temp;

        if (high >= 0) { 
            String pivot = a[(low + high) / 2].getName();
            while (i <= j) {
                while (a[i].getName().compareTo(pivot) < 0) i++;
                while (a[j].getName().compareTo(pivot) > 0) j--;
                if (i <= j) {
                    temp = a[i]; a[i] = a[j]; a[j] = temp;
                    i++; j--;
                }
                if (low < j) quicksort(a, low, j);
                if (i < high) quicksort(a, i, high);
            }
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
            if (oldTable[i] != null) {
                putWithoutResizing(oldTable[i]);
            }
        }
    }
} 
