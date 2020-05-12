package gitlet;


import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Nathan Zhang
 */
@SuppressWarnings("serial")



public class Commit implements Serializable {
    /**
     * List of all blobs for iteration.
     */
    private HashSet<String> blobs = new HashSet<>();

    /**
     * gets blobs.
     * @return blobs.
     */
    public HashSet<String> blobs() {
        return blobs;
    }
    /**
     * mergeparent.
     */
    private String prevmerge = null;
    /**
     * Sets prevmerge.
     * @return prevmerge.
     */
    public String pm() {
        return prevmerge;
    }

    /**
     * Sets prevmerge.
     * @param p target.
     */
    public void setmp(String p) {
        prevmerge = p;
    }
    /**
     * Sets merge.
     * @param p target.
     */
    public void setm(String p) {
        merge = p;
    }
    /**
     * Maps blobnames to contents.
     */
    private HashMap<String, String> blobsnames = new HashMap<String, String>();

    /**
     * puts stuff in bnames.
     * @param m key.
     * @param nm object.
     */
    public void bnput(String m, String nm) {
        blobsnames.put(m, nm);
    }
    /**
     * Maps blobnames to contents.
     * @return  blobsnames.
     */
    public HashMap<String, String> blobsnames() {
        return blobsnames;
    }
    /**
     * Message.
     */
    private String msg;

    /**
     * Get the message.
     * @return message.
     */
    public String msg() {
        return msg;
    }
    /**
     * List of all blobs for iteration.
     */
    private DateFormat format;
    /**
     * The date.
     */
    private Date d;
    /**
     *  string formate.
     */
    private String n;

    /**
     * Get time.
     * @return time.
     */
    public String n() {
        return n;
    }
    /**
     * string time.
     */
    private String hash;
    /**
     * did it merge.
     * @return merge.
     */
    public String hash() {
        return hash;
    }

    /**
     * did it merge.
     */
    private String merge;
    /**
     * did it merge.
     * @return merge.
     */
    public String merge() {
        return merge;
    }
    /**
     * previous.
     */
    private String prev;
    /**
     * did it merge.
     * @return merge.
     */
    public String prev() {
        return prev;
    }

    /**
     * Set prev as p.
     * @param p custom.
     */
    public void setp(String p) {
        prev = p;
    }

    /**
     * Lists all the blobs out.
     */
    private String listofblobs = "";

    /**
     * lob.
     * @return List of blobs.
     */
    public String lob() {
        return listofblobs;
    }

    /**
     * Another version of commit.
     */
    public Commit() {
        this("initial commit");
    }

    /**
     * Commits the string.
     * @param m M is the name.
     */
    public Commit(String m) {
        format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        d = new Timestamp(System.currentTimeMillis());
        n = format.format(d);
        msg = m;
        hash = hashcode();
    }

    /**
     * Add th bob.
     * @param nm name.
     */
    public void addblob(String nm) {
        blobs.add(nm);
        listofblobs += Main.getblob(nm).name();
    }

    /**
     * Hashcode of blob.
     * @return the hash.
     */
    public String hashcode() {
        hash = Utils.sha1(n.toString() + msg + listofblobs);
        return hash;
    }

    /**
     * To file the blob.
     * @param k destination.
     */
    void tofile(File k) {
        File nm = Utils.join(k, hashcode());
        Utils.writeObject(nm, this);
    }

    /**
     * Get contents of blob.
     * @param nm the name.
     * @return contnets.
     */
    String getblobcontents(String nm) {
        if (!blobsnames.containsKey(nm)) {
            return "";
        } else {
            return blobsnames.get(nm);
        }
    }

    /**
     * Does a name exist?
     * @param nm name.
     * @return whether it exist.
     */
    boolean nameexists(String nm) {
        return blobsnames.containsKey(nm);
    }

    /**
     * An iterator of blobs.
     * @return The iterator.
     */
    Iterator<String> blobnameiter() {
        return this.blobs.iterator();
    }
}
