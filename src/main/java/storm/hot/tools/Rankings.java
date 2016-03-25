package storm.hot.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.arnx.jsonic.JSON;

public class Rankings implements Serializable {

  private static final long serialVersionUID = -1549827195410578903L;
  private static final int DEFAULT_COUNT = 10;

  private final int maxSize;
  private final List<Rankable> rankedItems = Lists.newArrayList();

  public Rankings() {
    this(DEFAULT_COUNT);
  }

  public Rankings(int topN) {
    if (topN < 1) {
      throw new IllegalArgumentException("topN must be >= 1");
    }
    maxSize = topN;
  }

  /**
   * Copy constructor.
   * @param other
   */
  public Rankings(Rankings other) {
    this(other.maxSize());
    updateWith(other);
  }

  /**
   * @return the maximum possible number (size) of ranked objects this instance can hold
   */
  public int maxSize() {
    return maxSize;
  }

  /**
   * @return the number (size) of ranked objects this instance is currently holding
   */
  public int size() {
    return rankedItems.size();
  }

  /**
   * The returned defensive copy is only "somewhat" defensive.  We do, for instance, return a defensive copy of the
   * enclosing List instance, and we do try to defensively copy any contained Rankable objects, too.  However, the
   * contract of {@link storm.starter.tools.Rankable#copy()} does not guarantee that any Object's embedded within
   * a Rankable will be defensively copied, too.
   *
   * @return a somewhat defensive copy of ranked items
   */
  public List<Rankable> getRankings() {
    List<Rankable> copy = Lists.newLinkedList();
    for (Rankable r: rankedItems) {
      copy.add(r.copy());
    }
    return ImmutableList.copyOf(copy);
  }

  public void updateWith(Rankings other) {
    for (Rankable r : other.getRankings()) {
      updateWith(r);
    }
  }

  public void updateWith(Rankable r) {
    synchronized(rankedItems) {
      addOrReplace(r);
      rerank();
      shrinkRankingsIfNeeded();
    }
  }

  private void addOrReplace(Rankable r) {
    Integer rank = findRankOf(r);
    if (rank != null) {
      rankedItems.set(rank, r);
    }
    else {
      rankedItems.add(r);
    }
  }

  private Integer findRankOf(Rankable r) {
    Object tag = r.getObject();
    for (int rank = 0; rank < rankedItems.size(); rank++) {
      Object cur = rankedItems.get(rank).getObject();
      if (cur.equals(tag)) {
        return rank;
      }
    }
    return null;
  }

  private void rerank() {
    Collections.sort(rankedItems);
    Collections.reverse(rankedItems);
  }

  private void shrinkRankingsIfNeeded() {
    if (rankedItems.size() > maxSize) {
      rankedItems.remove(maxSize);
    }
  }

  /**
   * Removes ranking entries that have a count of zero.
   */
  public void pruneZeroCounts() {
    int i = 0;
    while (i < rankedItems.size()) {
      if (rankedItems.get(i).getCount() == 0) {
        rankedItems.remove(i);
      }
      else {
        i++;
      }
    }
  }

  public String toString() {
    return rankedItems.toString();
  }


  public void jsonLog() {

    String jsonlog = "";
    List<Map<String,Object>> jsonList = new ArrayList<Map<String, Object>>();

    List<Map<String,Object>> wordcountList = new ArrayList<Map<String, Object>>();
    Map<String,Object> jsonMap = new LinkedHashMap<String,Object>();


    for(int i=0;i<rankedItems.size();i++){
      Map<String,Object> wordcount = new LinkedHashMap<String, Object>();
      wordcount.put("name" , (String) rankedItems.get(i).getObject());
      wordcount.put("count" , String.valueOf(rankedItems.get(i).getCount()));
      wordcountList.add(wordcount);
    }

    jsonMap.put("success", true);
    jsonMap.put("result",wordcountList);
    jsonlog = JSON.encode(jsonMap);
    System.out.println(jsonlog);

    try {
        String home = System.getProperty("user.home");
        File file = new File(home+"/data");

        if (!file.exists()) {
           file.mkdirs();
         }

    	File jsonfile = new File(file+"/data.json");

        FileWriter filewriter = new FileWriter(jsonfile);
        filewriter.write(jsonlog);
        filewriter.close();

    	} catch (FileNotFoundException er) {
    		er.printStackTrace();
	    } catch (IOException er) {
	    	er.printStackTrace();
	  }
  }




  /**
   * Creates a (defensive) copy of itself.
   */
  public Rankings copy() {
    return new Rankings(this);
  }
}
