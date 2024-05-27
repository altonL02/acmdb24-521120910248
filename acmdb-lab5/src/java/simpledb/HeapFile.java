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

    /*My implementation Start*/
    File rawFile;
    TupleDesc tupleDesc;

    private class HeapFileTupleIterator implements DbFileIterator
    {

        private int pageOffset;
        private HeapPage currentPage;
        private Iterator<Tuple> currentTupleIterator;
        private TransactionId transactionId;
        private int pageCounter = 0;

        public HeapFileTupleIterator(TransactionId tid)
        {
            this.transactionId = tid;
            this.pageCounter = numPages();
        }

        @Override
        public void open() throws DbException, TransactionAbortedException
        {
            this.pageOffset = 0;
            this.currentPage = (HeapPage) Database.getBufferPool()
                    .getPage(this.transactionId, new HeapPageId(getId(), this.pageOffset), Permissions.READ_ONLY);
            this.currentTupleIterator = currentPage.iterator();
        }

        @Override
        public boolean hasNext() throws DbException, TransactionAbortedException
        {
            if (this.currentPage == null || this.currentTupleIterator == null)
                return false;
            if (this.currentTupleIterator.hasNext())
                return true;
            else
            {
                int tmpOffset = pageOffset + 1;
                while (tmpOffset < pageCounter)
                {
                    HeapPage currentPage = (HeapPage) Database.getBufferPool()
                            .getPage(this.transactionId, new HeapPageId(getId(), tmpOffset), Permissions.READ_ONLY);
                    Iterator<Tuple> currentTupleIterator = currentPage.iterator();
                    if (currentTupleIterator.hasNext())
                        return true;
                    tmpOffset++;
                }
            }
            return false;
        }

        @Override
        public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException
        {
            if (this.currentTupleIterator == null || this.currentPage == null)
                throw new NoSuchElementException("HeapFile Iterator: Iterator is closed!\n");
            if (this.currentTupleIterator.hasNext())
                return this.currentTupleIterator.next();
            else
            {
                this.pageOffset++;
                while (this.pageOffset < this.pageCounter)
                {
                    this.currentPage = (HeapPage) Database.getBufferPool()
                            .getPage(this.transactionId, new HeapPageId(getId(), this.pageOffset), Permissions.READ_ONLY);
                    this.currentTupleIterator = currentPage.iterator();
                    if (this.currentTupleIterator.hasNext())
                        return this.currentTupleIterator.next();
                    this.pageOffset++;
                }
            }
            throw new NoSuchElementException("HeapFile Iterator: No Next Tuple!\n");
        }

        @Override
        public void rewind() throws DbException, TransactionAbortedException
        {
            this.open();
        }

        @Override
        public void close()
        {
            this.pageOffset = 0;
            this.pageCounter = 0;
            this.currentPage = null;
            this.currentTupleIterator = null;
        }
    }

    /*My implementation End*/

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        this.rawFile = f;
        this.tupleDesc = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return this.rawFile;
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
//        throw new UnsupportedOperationException("implement this");
        return this.rawFile.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
//        throw new UnsupportedOperationException("implement this");
        return this.tupleDesc;
    }

    // see DbFile.java for javadocs
    public synchronized Page readPage(PageId pid) {
        // some code goes here
        byte[] pageBuffer = new byte[BufferPool.getPageSize()];
        Page targetPage = null;
        try
        {
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.rawFile,"r");
            randomAccessFile.seek(pid.pageNumber() * BufferPool.getPageSize());
            randomAccessFile.read(pageBuffer, 0, BufferPool.getPageSize());
            randomAccessFile.close();
            targetPage = new HeapPage((HeapPageId) pid, pageBuffer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return targetPage;
    }

    // see DbFile.java for javadocs
    public synchronized void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1

        int pageNumber = page.getId().pageNumber();

        byte[] pageData = page.getPageData();
        RandomAccessFile randomAccessFile = new RandomAccessFile(this.rawFile,"rw");
        randomAccessFile.seek(pageNumber * BufferPool.getPageSize());
        randomAccessFile.write(pageData, 0, BufferPool.getPageSize());
        randomAccessFile.close();
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return (int) Math.ceil(rawFile.length() / BufferPool.getPageSize());
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1

        if(!this.tupleDesc.equals(t.getTupleDesc()))
        {
            throw new DbException("TupleDesc mismatch in HeapFile insertTuple()! \n");
        }

        ArrayList<Page> modifiedPageList = new ArrayList<Page>();
        int pageCounter = numPages();
        for (int i = 0; i < pageCounter; i++)
        {
            PageId pid = new HeapPageId(getId(), i);
            HeapPage tmpPage = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
            try
            {
                tmpPage.insertTuple(t);
                modifiedPageList.add(tmpPage);
                return modifiedPageList;
            }
            catch (DbException dbException)
            {
            }
        }
        HeapPage newPage = new HeapPage(new HeapPageId(getId(), pageCounter), HeapPage.createEmptyPageData());
        writePage(newPage);
        newPage = (HeapPage)Database.getBufferPool().getPage(tid, newPage.getId(), Permissions.READ_WRITE);
        newPage.insertTuple(t);
        modifiedPageList.add(newPage);
        return modifiedPageList;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
        if(!this.tupleDesc.equals(t.getTupleDesc()))
        {
            throw new DbException("TupleDesc mismatch in HeapFile insertTuple()! \n");
        }

        ArrayList<Page> modifiedPageList = new ArrayList<Page>();
        RecordId recordId = t.getRecordId();
        HeapPage targetPage = (HeapPage) Database.getBufferPool().getPage(tid, recordId.getPageId(), Permissions.READ_WRITE);
        targetPage.deleteTuple(t);
        modifiedPageList.add(targetPage);
        return modifiedPageList;
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return new HeapFileTupleIterator(tid);
    }

}

