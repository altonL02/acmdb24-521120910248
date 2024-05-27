package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    /*My Implementation Start*/
    private int gbField;
    private Type gbFieldType;
    private int aggreField;
    private Op aggreOperator;
    private int count = 0;
    private HashMap<Field, Integer> gbResultHashMap;
    private TupleDesc resultTupleDesc = null;
    /*My Implementation End*/

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        this.gbField = gbfield;
        this.gbFieldType = gbfieldtype;
        this.aggreField = afield;
        this.aggreOperator = what;
        this.gbResultHashMap = new HashMap<Field, Integer>();
        if (what != Op.COUNT)
            throw new IllegalArgumentException("Op is not count in StringAggregator! \n");
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here

        if (this.gbField != NO_GROUPING)
        {
            Field gbField = tup.getField(this.gbField);
            if (this.resultTupleDesc == null)
            {
                Type[] groupedTypes = new Type[2];
                String[] groupedNames = new String[2];
                groupedTypes[0] = this.gbFieldType;
                groupedTypes[1] = Type.INT_TYPE;
                groupedNames[0] = tup.getTupleDesc().getFieldName(this.gbField);
                groupedNames[1] = tup.getTupleDesc().getFieldName(this.aggreField);
                this.resultTupleDesc = new TupleDesc(groupedTypes, groupedNames);
            }
            if (!this.gbResultHashMap.containsKey(gbField))
            {
                this.gbResultHashMap.put(gbField, 1);
            }
            else
            {
                int cnt = this.gbResultHashMap.get(gbField) + 1;
                this.gbResultHashMap.put(gbField, cnt);
            }
        }
        else
        {
            if (this.resultTupleDesc == null)
            {
                Type[] groupedTypes = new Type[1];
                String[] groupedNames = new String[1];
                groupedTypes[0] = Type.INT_TYPE;
                groupedNames[0] = tup.getTupleDesc().getFieldName(this.aggreField);
                this.resultTupleDesc = new TupleDesc(groupedTypes, groupedNames);
            }
            this.count += 1;
        }
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        ArrayList<Tuple> resultList = new ArrayList<Tuple>();
        if (this.gbField != NO_GROUPING)
        {
            for (Map.Entry<Field, Integer> entry : this.gbResultHashMap.entrySet())
            {
                Tuple tmpTuple = new Tuple(this.resultTupleDesc);
                tmpTuple.setField(0, entry.getKey());
                tmpTuple.setField(1, new IntField(entry.getValue()));
                resultList.add(tmpTuple);
            }
        }
        else
        {
            Tuple tmpTuple = new Tuple(this.resultTupleDesc);
            tmpTuple.setField(0, new IntField(this.count));
            resultList.add(tmpTuple);
        }
        return new TupleIterator(this.resultTupleDesc, resultList);
    }

}
