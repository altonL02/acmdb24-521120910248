package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    /*My Implementation Start*/
    private int gbField;
    private Type gbFieldType;
    private int aggreField;
    private Op aggreOperator;
    private int result;
    private int count = 0;
    private HashMap<Field, Integer> gbResultHashMap;
    private HashMap<Field, Integer> gbCountHashMap;
    private TupleDesc resultTupleDesc = null;
    /*My Implementation End*/

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        this.gbField = gbfield;
        this.gbFieldType = gbfieldtype;
        this.aggreField = afield;
        this.aggreOperator = what;
        this.gbResultHashMap = new HashMap<Field, Integer>();
        this.gbCountHashMap = new HashMap<Field, Integer>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here

        int aggreField = ((IntField)tup.getField(this.aggreField)).getValue();
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
            if (!this.gbCountHashMap.containsKey(gbField))
            {
                this.gbCountHashMap.put(gbField, 1);
            }
            else
            {
                int cnt = this.gbCountHashMap.get(gbField) + 1;
                this.gbCountHashMap.put(gbField, cnt);
            }
            if (!this.gbResultHashMap.containsKey(gbField))
            {
                this.gbResultHashMap.put(gbField, aggreField);
            }
            else
            {
                switch (this.aggreOperator)
                {
                    case MIN:
                        this.gbResultHashMap.put(gbField, min(this.gbResultHashMap.get(gbField), aggreField));
                        break;
                    case MAX:
                        this.gbResultHashMap.put(gbField, max(this.gbResultHashMap.get(gbField), aggreField));
                        break;
                    case AVG:
                    case SUM:
                        this.gbResultHashMap.put(gbField, this.gbResultHashMap.get(gbField) + aggreField);
                        break;
                    case COUNT:
                        this.gbResultHashMap.put(gbField, this.gbCountHashMap.get(gbField));
                        break;
                    default:
                        break;
                }
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
            switch (this.aggreOperator)
            {
                case MIN:
                    this.result = min(this.result, aggreField);
                    break;
                case MAX:
                    this.result = max(this.result, aggreField);
                    break;
                case AVG:
                case SUM:
                    this.result = this.result + aggreField;
                    break;
                case COUNT:
                    this.result = this.count;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Create a DbIterator over group aggregate results.
     * 
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        ArrayList<Tuple> resultList = new ArrayList<Tuple>();
        if (this.gbField != NO_GROUPING)
        {
            for (Map.Entry<Field, Integer> entry : this.gbResultHashMap.entrySet())
            {
                Tuple tmpTuple = new Tuple(this.resultTupleDesc);
                if (this.aggreOperator == Op.AVG)
                {
                    tmpTuple.setField(0, entry.getKey());
                    tmpTuple.setField(1, new IntField(entry.getValue() / this.gbCountHashMap.get(entry.getKey())));
                }
                else
                {
                    tmpTuple.setField(0, entry.getKey());
                    tmpTuple.setField(1, new IntField(entry.getValue()));
                }
                resultList.add(tmpTuple);
            }
        }
        else
        {
            Tuple tmpTuple = new Tuple(this.resultTupleDesc);
            if (this.aggreOperator == Op.AVG)
            {
                tmpTuple.setField(0, new IntField(this.result / this.count));
            }
            else
            {
                tmpTuple.setField(0, new IntField(this.result));
            }
            resultList.add(tmpTuple);
        }
        return new TupleIterator(this.resultTupleDesc, resultList);
    }

}
