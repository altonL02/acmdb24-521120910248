package simpledb;

import java.util.*;

/**
 * Filter is an operator that implements a relational select.
 */
public class Filter extends Operator {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor accepts a predicate to apply and a child operator to read
     * tuples to filter from.
     * 
     * @param p
     *            The predicate to filter tuples with
     * @param child
     *            The child operator
     */

    /*My Implementation Start*/
    private Predicate predicate;
    private DbIterator childIterator;
    /*My Implementation End*/

    public Filter(Predicate p, DbIterator child) {
        // some code goes here
        this.predicate = p;
        this.childIterator = child;
    }

    public Predicate getPredicate() {
        // some code goes here
        return this.predicate;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return this.childIterator.getTupleDesc();
    }

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
        super.open();
        this.childIterator.open();
    }

    public void close() {
        // some code goes here
        this.childIterator.close();
        super.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        this.childIterator.rewind();
    }

    /**
     * AbstractDbIterator.readNext implementation. Iterates over tuples from the
     * child operator, applying the predicate to them and returning those that
     * pass the predicate (i.e. for which the Predicate.filter() returns true.)
     * 
     * @return The next tuple that passes the filter, or null if there are no
     *         more tuples
     * @see Predicate#filter
     */
    protected Tuple fetchNext() throws NoSuchElementException,
            TransactionAbortedException, DbException {
        // some code goes here

        while (childIterator.hasNext())
        {
            Tuple tmpTuple = childIterator.next();
            if (this.predicate.filter(tmpTuple))
            {
                return tmpTuple;
            }

        }
        return null;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        DbIterator[] resultArray = new DbIterator[1];
        resultArray[0] = this.childIterator;
        return resultArray;
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        this.childIterator = children[0];
    }

}
