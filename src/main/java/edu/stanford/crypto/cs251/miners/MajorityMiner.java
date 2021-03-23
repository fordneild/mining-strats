package edu.stanford.crypto.cs251.miners;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.crypto.cs251.blockchain.Block;
import edu.stanford.crypto.cs251.blockchain.NetworkStatistics;

public class MajorityMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private List<Block> myMinedBlocks;
    private NetworkStatistics curNetStats;

    public MajorityMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
        myMinedBlocks = new ArrayList<Block>();

    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }



    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if(isMinerMe) {
            //I am the miner and majority
            if (((double) this.getHashRate() / curNetStats.getTotalHashRate()) > .50) {
                myMinedBlocks.add(block);
                this.currentHead = myMinedBlocks.get(myMinedBlocks.size() - 1);
            } else {
                if (block.getHeight() > currentHead.getHeight()) {
                    this.currentHead = block;
                }
            }
        } else {
            if (currentHead == null) {
                currentHead = block;
            } else if (block != null && ((double) this.getHashRate() / curNetStats.getTotalHashRate() > .50)) {
                //temporarily reject block
            } else if (block != null && block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
            }
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        this.curNetStats = networkStatistics;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        this.curNetStats = statistics;
    }
}
