package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
/**
 * This represents tree object.
 * @author Nathan Zhang
 */

@SuppressWarnings({"serial", "unchecked"})

public class Tree implements Serializable {
    /**
     * curr string.
     */
    private String _curr;

    /**
     * Get the string.
     * @return the strung.
     */
    public String cur() {
        return _curr;
    }
    /**
     * curr commits.
     */
    private HashSet<String> commits = new HashSet<String>();
    /**
     * Get the commits.
     * @return the strung.
     */
    public HashSet<String> com() {
        return commits;
    }
    /**
     * curr branches for finding.
     */
    private HashMap<String, String> branches = new HashMap<String, String>();
    /**
     * Get the commits.
     * @return the strung.
     */
    public HashMap<String, String> branches() {
        return branches;
    }
    /**
     * curr branches for iterating.
     */
    private HashSet<String> branches2 = new HashSet<String>();
    /**
     * Get the commits.
     * @return the strung.
     */
    public HashSet<String> branches2() {
        return branches2;
    }
    /**
     * abbreviations.
     */
    private HashMap<String, String> abbrev = new HashMap<String, String>();
    /**
     * Get the commits.
     * @return the strung.
     */
    public HashMap<String, String> abbrev() {
        return abbrev;
    }
    /**
     * initial hash.
     */
    private String _init;
    /**
     * Get the commits.
     * @return the strung.
     */
    public String init() {
        return _init;
    }

    /**
     * remote names.
     */
    private HashMap<String, String> remote = new HashMap<String, String>();
    /**
     * Get the commits.
     * @return the strung.
     */
    public HashMap<String, String> remote() {
        return remote;
    }

    /**
     * Constructor hehe.
     */
    public Tree() {
        _curr = "master";
        branches2.add(_curr);
        write();

    }
    /**
     * Get the commits.
     * @param nmd  The key.
     */
    public void brrem(String nmd) {
        branches.remove(nmd);
        write();
    }
    /**
     * Get the commits.
     * @param md the key.
     * @param lsd the object.
     */
    public void brput(String md, String lsd) {
        branches.put(md, lsd);
        write();
    }

    /**
     * adds the commit.
     * @param msg message.
     * @param add add.
     * @param rm remove.
     */
    void addcommit(String msg, HashSet<String> add, HashSet<String> rm) {
        if (msg.equals("") & add != null) {
            throw new GitletException("Please enter a commit message.");
        }
        String cur = getcurrbranch();
        Commit newhead = new Commit(msg);
        if (msg.equals("") & add == null) {
            newhead = new Commit();
        }
        if (add != null) {
            Commit oldhead = Main.getcommit(branches.get(cur));
            Iterator iter = add.iterator();
            while (iter.hasNext()) {
                String s = (String) iter.next();
                File blobfile = Utils.join(Main.getn(), s);
                Blob bl = Utils.readObject(blobfile, Blob.class);
                if (Utils.join(Main.gets(), bl.name()).exists()) {
                    newhead.addblob(bl.hash());
                    newhead.bnput(bl.name(), bl.contents());
                    Utils.join(Main.gets(), bl.name()).delete();
                }
            }
            iter = oldhead.blobnameiter();
            while (iter.hasNext()) {
                String b = (String) iter.next();
                Blob bl = Utils.readObject(Utils.join(Main.getn(),
                         b), Blob.class);
                if (!newhead.blobsnames().containsKey(bl.name())
                        & !Utils.join(Main.getr(), bl.name()).exists()) {
                    Utils.readObject(Utils.join(Main.getn(),
                            bl.hash()), Blob.class);
                    newhead.addblob(bl.hash());
                    newhead.bnput(bl.name(), bl.contents());
                } else {
                    Utils.join(Main.getr(), bl.name()).delete();
                }
            }
            branches.remove(_curr, oldhead.hash());
            newhead.setp(oldhead.hash());
            branches.put(_curr, newhead.hashcode());
            newhead.tofile(Main.getn());
            write();
            Index.clear();
            Index.addwrite();
            Index.remwrite();

        } else {
            branches.put(_curr, newhead.hashcode());
            write();
            newhead.tofile(Main.getn());
            _init = newhead.hashcode();
        }
        commits.add(newhead.hashcode());
        abbrev.put(newhead.hashcode().substring(0, 8), newhead.hashcode());
        write();
    }

