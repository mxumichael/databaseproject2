package simpledb.buffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import simpledb.file.*;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
class BasicBufferMgr {
	private Buffer[] bufferpool;
	private int numAvailable;
	HashMap<Block,List<Long>> pageHistory;
	HashMap<Block,Long> last;
	int lru_K_value = 5;
	private static final long lru_correlated_reference_period = 10000; // 10 seconds

	/**
	 * Creates a buffer manager having the specified number 
	 * of buffer slots.
	 * This constructor depends on both the {@link FileMgr} and
	 * {@link simpledb.log.LogMgr LogMgr} objects 
	 * that it gets from the class
	 * {@link simpledb.server.SimpleDB}.
	 * Those objects are created during system initialization.
	 * Thus this constructor cannot be called until 
	 * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or
	 * is called first.
	 * @param numbuffs the number of buffer slots to allocate
	 */
	BasicBufferMgr(int numbuffs) {
		bufferpool = new Buffer[numbuffs];
		numAvailable = numbuffs;
		pageHistory = new HashMap<Block,List<Long>>();
		last = new HashMap<Block,Long>();
		for (int i=0; i<numbuffs; i++)
			bufferpool[i] = new Buffer();
	}

	/**
	 * Flushes the dirty buffers modified by the specified transaction.
	 * @param txnum the transaction's id number
	 */
	synchronized void flushAll(int txnum) {
		for (Buffer buff : bufferpool)
			if (buff.isModifiedBy(txnum))
				buff.flush();
	}

	/**
	 * Pins a buffer to the specified block. 
	 * If there is already a buffer assigned to that block
	 * then that buffer is used;  
	 * otherwise, an unpinned buffer from the pool is chosen.
	 * Returns a null value if there are no available buffers.
	 * @param blk a reference to a disk block
	 * @return the pinned buffer
	 */
	synchronized Buffer pin(Block blk) {

		Buffer buff = findExistingBuffer(blk);
		long currentTime = System.currentTimeMillis();

		if (buff == null) {
			buff = chooseUnpinnedBuffer(currentTime);
			if (buff == null)
				return null;
			buff.assignToBlock(blk);
			if (pageHistory.get(blk) == null){
				pageHistory.put(blk, new ArrayList<Long>());
				List<Long> list = pageHistory.get(blk);
				for (int i = 0; i<lru_K_value; i++)
				{
					list.add((long) 0);
				}
			}
			else{

				List<Long> bufferAccessHistory= pageHistory.get(blk);
//				bufferAccessHistory.add(0,(long) 0);
				for (int i = lru_K_value-1; i>=1; i-- )
				{
					bufferAccessHistory.set(i, bufferAccessHistory.get(i-1));
				} 
				if (bufferAccessHistory.size() > lru_K_value) {//trimming the history to size K
					bufferAccessHistory.remove(lru_K_value);
				}

			}
			pageHistory.get(blk).set(0, new Long(currentTime));
			last.put(blk, currentTime);

		}
		else
		{  /*noting the time that the buffer was accessed.*/
			List<Long> bufferAccessHistory= pageHistory.get(blk);
			if (currentTime - last.get(blk) > lru_correlated_reference_period){
				long correl_period_of_refd_page = last.get(blk) - bufferAccessHistory.get(0); 
				for (int i = lru_K_value-1; i>=1; i-- )
				{
					bufferAccessHistory.set(i, bufferAccessHistory.get(i-1)+correl_period_of_refd_page);
				}
				bufferAccessHistory.set(0, currentTime);
				last.put(blk, currentTime);
			}
			else{
				last.put(blk, currentTime);
			}
			if (bufferAccessHistory.size() > lru_K_value) {//trimming the history to size K
				bufferAccessHistory.remove(lru_K_value);
			}
		}
		if (!buff.isPinned())
			numAvailable--;
		buff.pin();

		return buff;
	}

	/**
	 * Allocates a new block in the specified file, and
	 * pins a buffer to it. 
	 * Returns null (without allocating the block) if 
	 * there are no available buffers.
	 * @param filename the name of the file
	 * @param fmtr a pageformatter object, used to format the new block
	 * @return the pinned buffer
	 */
	synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
		Buffer buff = chooseUnpinnedBuffer();
		if (buff == null)
			return null;
		buff.assignToNew(filename, fmtr);
		numAvailable--;
		buff.pin();
		return buff;
	}

	private Buffer chooseUnpinnedBuffer() {
		return chooseUnpinnedBuffer(System.currentTimeMillis());
	}

	/**
	 * Unpins the specified buffer.
	 * @param buff the buffer to be unpinned
	 */
	synchronized void unpin(Buffer buff) {
		buff.unpin();
		if (!buff.isPinned())
			numAvailable++;
	}

	/**
	 * Returns the number of available (i.e. unpinned) buffers.
	 * @return the number of available buffers
	 */
	int available() {
		return numAvailable;
	}

	private Buffer findExistingBuffer(Block blk) {
		for (Buffer buff : bufferpool) {
			Block b = buff.block();
			if (b != null && b.equals(blk))
				return buff;
		}
		return null;
	}

	/**
	 * assumes that the block is not already in a buffer.
	 * @return
	 */
	private Buffer chooseUnpinnedBuffer(long t) {
		long min = t;
		Buffer victim;
		victim = null;
		for (Buffer buff : bufferpool)
			if (!buff.isPinned())
			{
				if (last.get(buff.block()) == null){
					return buff;//buffer has never been used before, so you just use it.
				}
				if (t-last.get(buff.block())>lru_correlated_reference_period 
						&& pageHistory.get(buff.block()).get(lru_K_value-1)<min)
				{
					victim = buff;
					min = pageHistory.get(buff.block()).get(lru_K_value-1);
				}
			}		
		return victim;
	}
	private Buffer chooseUnpinnedBuffer2() { //the original
		for (Buffer buff : bufferpool)
			if (!buff.isPinned())
				return buff;
		return null;
	}

}

