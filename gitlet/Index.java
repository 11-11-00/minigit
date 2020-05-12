package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;

/**
 * The main index. Controls Add/remove.
 * @author  Nathan Zhang
 */
@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class Index implements Serializable {
    /**
     * addblobs.
     */
    private static HashSet<String> addblobs = new HashSet<>();
    /**
     * addblobs.
     * @return addblobs.
     */
    public static HashSet<String> addblobs() {
        return addblobs;
    }
    /**
     * addblobs.
     */
    private static HashSet<String> removeblobs = new HashSet<>();
    /**
     * remblobs.
     * @return  removeblobs
     */
    public static HashSet<String> remblobs() {
        return removeblobs;
    }
    /**
     * addblobs.
     */
    private static File add = Utils.join(Main.getn(), "add");
    /**
     * addblobs.
     */
    private static File remove = Utils.join(Main.getn(), "remove");

    /**
     * Constructs the index.
     */
    public Index() {
        File index = Utils.join(Main.getn(), "index");
        Utils.writeObject(index, this);
        addwrite();
        remwrite();
    }

    /**
     * Adds files in.
     * @param s S is param.
     */
    static void add(String s) {
        File orig = new File(s);
        if (!orig.exists()) {
            throw new GitletException("File does not exist");
        }
        String n = Utils.readContentsAsString(orig);
        if (Main.gettree().getcurrhead().nameexists(s)) {
            String stagen = Main.gettree().getcurrhead().getblobcontents(s);
            if (n.equals(stagen)) {
                if ((new File(Main.getr(), s)).exists()) {
                    (new File(Main.getr(), s)).delete();
                    removeblobs = remread();
                    removeblobs.remove(s);
                    remwrite();
                }
                return;
            }
        }
        File k = Utils.join(Main.gets(), s);
        Utils.writeContents(k, n);
        addblobs = addread();
        Blob b = new Blob(s);
        addblobs.add(b.hash());
        addwrite();
    }

    /**
     * Write to add.
     */
    static void addwrite() {
        Utils.writeObject(add, addblobs);
    }
    /**
     * Write to remove.
     */
    static void remwrite() {
        Utils.writeObject(remove, removeblobs);
    }
    /**
     * Read to add.
     * @return hashset.
     */
    static HashSet addread() {
        return Utils.readObject(add, HashSet.class);
    }
    /**
     * Read to remove.
     * @return HAshset.
     */
    static HashSet remread() {
        return Utils.readObject(remove, HashSet.class);
    }
    /**
     * Read to  clear everything.
     */
    static void clear() {
        addblobs.clear();
        removeblobs.clear();
        addwrite();
        remwrite();
    }

    /**
     * Removes everything.
     * @param n Removel
     */
    static void remove(String n) {
        addblobs = addread();
        removeblobs = remread();
        File k = new File(Main.gets(), n);
        File nfile = new File(n);
        Tree tr = Utils.readObject(Utils.join(Main.getn(), "Tree"), Tree.class);
        Commit c = tr.getcurrhead();
        if (!k.exists()
                & !c.blobsnames().containsKey(n)) {
            throw new GitletException("No reason to remove the file.");
        }
        if (k.exists()) {
            k.delete();
            Index.addblobs.remove((new Blob(n)).hash());
            addwrite();
        }
        if (c.blobsnames().containsKey(n)) {
            removeblobs.add(n);
            File f = Utils.join(Main.getr(), n);
            Utils.writeContents(f, "remove");
            nfile.delete();
            remwrite();
        }
    }


}
