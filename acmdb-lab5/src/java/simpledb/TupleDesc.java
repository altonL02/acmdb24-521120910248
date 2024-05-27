package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }

        /*My implementation Start*/
        // Redefine the equals function for TDItem.
        public boolean equals(TDItem tdItem)
        {
            return (this.fieldType == tdItem.fieldType);
        }
        /*My implementation End*/
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here
        return tdItemList.iterator();
    }

    private static final long serialVersionUID = 1L;

    /*My implementation Start*/
    private ArrayList<TDItem> tdItemList;
    /*My implementation End*/

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
        if (typeAr.length != fieldAr.length)
        {
            System.err.println("In TupleDesc: size of typeAr and fieldAr not match!");
        }
        this.tdItemList = new ArrayList<TDItem>(typeAr.length);
        for(int i = 0; i < typeAr.length; i++)
        {
            tdItemList.add(new TDItem(typeAr[i], fieldAr[i]));
        }
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
        this.tdItemList = new ArrayList<TDItem>(typeAr.length);
        for (Type type : typeAr)
        {
            tdItemList.add(new TDItem(type, ""));
        }
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return this.tdItemList.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
        if (i < 0 || i >= this.tdItemList.size())
            throw new NoSuchElementException();
        return this.tdItemList.get(i).fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // some code goes here
        if (i < 0 || i >= this.tdItemList.size())
            throw new NoSuchElementException("NoSuchElementException in TupleDesc getFieldType()");
        return this.tdItemList.get(i).fieldType;
    }

    /*My implementation Start*/
    /**
     * Gets the ith TDItem of this TupleDesc.
     *
     * @param i
     *            The index of the TDItem to get. It must be a valid
     *            index.
     * @return the ith
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public TDItem getTDItem(int i) throws NoSuchElementException {
        // some code goes here
        if (i < 0 || i >= this.tdItemList.size())
            throw new NoSuchElementException("NoSuchElementException in TupleDesc getFieldType()");
        return this.tdItemList.get(i);
    }
    /*My implementation End*/

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
        for (int i = 0; i < tdItemList.size(); i++)
        {
            if (tdItemList.get(i).fieldName.equals(name))
                return i;
        }
        throw new NoSuchElementException("NoSuchElementException in TupleDesc fieldNameToIndex()");
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
        int size = 0;
        for (TDItem tdItem : tdItemList)
            size += tdItem.fieldType.getLen();
        return size;
    }

    /*My implementation Start*/
    /**
     * @return The length of the TupleDesc.
     */
    public int getLength() {
        // some code goes here
        return tdItemList.size();
    }
    /*My implementation End*/

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here

        int totalSize = td1.getLength() + td2.getLength();

        Type[] typeAr = new Type[totalSize];
        String[] fieldAr = new String[totalSize];

        for (int i = 0; i < totalSize; i++)
        {
            if (i < td1.getLength())
            {
                typeAr[i] = td1.getFieldType(i);
                fieldAr[i] = td1.getFieldName(i);
            }
            else
            {
                typeAr[i] = td2.getFieldType(i - td1.getLength());
                fieldAr[i] = td2.getFieldName(i - td1.getLength());
            }
        }

        return new TupleDesc(typeAr, fieldAr);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here
        if (!(o instanceof TupleDesc))
            return false;
        TupleDesc objTupleDesc = (TupleDesc) o;
        if (objTupleDesc.getLength() != this.getLength())
            return false;
        for (int i = 0; i < this.getLength(); i++)
        {
            if (!objTupleDesc.getTDItem(i).equals(this.tdItemList.get(i)))
                return false;
        }
        return true;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        return this.tdItemList.hashCode();
//        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        StringBuilder resultBuilder = new StringBuilder();
        for(TDItem tdItem : tdItemList)
        {
            resultBuilder.append(tdItem.fieldType.toString()).append("(").append(tdItem.fieldName).append("), ");
        }
        return resultBuilder.substring(0, resultBuilder.length() - 2);
    }
}
