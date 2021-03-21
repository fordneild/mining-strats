package edu.stanford.crypto.cs251.miners;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.crypto.cs251.blockchain.Block;
import edu.stanford.crypto.cs251.blockchain.NetworkStatistics;

public class MajorityMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private List<Block> myOldBlocks;

    public MajorityMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
        myOldBlocks = new ArrayList<Block>();

    }

    @Override
    public Block currentlyMiningAt() {
        if(this.myOldBlocks.size() > 0){
            return this.myOldBlocks.get(this.myOldBlocks.size()-1);
        }
        return currentHead;
    }

@Override
public Block currentHead() {
    return currentHead;
}



    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if(isMinerMe) {
            myOldBlocks.add(block);
            if (block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
            }
        }else{
            if (currentHead == null) {
                currentHead = block;
            } else if (block != null && block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
    
            }
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;

    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
    }
}
