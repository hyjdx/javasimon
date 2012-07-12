package org.javasimon.callback.timeline;

import java.util.List;
import org.javasimon.callback.lastsplits.CircularList;

/**
 * Collection of values sorted on a time line
 * @author gerald
 */
public abstract class Timeline<TR extends TimeRange> {
    /**
     * List of time ranges
     */
    protected final List<TR> timeRanges;
    /**
     * Time range width in milliseconds
     */
    protected final long timeRangeWidth;
    /**
     * Last used time range
     */
    private TR lastTimeRange;
    /**
     * Main constructor
     */
    protected Timeline(int capacity, long timeRangeWidth) {
        this.timeRanges = new CircularList<TR>(capacity);
        this.timeRangeWidth = timeRangeWidth;
    }
    /**
     * Create an time range (factory method).
     * @param startTimestamp Time range start
     * @param endTimestamp Time range end
     * @return 
     */
    protected abstract TR createTimeRange(long startTimestamp, long endTimestamp);
    /**
     * Get existing time range if it already exists or create a new one
     * @param timestamp 
     * @return 
     */
    protected TR getOrCreateTimeRange(long timestamp) {
        TR timeRange;
        if (lastTimeRange == null || timestamp > lastTimeRange.getEndTimestamp()) {
            // New time range
            long roundedTimestamp = timestamp -timestamp % timeRangeWidth;
            timeRange=createTimeRange(roundedTimestamp, roundedTimestamp + timeRangeWidth);
            timeRanges.add(timeRange);
            lastTimeRange=timeRange;
        } else if (timestamp >= lastTimeRange.getStartTimestamp()) {
            // Current time range
            timeRange = lastTimeRange;
        } else {
            // Old time range
            timeRange = null;
            // Not very good from performance point of view
            // iterating from end till start would be a better solution
            for(TR oldTimeRange:timeRanges) {
                if (oldTimeRange.containsTimestamp(timestamp)) {
                    timeRange = oldTimeRange;
                    break;
                }
            }
        }
        
        return timeRange;
    }
    public abstract TimelineSample<TR> sample();
}