    /**
     * Add commit.
     */
    void addcommit() {
        addcommit("", null, null);
    }

    /**
     * adds to branch.
     * @param n add branch.
     */
    void addbranch(String n) {
        if (branches2.contains(n)) {
            throw new GitletException("A branch"
                  +  " with that name already exists.");
        }
        branches2.add(n);
        branches.put(n, getcurrheadname());
        write();
    }

    /**
     * Switch to curr.
     * @param name current name.
     */
    void swi(String name) {
        _curr = name;
    }

    /**
     * get current head.
     * @return currhead
     */
    Commit getcurrhead() {
        String name = branches.get(_curr);
        return Utils.readObject(Utils.join(Main.getn(), name), Commit.class);
    }
    /**
     * Gets current name.
     * @return Current branch.
     */
    String getcurrheadname() {
        return getcurrhead().hash();
    }

    /**
     * Gets current branch.
     * @return Current branch.
     */
    String getcurrbranch() {
        return _curr;
    }

    /**
     * Deletes branch.
     * @param n The name.
     */
    void removebranch(String n) {
        if (!branches.containsKey(n)) {
            throw new GitletException("A branch "
                   + "with that name does not exist.");
        }
        if (n.equals(_curr)) {
            throw new GitletException("Cannot remove the current branch.");
        } else {
            branches.remove(n);
            branches2.remove(n);
        }
        write();
    }

    /**
     * Writes file.
     */
    public void write() {
        File f = Utils.join(Main.getn(), "Tree");
        Utils.writeObject(f, this);
    }

    /**
     * Resets to the branch.
     * @param name Name of branch.
     */
    public void reset(String name) {
        Commit target = Main.getcommit(name);
        Commit curr = this.getcurrhead();
        Iterator<String> iter = target.blobs().iterator();
        while (iter.hasNext()) {
            String s = iter.next();
            Blob atcomm = Main.getblob(s);
            if (!curr.nameexists(atcomm.name())
                    & (new File(atcomm.name())).exists()) {
                throw new GitletException("There is an untracked fil"
                      +  "e in the way; delete it or add and commit it first.");
            }
            Utils.writeContents(new File(atcomm.name()), atcomm.contents());
        }
        iter = curr.blobnameiter();
        while (iter.hasNext()) {
            String s = iter.next();
            Blob atcomm = Main.getblob(s);
            if (!target.blobsnames().containsKey(atcomm.name())) {
                File tar = new File(atcomm.name());
                tar.delete();
            }
        }
        HashSet<String> mem = Index.addread();
        iter = mem.iterator();
        while (iter.hasNext()) {
            String s = iter.next();
            File blobfile = Utils.join(Main.getn(), s);
            Blob bl = Utils.readObject(blobfile, Blob.class);
            Utils.join(Main.gets(), bl.name()).delete();
        }
        HashSet<String> remove = Index.remread();
        iter = remove.iterator();
        while (iter.hasNext()) {
            String rem = iter.next();
            Utils.join(Main.getr(), rem).delete();
        }
        Index.clear();
        Index.remwrite();
        Index.addwrite();
    }

