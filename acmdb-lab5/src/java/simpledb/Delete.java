package simpledb;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;

    /*My Implementation Start*/
    private TransactionId tid;
    private DbIterator child;
    private TupleDesc tupleDesc;
    private boolean isCalled = false;
    /*My Implementation End*/

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
        this.tid = t;
        this.child = child;
        Type[] tmpArray = new Type[1];
        tmpArray[0] = Type.INT_TYPE;
        this.tupleDesc = new TupleDesc(tmpArray);
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return this.tupleDesc;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        this.child.open();
        super.open();
    }

    public void close() {
        // some code goes here
        super.close();
        this.child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        this.child.rewind();
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        if (this.isCalled)
        {
            return null;
        }
        else
        {
            int count = 0;
            int cnt = 0;
            ArrayList<Tuple> arrayList = new ArrayList<Tuple>();
            while (child.hasNext())
                arrayList.add(child.next());
            for (Tuple deletingTuple : arrayList)
            {
                cnt++;
                try
                {
                    Database.getBufferPool().deleteTuple(this.tid, deletingTuple);
                    count++;
                } catch (IOException ioException)
                {
                    throw new DbException("IOException happened when inserting Tuple in fetchNext() \n");
                }
            }
            Tuple resTuple = new Tuple(this.tupleDesc);
            resTuple.setField(0, new IntField(count));
            this.isCalled = true;
            return resTuple;
        }
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        DbIterator[] children = new DbIterator[1];
        children[0] = child;
        return children;
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        this.child = children[0];
    }

}
