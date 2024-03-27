package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    private File f;
    private TupleDesc td;

    public class HeapFileIterator implements DbFileIterator{
        private HeapFile f;
        private TransactionId tid;
        private Iterator<Tuple> tIterator;
        private int pgNo;
    
        public HeapFileIterator(HeapFile f, TransactionId tid){
            this.f = f;
            this.tid = tid;
            this.tIterator = null;
        }
    
        public void open() throws DbException, TransactionAbortedException{
            pgNo = 0;
            tIterator = getTuples(pgNo);
        }
    
        public Iterator<Tuple> getTuples(int pgNo) throws DbException, TransactionAbortedException{
            if(pgNo < 0 || pgNo >= f.numPages()){
                throw new DbException("page" + pgNo + "not valid.");
            }
            HeapPageId pid = new HeapPageId(f.getId(),pgNo);
            HeapPage page = (HeapPage) Database.getBufferPool().getPage(tid,pid,Permissions.READ_ONLY);
            return page.iterator();
        }
    
        public boolean hasNext() throws DbException, TransactionAbortedException{
            if (tIterator == null){
                return false;
            }
            if (tIterator.hasNext()){
                return true;
            }
            else{
                if (pgNo < f.numPages() - 1){
                    pgNo++;
                    tIterator = getTuples(pgNo);
                    return tIterator.hasNext();
                }
                else{
                    return false;
                }
            }
        }
    
        public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException{
            if (tIterator == null || !tIterator.hasNext()){
                throw new NoSuchElementException();
            }
            return tIterator.next();
        }
    
        public void rewind() throws DbException, TransactionAbortedException{
            close();
            open();
        }
        
        public TupleDesc getTupleDesc(){
            return f.getTupleDesc();
        }
    
        public void close(){
            tIterator = null;
        }
    }
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        this.f = f;
        this.td = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return this.f;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        return f.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes herethrows DbException, TransactionAbortedException
        return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        int pgNo = pid.pageNumber();
        int page_size = BufferPool.getPageSize();
        int offset = pgNo * page_size;
        RandomAccessFile random_access_file = null;
        try{
            random_access_file = new RandomAccessFile(f,"r");
            byte[] bytes = new byte[page_size];
            random_access_file.seek(offset);
            random_access_file.read(bytes);
            HeapPage page = new HeapPage((HeapPageId) pid, bytes);
            return page;
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            try{
                if (random_access_file != null){
                    random_access_file.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("page" + pgNo + "doesn't exist.");
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return (int) Math.ceil(f.length() * 1.0 / BufferPool.getPageSize());
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return new HeapFileIterator(this,tid);
    }

}