    /**
     * Finds the ancestor.
     * @param n ancestorstring.
     * @return The nearest ancestor.
     */
    public Commit ancestor(String n) {
        HashSet<String> branchids = new HashSet<>();
        ArrayList<Commit> branch = new ArrayList<>();
        ArrayList<Commit> curr = new ArrayList<>();
        Commit c = Main.getcommit(branches.get(n));
        branch.add(c);
        while (c != null) {
            if (!branchids.contains(c.hashcode())) {
                branchids.add(c.hashcode());
                if (c.prev() == null) {
                    break;
                }
                if (!branchids.contains(c.prev().hashCode())) {
                    branch.add(Main.getcommit(c.prev()));
                }
                if (c.pm() != null) {
                    if (!branchids.contains(c.pm().hashCode())) {
                        branch.add(Main.getcommit(c.pm()));
                    }
                }
            }
            if (branch.size() > 0) {
                c = branch.remove(0);

            } else {
                c = null;
            }
        }
        curr.add(getcurrhead());
        c = curr.get(0);
        HashSet<String> visited = new HashSet<>();
        while (true) {
            if (branchids.contains(c.hashcode())) {
                return c;
            }
            if (!visited.contains(c.hashcode())) {
                visited.add(c.hashcode());
                if (c.prev() == null) {
                    return c;
                }
                if (!visited.contains(c.prev().hashCode())) {
                    curr.add(Main.getcommit(c.prev()));
                }
                if (c.pm() != null) {
                    curr.add(Main.getcommit(c.pm()));
                }
            }
            curr.remove(0);
            c = curr.get(0);

        }
    }

    /**
     * extends add commit but adds merge.
     * @param other OTher parent.
     * @param mergeparent The mergeparent.
     */
    public void addmergecommit(String other, Commit mergeparent) {
        String n = "Merged " + other + " into " + _curr + ".";
        addcommit(n, Index.addread(), Index.remread());
        Commit c = Main.getcommit(branches.get(_curr));
        c.setmp(mergeparent.hash());
        c.setm("Merge: " + c.prev().substring(0, 7)
                + " " + mergeparent.hash().substring(0, 7));
        c.tofile(Main.getn());
    }

    /**
     * adds a commit.
     * @param n The commit name
     */
    public void addbr(String n) {
        commits.add(n);
        write();
    }

    /**
     * Adds branch.
     * @param n you get commit by branch.
     * @return Name.
     */
    public Commit getbrcom2(String n) {
        String name = branches.get(n);
        File m = Utils.join(Main.getn(), name);
        return Utils.readObject(m, Commit.class);
    }

    /**
     * Find the string.
     * @param n branch name.
     * @return true or false.
     */
    public boolean hasbranch(String n) {
        return branches.containsKey(n);
    }

    /**
     * Add the resets.
     * @param target The target commit.
     */
    public void reset(Commit target) {
        Commit curr = this.getcurrhead();
        Iterator<String> iter = target.blobnameiter();
        while (iter.hasNext()) {
            String s = iter.next();
            Blob atcomm = Main.getblob(s);
            if (!curr.nameexists(atcomm.name())
                    & (new File(atcomm.name())).exists()) {
                throw new GitletException("There is an untracked fil"
                     +   "e in the way; delet"
                        +  "e it or add and commit it first.");
            }
            Utils.writeContents(new File(atcomm.name()), atcomm.contents());
        }
        iter = curr.blobnameiter();
        while (iter.hasNext()) {
            String s = iter.next();
            Blob atcomm = Main.getblob(s);
            if (!target.blobsnames().containsKey(atcomm.name())) {
                File tar = new File(atcomm.name());
                tar.delete();
            }
        }
        HashSet<String> mem = Index.addread();
        iter = mem.iterator();
        while (iter.hasNext()) {
            String s = iter.next();
            File blobfile = Utils.join(Main.getn(), s);
            Blob bl = Utils.readObject(blobfile, Blob.class);
            Utils.join(Main.gets(), bl.name()).delete();
        }
        HashSet<String> remove = Index.remread();
        iter = remove.iterator();
        while (iter.hasNext()) {
            String rem = iter.next();
            Utils.join(Main.getr(), rem).delete();
        }
        Index.clear();
        Index.remwrite();
        Index.addwrite();
    }

    /**
     * Add commits.
     * @param newhead the new head.
     * @param branch BRanch you add it to.
     */
    void addcommit(Commit newhead, String branch) {
        Commit oldhead = Main.getcommit(branches.get(branch));
        branches.remove(branch, oldhead.hash());
        newhead.setp(oldhead.hash());
        branches.put(branch, newhead.hashcode());
        commits.add(newhead.hashcode());
        abbrev.put(newhead.hashcode().substring(0, 8), newhead.hashcode());
        write();
        newhead.tofile(Main.getn());

    }
}
